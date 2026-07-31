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
        // 1. GIVEN (Hazırlık)
        AnalyticsRequest request = new AnalyticsRequest();
        request.setPlayerId("player-uuid-123");
        request.setEventName("level_complete");
        request.setLevel(1);
        request.setScore(150); // Double (150.0) yerine Integer (150) yaptık
        request.setCoinCount(20);
        request.setPlayTime(120.5);

        when(analyticsRepository.save(any(Analytics.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. WHEN (Eylem)
        // NOTE: Servisindeki metot adı 'saveEvent' ise koddaki metot adını ona göre çağırıyoruz:
        Analytics result = analyticsService.saveEvent(request);

        // 3. THEN (Doğrulama)
        assertNotNull(result, "Kaydedilen analitik nesnesi null olmamalı");
        assertEquals("player-uuid-123", result.getPlayerId(), "Player ID eşleşmeli");
        assertEquals("level_complete", result.getEventName(), "Event adı eşleşmeli");

        verify(analyticsRepository, times(1)).save(any(Analytics.class));
    }
}