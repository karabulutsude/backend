package com.jollifiy.gameanalytics.service;

import com.jollifiy.gameanalytics.dto.ProgressSaveRequest;
import com.jollifiy.gameanalytics.entity.Progress;
import com.jollifiy.gameanalytics.repository.PlayerRepository;
import com.jollifiy.gameanalytics.repository.ProgressRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgressServiceTest {

    @Mock
    private ProgressRepository progressRepository;

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private ProgressService progressService;

    @Test
    @DisplayName("Oyuncunun mevcut kaydi varsa ilerleme basariyla guncellenmeli")
    void saveProgress_ShouldUpdateProgress_WhenProgressExists() {
        // 1. GIVEN (Hazırlık)
        String playerId = "player-uuid-123";

        Progress existingProgress = new Progress();
        existingProgress.setPlayerId(playerId);
        existingProgress.setCurrentLevel(1);

        ProgressSaveRequest request = new ProgressSaveRequest();
        request.setPlayerId(playerId);
        request.setCurrentLevel(2);

        when(progressRepository.findByPlayerId(playerId)).thenReturn(Optional.of(existingProgress));
        when(progressRepository.save(any(Progress.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. WHEN (Eylem)
        Progress result = progressService.saveProgress(request);

        // 3. THEN (Doğrulama)
        assertNotNull(result, "Dönen Progress nesnesi null olmamalı");
        assertEquals(2, result.getCurrentLevel(), "Seviye 2'ye güncellenmeli");

        verify(progressRepository, times(1)).save(any(Progress.class));
    }

    @Test
    @DisplayName("Oyuncu ilk kez kaydediliyorsa yeni ilerleme olusturulmali")
    void saveProgress_ShouldCreateNewProgress_WhenProgressDoesNotExist() {
        // 1. GIVEN (Hazırlık)
        String playerId = "fake-player-999";

        ProgressSaveRequest request = new ProgressSaveRequest();
        request.setPlayerId(playerId);
        request.setCurrentLevel(1);

        when(progressRepository.findByPlayerId(playerId)).thenReturn(Optional.empty());
        when(progressRepository.save(any(Progress.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. WHEN (Eylem)
        Progress result = progressService.saveProgress(request);

        // 3. THEN (Doğrulama)
        assertNotNull(result, "Yeni oluşturulan Progress null olmamalı");
        assertEquals(playerId, result.getPlayerId(), "Player ID eşleşmeli");

        // Veritabanına yeni kayıt olarak eklendiğini doğruluyoruz
        verify(progressRepository, times(1)).save(any(Progress.class));
    }
}