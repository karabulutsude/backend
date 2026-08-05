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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Tüm Spring Boot uygulamasını test ortamında (tüm katmanları ve bileşenleriyle) ayağa kaldırır
@SpringBootTest
// Gerçek bir web sunucusu başlatmadan HTTP isteklerini (GET, POST vb.) simüle etmeyi sağlar
@AutoConfigureMockMvc
// 'application-test.properties' dosyasını aktif ederek testlerde H2 bellek içi veritabanının kullanılmasını sağlar
@ActiveProfiles("test")
class AnalyticsIntegrationTest {

    // HTTP isteklerini uç noktalara gönderebilmek için kullanılan mock nesnesi
    @Autowired
    private MockMvc mockMvc;

    // Java nesnelerini JSON formatına dönüştürmek için kullanılan araç
    @Autowired
    private ObjectMapper objectMapper;

    // Entegrasyon testlerinde gerçek veritabanı işlemlerini kontrol etmek için kullanılan repository
    @Autowired
    private AnalyticsRepository analyticsRepository;

    // --- BİRİM (UNIT) TESTLERİ İÇİN MOCK TANIMLARI ---

    // Unit testlerde gerçek veritabanı yerine kullanılacak sahte (Mock) repository nesnesi
    @Mock
    private AnalyticsRepository analyticsServiceMockRepository;

    // Unit testlerde test edilecek olan gerçek Service sınıfı (İçine yukarıdaki mock repository enjekte edilir)
    @InjectMocks
    private AnalyticsService analyticsService;

    // Her test metodundan önce çalışarak veritabanını temizler
    @BeforeEach
    void setUp() {
        analyticsRepository.deleteAll();
    }

    // ==========================================
    //  İNTEGRASYON TESTLERİ (MockMvc & H2 DB)
    // ==========================================

    @Test
    @DisplayName("GET /analytics - Veritabanindaki tüm kayıtlar dönmeli")
    void shouldReturnAllAnalyticsFromDatabase() throws Exception {
        // Hazırlık: Veritabanına doğrudan sahte bir kayıt eklenir
        Analytics event1 = new Analytics();
        event1.setPlayerId("player-1");
        event1.setEventName("game_start");

        analyticsRepository.saveAll(List.of(event1));

        // Eylem & Doğrulama: /analytics adresine GET isteği atılır ve sonuçlar denetlenir
        mockMvc.perform(get("/analytics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].playerId", is("player-1")));
    }

    @Test
    @DisplayName("POST /analytics/send - Istek atildiginda veritabanina kaydedilmeli")
    void shouldSaveAnalyticsToDatabase() throws Exception {
        // Hazırlık: İstek atılacak DTO nesnesi hazırlanır
        AnalyticsRequest request = new AnalyticsRequest();
        request.setPlayerId("player-uuid-999");
        request.setEventName("level_complete");

        // Eylem & Doğrulama: POST isteği simüle edilir
        mockMvc.perform(post("/analytics/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerId", is("player-uuid-999")))
                .andExpect(jsonPath("$.eventName", is("level_complete")));

        // Veritabanı Kontrolü: Kaydın gerçekte veritabanına işlendiği doğrulanır
        List<Analytics> allEvents = analyticsRepository.findAll();
        assertEquals(1, allEvents.size());
        assertEquals("player-uuid-999", allEvents.get(0).getPlayerId());
    }

    // ==========================================
    //  BİRİM (UNIT) TESTLERİ (Mockito)
    // ==========================================

    @Test
    @DisplayName("[Unit] Gecerli analytics istegi geldiginde veri veritabanina kaydedilmeli")
    void logEvent_ShouldSaveAnalytics_WhenRequestIsValid() {
        // GIVEN: Test girdileri ve mock davranışı tanımlanır
        AnalyticsRequest request = new AnalyticsRequest();
        request.setPlayerId("player-uuid-123");
        request.setEventName("level_complete");
        request.setLevel(1);
        request.setScore(150);
        request.setCoinCount(20);
        request.setPlayTime(120.5);

        // Mock repository'nin save metodunun gönderilen nesneyi geri döndürmesi sağlanır
        when(analyticsServiceMockRepository.save(any(Analytics.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN: Servis metodu çağrılır
        Analytics result = analyticsService.saveEvent(request);

        // THEN: Sonuçlar doğrulanır
        assertNotNull(result, "Kaydedilen analitik nesnesi null olmamalı");
        assertEquals("player-uuid-123", result.getPlayerId(), "Player ID eşleşmeli");
        assertEquals("level_complete", result.getEventName(), "Event adı eşleşmeli");

        verify(analyticsServiceMockRepository, times(1)).save(any(Analytics.class));
    }

    @Test
    @DisplayName("[Unit] getAllEvents cagirildiginda tum analitik kayitlari liste olarak donmeli")
    void getAllEvents_ShouldReturnListOfAnalytics() {
        // GIVEN: Mock veritabanından dönecek sahte liste hazırlanır
        Analytics analytics1 = new Analytics();
        analytics1.setPlayerId("player-1");
        analytics1.setEventName("level_start");

        Analytics analytics2 = new Analytics();
        analytics2.setPlayerId("player-2");
        analytics2.setEventName("level_complete");

        when(analyticsServiceMockRepository.findAll())
                .thenReturn(List.of(analytics1, analytics2));

        // WHEN: Servis metodu tetiklenir
        List<Analytics> result = analyticsService.getAllEvents();

        // THEN: Dönen listenin doğruluğu kontrol edilir
        assertNotNull(result, "Dönen liste null olmamalı");
        assertEquals(2, result.size(), "2 adet analitik kaydı dönmeli");
        assertEquals("player-1", result.get(0).getPlayerId());

        verify(analyticsServiceMockRepository, times(1)).findAll();
    }
}