package com.jollifiy.gameanalytics.service;

import com.jollifiy.gameanalytics.dto.AnalyticsRequest;
import com.jollifiy.gameanalytics.entity.Analytics;
import com.jollifiy.gameanalytics.repository.AnalyticsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    public AnalyticsService(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    // Vaadin arayüzü için doğrudan entity kaydetme / güncelleme
    public Analytics save(Analytics analytics) {
        return analyticsRepository.save(analytics);
    }

    // Vaadin arayüzü için kayıt silme
    public void delete(Analytics analytics) {
        analyticsRepository.delete(analytics);
        log.info("Analiz kaydı silindi. ID: {}", analytics.getId());
    }

    public Analytics saveEvent(AnalyticsRequest request) {
        log.info("Analiz olayı alındı. Player ID: {}, Event: {}", request.getPlayerId(), request.getEventName());
        log.debug("Olay detayları: PlayerID={}, Event={}, Level={}, Score={}",
                request.getPlayerId(), request.getEventName(), request.getLevel(), request.getScore());

        Analytics analytics = new Analytics();
        analytics.setPlayerId(request.getPlayerId());
        analytics.setEventName(request.getEventName());
        analytics.setLevel(request.getLevel());
        analytics.setScore(request.getScore());
        analytics.setCoinCount(request.getCoinCount());
        analytics.setCompletionPercentage(request.getCompletionPercentage());
        analytics.setPlayTime(request.getPlayTime());
        analytics.setHealthRemaining(request.getHealthRemaining());
        analytics.setDeathReason(request.getDeathReason());

        Analytics savedAnalytics = analyticsRepository.save(analytics);

        log.info("Analiz kaydı başarıyla oluşturuldu. ID: {}", savedAnalytics.getId());

        return savedAnalytics;
    }

    public List<Analytics> getAllEvents() {
        log.debug("Tüm analiz kayıtları veritabanından getiriliyor.");

        List<Analytics> events = analyticsRepository.findAll();

        log.info("Getirilen toplam analiz kayıt sayısı: {}", events.size());

        return events;
    }
}