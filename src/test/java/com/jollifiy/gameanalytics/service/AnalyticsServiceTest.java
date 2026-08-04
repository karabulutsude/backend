package com.jollifiy.gameanalytics.service;

import com.jollifiy.gameanalytics.dto.AnalyticsRequest;
import com.jollifiy.gameanalytics.entity.Analytics;
import com.jollifiy.gameanalytics.repository.AnalyticsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private AnalyticsRepository analyticsRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    @Test
    @DisplayName("Gecerli analytics istegi geldiginde veri veritabanina kaydedilmeli")
    void logEvent_ShouldSaveAnalytics_WhenRequestIsValid() {
        // GIVEN
        AnalyticsRequest request = new AnalyticsRequest();
        request.setPlayerId("player-uuid-123");
        request.setEventName("level_complete");
        request.setLevel(1);
        request.setScore(150);
        request.setCoinCount(20);
        request.setPlayTime(120.5);

        when(analyticsRepository.save(any(Analytics.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        Analytics result = analyticsService.saveEvent(request);

        // THEN
        assertNotNull(result, "Kaydedilen analitik nesnesi null olmamalı");
        assertEquals("player-uuid-123", result.getPlayerId(), "Player ID eşleşmeli");
        assertEquals("level_complete", result.getEventName(), "Event adı eşleşmeli");

        verify(analyticsRepository, times(1)).save(any(Analytics.class));
    }

    @Test
    @DisplayName("getAllEvents cagirildiginda tum analitik kayitlari liste olarak donmeli")
    void getAllEvents_ShouldReturnListOfAnalytics() {
        // GIVEN
        Analytics analytics1 = new Analytics();
        analytics1.setPlayerId("player-1");
        analytics1.setEventName("level_start");

        Analytics analytics2 = new Analytics();
        analytics2.setPlayerId("player-2");
        analytics2.setEventName("level_complete");

        when(analyticsRepository.findAll())
                .thenReturn(List.of(analytics1, analytics2));

        // WHEN
        List<Analytics> result = analyticsService.getAllEvents();

        // THEN
        assertNotNull(result, "Dönen liste null olmamalı");
        assertEquals(2, result.size(), "2 adet analitik kaydı dönmeli");
        assertEquals("player-1", result.get(0).getPlayerId());

        verify(analyticsRepository, times(1)).findAll();
    }
}