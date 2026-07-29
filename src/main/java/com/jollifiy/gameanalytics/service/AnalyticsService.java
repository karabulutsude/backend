/*
package com.jollifiy.gameanalytics.service;

import com.jollifiy.gameanalytics.entity.Analytics;
import com.jollifiy.gameanalytics.repository.AnalyticsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    public AnalyticsService(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    public Analytics saveEvent(Analytics analytics) {
        return analyticsRepository.save(analytics);
    }

    public List<Analytics> getAllEvents() {
        return analyticsRepository.findAll();
    }
}

 */

package com.jollifiy.gameanalytics.service;

import com.jollifiy.gameanalytics.dto.AnalyticsRequest;
import com.jollifiy.gameanalytics.entity.Analytics;
import com.jollifiy.gameanalytics.repository.AnalyticsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    public AnalyticsService(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    // Metot artık AnalyticsRequest (DTO) alıyor ve Entity'ye dönüştürüp kaydediyor
    public Analytics saveEvent(AnalyticsRequest request) {
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
        analytics.setCreatedAt(LocalDateTime.now());

        return analyticsRepository.save(analytics);
    }

    public List<Analytics> getAllEvents() {
        return analyticsRepository.findAll();
    }
}