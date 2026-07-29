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

    // 1. İlerleme Kaydetme / Güncelleme Metodu
    public Progress saveProgress(ProgressSaveRequest request) {

        Optional<Progress> existingProgress =
                progressRepository.findByPlayerId(request.getPlayerId());

        Progress progress;

        // Maksimum level sınırımız 3
        int maxLevel = 3;

        if (existingProgress.isPresent()) {
            progress = existingProgress.get();

            // Level Güncellemesi (Gelen level maxLevel'ı geçemez)
            if (request.getCurrentLevel() > progress.getCurrentLevel()) {
                int newLevel = Math.min(request.getCurrentLevel(), maxLevel);
                progress.setCurrentLevel(newLevel);
            }

            // Kümülatif Toplam Coin
            int currentCoinsInDb = (progress.getTotalCoins() != null) ? progress.getTotalCoins() : 0;
            progress.setTotalCoins(currentCoinsInDb + request.getTotalCoins());

        } else {
            // İlk Defa İlerleme Kaydedilen Oyuncu
            progress = new Progress();
            progress.setPlayerId(request.getPlayerId());

            // İlk kayıtta da max level kontrolü yapalım
            int initialLevel = Math.min(request.getCurrentLevel(), maxLevel);
            progress.setCurrentLevel(initialLevel);

            progress.setTotalCoins(request.getTotalCoins());
        }

        // Zamanı güncelliyoruz
        progress.setUpdatedAt(LocalDateTime.now());

        return progressRepository.save(progress);
    }

    // 2. YENİ EKLENEN METOT: Unity'nin profil ekranı için veri çektiği metot
    public Progress getProgressByPlayerId(String playerId) {
        return progressRepository.findByPlayerId(playerId).orElse(null);
    }
}