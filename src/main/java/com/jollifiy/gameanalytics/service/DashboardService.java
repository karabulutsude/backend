package com.jollifiy.gameanalytics.service;

import com.jollifiy.gameanalytics.repository.AnalyticsRepository;
import com.jollifiy.gameanalytics.repository.PlayerRepository;
import com.jollifiy.gameanalytics.repository.ProgressRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final PlayerRepository playerRepository;
    private final AnalyticsRepository analyticsRepository;
    private final ProgressRepository progressRepository;

    public DashboardService(PlayerRepository playerRepository,
                            AnalyticsRepository analyticsRepository,
                            ProgressRepository progressRepository) {
        this.playerRepository = playerRepository;
        this.analyticsRepository = analyticsRepository;
        this.progressRepository = progressRepository;
    }

    public long getTotalPlayerCount() {
        return playerRepository.count();
    }

    public long getTotalAnalyticsCount() {
        return analyticsRepository.count();
    }

    public long getTotalProgressCount() {
        return progressRepository.count();
    }
}