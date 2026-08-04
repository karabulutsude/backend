package com.jollifiy.gameanalytics.service;

import com.jollifiy.gameanalytics.dto.ProgressSaveRequest;
import com.jollifiy.gameanalytics.entity.Progress;
import com.jollifiy.gameanalytics.repository.ProgressRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class ProgressService {

    private final ProgressRepository progressRepository;

    public ProgressService(ProgressRepository progressRepository) {
        this.progressRepository = progressRepository;
    }

    public Progress saveProgress(ProgressSaveRequest request) {
        log.info("İlerleme kaydetme isteği geldi. Player ID: {}", request.getPlayerId());

        Optional<Progress> existingProgress = progressRepository.findByPlayerId(request.getPlayerId());

        Progress progress;
        int maxLevel = 3;
        int incomingCoins = (request.getTotalCoins() != null) ? request.getTotalCoins() : 0;
        int incomingLevel = (request.getCurrentLevel() != null) ? request.getCurrentLevel() : 1;

        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
            log.debug("Mevcut ilerleme kaydı bulundu. Player ID: {}", request.getPlayerId());

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

        Progress savedProgress = progressRepository.save(progress);

        log.info("İlerleme başarıyla kaydedildi. Player ID: {}, Current Level: {}, Total Coins: {}",
                savedProgress.getPlayerId(), savedProgress.getCurrentLevel(), savedProgress.getTotalCoins());

        return savedProgress;
    }

    public Progress getProgressByPlayerId(String playerId) {
        log.debug("Oyuncu ilerlemesi sorgulanıyor. Player ID: {}", playerId);

        Optional<Progress> progress = progressRepository.findByPlayerId(playerId);

        if (progress.isEmpty()) {
            log.warn("Oyuncuya ait ilerleme kaydı bulunamadı. Player ID: {}", playerId);
        }

        return progress.orElse(null);
    }
}