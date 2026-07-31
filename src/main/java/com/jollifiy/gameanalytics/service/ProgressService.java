package com.jollifiy.gameanalytics.service;

import com.jollifiy.gameanalytics.dto.ProgressSaveRequest;
import com.jollifiy.gameanalytics.entity.Progress;
import com.jollifiy.gameanalytics.repository.ProgressRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
            log.debug("Mevcut ilerleme kaydı bulundu. Player ID: {}, Mevcut Level: {}",
                    request.getPlayerId(), progress.getCurrentLevel());

            if (incomingLevel > progress.getCurrentLevel()) {
                int newLevel = Math.min(incomingLevel, maxLevel);
                log.info("Oyuncunun seviyesi yükseltildi. Player ID: {}, Eski Level: {}, Yeni Level: {}",
                        request.getPlayerId(), progress.getCurrentLevel(), newLevel);
                progress.setCurrentLevel(newLevel);
            }

            int currentCoinsInDb = (progress.getTotalCoins() != null) ? progress.getTotalCoins() : 0;
            progress.setTotalCoins(currentCoinsInDb + incomingCoins);

        } else {
            log.info("Oyuncu için ilk kez ilerleme kaydı oluşturuluyor. Player ID: {}", request.getPlayerId());
            progress = new Progress();
            progress.setPlayerId(request.getPlayerId());

            int initialLevel = Math.min(incomingLevel, maxLevel);
            progress.setCurrentLevel(initialLevel);
            progress.setTotalCoins(incomingCoins);
        }

        progress.setUpdatedAt(LocalDateTime.now());
        Progress savedProgress = progressRepository.save(progress);

        log.info("İlerleme kaydedildi. Player ID: {}, Güncel Level: {}, Toplam Coin: {}",
                savedProgress.getPlayerId(), savedProgress.getCurrentLevel(), savedProgress.getTotalCoins());

        return savedProgress;
    }

    public Progress getProgressByPlayerId(String playerId) {
        log.debug("Oyuncu ilerleme bilgisi sorgulanıyor. Player ID: {}", playerId);
        Optional<Progress> progress = progressRepository.findByPlayerId(playerId);

        if (progress.isEmpty()) {
            log.warn("Oyuncuya ait ilerleme kaydı bulunamadı! Player ID: {}", playerId);
        }

        return progress.orElse(null);
    }
}