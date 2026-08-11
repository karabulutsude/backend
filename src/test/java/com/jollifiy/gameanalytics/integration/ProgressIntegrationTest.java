package com.jollifiy.gameanalytics.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jollifiy.gameanalytics.dto.ProgressSaveRequest;
import com.jollifiy.gameanalytics.entity.Progress;
import com.jollifiy.gameanalytics.repository.ProgressRepository;
import com.jollifiy.gameanalytics.service.ProgressService;
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

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
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
class ProgressIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private ProgressRepository progressRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private ProgressRepository progressServiceMockRepository;

    @InjectMocks
    private ProgressService progressService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        progressRepository.deleteAll();
    }

    @Test
    @WithMockUser
    @DisplayName("GET /progress/get - Kayitli ilerleme varsa basariyla donmeli")
    void shouldGetProgressByPlayerId() throws Exception {
        Progress progress = new Progress();
        progress.setPlayerId("player-uuid-888");
        progress.setCurrentLevel(5);
        progress.setTotalCoins(9200);
        progressRepository.save(progress);

        mockMvc.perform(get("/progress/get")
                        .param("playerId", "player-uuid-888"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerId", is("player-uuid-888")))
                .andExpect(jsonPath("$.currentLevel", is(5)))
                .andExpect(jsonPath("$.totalCoins", is(9200)));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /progress/get - Kayit bulunamazsa 404 NotFound donmeli")
    void shouldReturnNotFoundWhenProgressDoesNotExist() throws Exception {
        mockMvc.perform(get("/progress/get")
                        .param("playerId", "non-existent-player"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /progress/save - Oyuncu ilerlemesi basariyla kaydedilmeli")
    void shouldSaveProgress() throws Exception {
        ProgressSaveRequest request = new ProgressSaveRequest();
        request.setPlayerId("player-uuid-999");
        request.setCurrentLevel(3);
        request.setTotalCoins(4500);

        mockMvc.perform(post("/progress/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())) // <-- Eklendi
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerId", is("player-uuid-999")))
                .andExpect(jsonPath("$.currentLevel", is(3)))
                .andExpect(jsonPath("$.totalCoins", is(4500)));
    }

    @Test
    @DisplayName("[Unit] Oyuncunun mevcut kaydi varsa ilerleme basariyla guncellenmeli")
    void saveProgress_ShouldUpdateProgress_WhenProgressExists() {
        String playerId = "player-uuid-123";

        Progress existingProgress = new Progress();
        existingProgress.setPlayerId(playerId);
        existingProgress.setCurrentLevel(1);

        ProgressSaveRequest request = new ProgressSaveRequest();
        request.setPlayerId(playerId);
        request.setCurrentLevel(2);

        when(progressServiceMockRepository.findByPlayerId(playerId))
                .thenReturn(Optional.of(existingProgress));
        when(progressServiceMockRepository.save(any(Progress.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Progress result = progressService.saveProgress(request);

        assertNotNull(result, "Dönen Progress nesnesi null olmamalı");
        assertEquals(2, result.getCurrentLevel(), "Seviye 2'ye güncellenmiş olmalı");

        verify(progressServiceMockRepository, times(1)).save(any(Progress.class));
    }

    @Test
    @DisplayName("[Unit] Mevcut oyuncunun coin miktari uzerine eklenmeli ve max level (3) gecilmemeli")
    void saveProgress_ShouldAccumulateCoinsAndCapMaxLevel() {
        String playerId = "player-123";

        Progress existingProgress = new Progress();
        existingProgress.setPlayerId(playerId);
        existingProgress.setCurrentLevel(2);
        existingProgress.setTotalCoins(100);

        ProgressSaveRequest request = new ProgressSaveRequest();
        request.setPlayerId(playerId);
        request.setCurrentLevel(5);
        request.setTotalCoins(50);

        when(progressServiceMockRepository.findByPlayerId(playerId))
                .thenReturn(Optional.of(existingProgress));
        when(progressServiceMockRepository.save(any(Progress.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Progress result = progressService.saveProgress(request);

        assertNotNull(result, "Sonuç null olmamalı");
        assertEquals(3, result.getCurrentLevel(), "Level max 3 sınırında kalmalı");
        assertEquals(150, result.getTotalCoins(), "Coin miktarları toplanmalı (100 + 50 = 150)");

        verify(progressServiceMockRepository, times(1)).save(any(Progress.class));
    }

    @Test
    @DisplayName("[Unit] Oyuncu ilk kez kaydediliyorsa yeni ilerleme olusturulmali")
    void saveProgress_ShouldCreateNewProgress_WhenProgressDoesNotExist() {
        String playerId = "fake-player-999";

        ProgressSaveRequest request = new ProgressSaveRequest();
        request.setPlayerId(playerId);
        request.setCurrentLevel(1);

        when(progressServiceMockRepository.findByPlayerId(playerId))
                .thenReturn(Optional.empty());
        when(progressServiceMockRepository.save(any(Progress.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Progress result = progressService.saveProgress(request);

        assertNotNull(result, "Yeni oluşturulan Progress null olmamalı");
        assertEquals(playerId, result.getPlayerId(), "Player ID eşleşmeli");

        verify(progressServiceMockRepository, times(1)).save(any(Progress.class));
    }

    @Test
    @DisplayName("[Unit] Oyuncu bulundugunda progress nesnesi donmeli")
    void getProgressByPlayerId_ShouldReturnProgress_WhenPlayerExists() {
        String playerId = "player-123";
        Progress progress = new Progress();
        progress.setPlayerId(playerId);

        when(progressServiceMockRepository.findByPlayerId(playerId))
                .thenReturn(Optional.of(progress));

        Progress result = progressService.getProgressByPlayerId(playerId);

        assertNotNull(result, "Bulunan kayıt için dönen nesne null olmamalı");
        assertEquals(playerId, result.getPlayerId(), "Player ID eşleşmeli");

        verify(progressServiceMockRepository, times(1)).findByPlayerId(playerId);
    }

    @Test
    @DisplayName("[Unit] Oyuncu bulunamadiginda null donmeli")
    void getProgressByPlayerId_ShouldReturnNull_WhenPlayerDoesNotExist() {
        String playerId = "unknown-player";

        when(progressServiceMockRepository.findByPlayerId(playerId))
                .thenReturn(Optional.empty());

        Progress result = progressService.getProgressByPlayerId(playerId);

        assertNull(result, "Kayıt olmadığında servis null dönmeli");

        verify(progressServiceMockRepository, times(1)).findByPlayerId(playerId);
    }
}