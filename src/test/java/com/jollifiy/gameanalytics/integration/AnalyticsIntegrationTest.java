package com.jollifiy.gameanalytics.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jollifiy.gameanalytics.dto.AnalyticsRequest;
import com.jollifiy.gameanalytics.entity.Analytics;
import com.jollifiy.gameanalytics.repository.AnalyticsRepository;
import com.jollifiy.gameanalytics.service.AnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AnalyticsIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AnalyticsRepository analyticsRepository;

    @Mock
    private AnalyticsRepository analyticsServiceMockRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        analyticsRepository.deleteAll();
    }

    @Test
    @WithMockUser
    @DisplayName("GET /analytics - Veritabanindaki tüm kayıtlar dönmeli")
    void shouldReturnAllAnalyticsFromDatabase() throws Exception {
        Analytics event1 = new Analytics();
        event1.setPlayerId("player-1");
        event1.setEventName("game_start");

        analyticsRepository.saveAll(List.of(event1));

        mockMvc.perform(get("/analytics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].playerId", is("player-1")));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /analytics/send - Istek atildiginda veritabanina kaydedilmeli")
    void shouldSaveAnalyticsToDatabase() throws Exception {
        AnalyticsRequest request = new AnalyticsRequest();
        request.setPlayerId("player-uuid-999");
        request.setEventName("level_complete");

        mockMvc.perform(post("/analytics/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())) // <-- Eklendi
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerId", is("player-uuid-999")))
                .andExpect(jsonPath("$.eventName", is("level_complete")));

        List<Analytics> allEvents = analyticsRepository.findAll();
        assertEquals(1, allEvents.size());
        assertEquals("player-uuid-999", allEvents.get(0).getPlayerId());
    }

    @Test
    @DisplayName("[Unit] Gecerli analytics istegi geldiginde veri veritabanina kaydedilmeli")
    void logEvent_ShouldSaveAnalytics_WhenRequestIsValid() {
        AnalyticsRequest request = new AnalyticsRequest();
        request.setPlayerId("player-uuid-123");
        request.setEventName("level_complete");
        request.setLevel(1);
        request.setScore(150);
        request.setCoinCount(20);
        request.setPlayTime(120.5);

        when(analyticsServiceMockRepository.save(any(Analytics.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Analytics result = analyticsService.saveEvent(request);

        assertNotNull(result, "Kaydedilen analitik nesnesi null olmamalı");
        assertEquals("player-uuid-123", result.getPlayerId(), "Player ID eşleşmeli");
        assertEquals("level_complete", result.getEventName(), "Event adı eşleşmeli");

        verify(analyticsServiceMockRepository, times(1)).save(any(Analytics.class));
    }

    @Test
    @DisplayName("[Unit] getAllEvents cagirildiginda tum analitik kayitlari liste olarak donmeli")
    void getAllEvents_ShouldReturnListOfAnalytics() {
        Analytics analytics1 = new Analytics();
        analytics1.setPlayerId("player-1");
        analytics1.setEventName("level_start");

        Analytics analytics2 = new Analytics();
        analytics2.setPlayerId("player-2");
        analytics2.setEventName("level_complete");

        when(analyticsServiceMockRepository.findAll())
                .thenReturn(List.of(analytics1, analytics2));

        List<Analytics> result = analyticsService.getAllEvents();

        assertNotNull(result, "Dönen liste null olmamalı");
        assertEquals(2, result.size(), "2 adet analitik kaydı dönmeli");
        assertEquals("player-1", result.get(0).getPlayerId());

        verify(analyticsServiceMockRepository, times(1)).findAll();
    }
}