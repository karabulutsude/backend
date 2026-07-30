package com.jollifiy.gameanalytics.service;

import com.jollifiy.gameanalytics.dto.ProgressSaveRequest;
import com.jollifiy.gameanalytics.entity.Progress;
import com.jollifiy.gameanalytics.repository.ProgressRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ProgressService {

    private final ProgressRepository progressRepository;

    public ProgressService(ProgressRepository progressRepository) {
        this.progressRepository = progressRepository;
    }

    public Progress saveProgress(ProgressSaveRequest request) {
        Optional<Progress> existingProgress = progressRepository.findByPlayerId(request.getPlayerId());

        Progress progress;
        int maxLevel = 3;

        // NullPointerException riskine karşı güvenli coin okuma
        int incomingCoins = (request.getTotalCoins() != null) ? request.getTotalCoins() : 0;
        int incomingLevel = (request.getCurrentLevel() != null) ? request.getCurrentLevel() : 1;

        if (existingProgress.isPresent()) {
            progress = existingProgress.get();

            if (incomingLevel > progress.getCurrentLevel()) {
                int newLevel = Math.min(incomingLevel, maxLevel);
                progress.setCurrentLevel(newLevel);
            }

            int currentCoinsInDb = (progress.getTotalCoins() != null) ? progress.getTotalCoins() : 0;
            progress.setTotalCoins(currentCoinsInDb + incomingCoins);

        } else {
            progress = new Progress();
            progress.setPlayerId(request.getPlayerId());

            int initialLevel = Math.min(incomingLevel, maxLevel);
            progress.setCurrentLevel(initialLevel);

            progress.setTotalCoins(incomingCoins);
        }

        progress.setUpdatedAt(LocalDateTime.now());

        return progressRepository.save(progress);
    }

    public Progress getProgressByPlayerId(String playerId) {
        return progressRepository.findByPlayerId(playerId).orElse(null);
    }
}