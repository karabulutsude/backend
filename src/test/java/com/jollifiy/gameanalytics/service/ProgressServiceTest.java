package com.jollifiy.gameanalytics.service;

import com.jollifiy.gameanalytics.dto.ProgressSaveRequest;
import com.jollifiy.gameanalytics.entity.Progress;
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

// Mockito'yu JUnit 5 ile entegre çalıştırır. @Mock ve @InjectMocks anotasyonlarının çalışmasını sağlar.
@ExtendWith(MockitoExtension.class)
class ProgressServiceTest {

    // Gerçek veritabanı bağlantısı yerine sahte (Mock) bir ProgressRepository oluşturur.
    @Mock
    private ProgressRepository progressRepository;

    // Test edeceğimiz ProgressService nesnesini oluşturur ve yukarıdaki mock repository'yi içine otomatik enjekte eder.
    @InjectMocks
    private ProgressService progressService;

    @Test
    @DisplayName("Oyuncunun mevcut kaydi varsa ilerleme basariyla guncellenmeli")
    void saveProgress_ShouldUpdateProgress_WhenProgressExists() {
        // =========================================================================
        // GIVEN (Hazırlık Aşaması): Testte kullanılacak nesneler ve Mock davranışları hazırlanır.
        // =========================================================================
        String playerId = "player-uuid-123";

        // Veritabanında önceden var olduğunu varsaydığımız oyuncu ilerleme kaydı (Level 1)
        Progress existingProgress = new Progress();
        existingProgress.setPlayerId(playerId);
        existingProgress.setCurrentLevel(1);

        // Oyuncunun yeni kaydetmek istediği ilerleme isteği (Level 2)
        ProgressSaveRequest request = new ProgressSaveRequest();
        request.setPlayerId(playerId);
        request.setCurrentLevel(2);

        // Repository'ye 'findByPlayerId' çağrısı geldiğinde sahte veritabanı kaydımızı döndür
        when(progressRepository.findByPlayerId(playerId))
                .thenReturn(Optional.of(existingProgress));

        // Repository'ye 'save' çağrısı geldiğinde kendisine gönderilen nesneyi aynen geri döndür
        when(progressRepository.save(any(Progress.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // =========================================================================
        // WHEN (İşlemi Çalıştır): Test edilmek istenen metod çalıştırılır.
        // =========================================================================
        Progress result = progressService.saveProgress(request);

        // =========================================================================
        // THEN (Doğrulama): Sonuçların iş mantığına uygunluğu doğrulanır.
        // =========================================================================
        assertNotNull(result, "Dönen Progress nesnesi null olmamalı");
        assertEquals(2, result.getCurrentLevel(), "Seviye 2'ye güncellenmiş olmalı");

        // Repository'nin save() metodunun tam 1 kez çağrıldığını doğrula
        verify(progressRepository, times(1)).save(any(Progress.class));
    }

    @Test
    @DisplayName("Mevcut oyuncunun coin miktari uzerine eklenmeli ve max level (3) gecilmemeli")
    void saveProgress_ShouldAccumulateCoinsAndCapMaxLevel() {
        // =========================================================================
        // GIVEN (Hazırlık Aşaması)
        // =========================================================================
        String playerId = "player-123";

        // Veritabanında 100 altını ve 2. seviyesi olan mevcut oyuncu
        Progress existingProgress = new Progress();
        existingProgress.setPlayerId(playerId);
        existingProgress.setCurrentLevel(2);
        existingProgress.setTotalCoins(100);

        // Oyuncudan gelen istek: Level'ı 5 yap ve 50 altın ekle
        ProgressSaveRequest request = new ProgressSaveRequest();
        request.setPlayerId(playerId);
        request.setCurrentLevel(5); // Sınır durum testi: Max level 3 kuralını test ediyoruz
        request.setTotalCoins(50);  // İş kuralı testi: 100 + 50 = 150 olmalı

        when(progressRepository.findByPlayerId(playerId))
                .thenReturn(Optional.of(existingProgress));

        when(progressRepository.save(any(Progress.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // =========================================================================
        // WHEN (İşlemi Çalıştır)
        // =========================================================================
        Progress result = progressService.saveProgress(request);

        // =========================================================================
        // THEN (Doğrulama)
        // =========================================================================
        assertNotNull(result, "Sonuç null olmamalı");

        // İş kuralı 1: Level 5 isteği gelse dahi sistem maksimum 3 seviyesinde sınırlamalı
        assertEquals(3, result.getCurrentLevel(), "Level max 3 sınırında kalmalı");

        // İş kuralı 2: Mevcut coin miktarı (100) ile gelen coin miktarı (50) toplanmalı
        assertEquals(150, result.getTotalCoins(), "Coin miktarları toplanmalı (100 + 50 = 150)");

        verify(progressRepository, times(1)).save(any(Progress.class));
    }

    @Test
    @DisplayName("Oyuncu ilk kez kaydediliyorsa yeni ilerleme olusturulmali")
    void saveProgress_ShouldCreateNewProgress_WhenProgressDoesNotExist() {
        // =========================================================================
        // GIVEN (Hazırlık Aşaması)
        // =========================================================================
        String playerId = "fake-player-999";

        ProgressSaveRequest request = new ProgressSaveRequest();
        request.setPlayerId(playerId);
        request.setCurrentLevel(1);

        // Veritabanında oyuncuya ait kayıt bulunamadı durumu (Optional.empty)
        when(progressRepository.findByPlayerId(playerId))
                .thenReturn(Optional.empty());

        when(progressRepository.save(any(Progress.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // =========================================================================
        // WHEN (İşlemi Çalıştır)
        // =========================================================================
        Progress result = progressService.saveProgress(request);

        // =========================================================================
        // THEN (Doğrulama)
        // =========================================================================
        assertNotNull(result, "Yeni oluşturulan Progress null olmamalı");
        assertEquals(playerId, result.getPlayerId(), "Player ID eşleşmeli");

        verify(progressRepository, times(1)).save(any(Progress.class));
    }

    @Test
    @DisplayName("Oyuncu bulundugunda progress nesnesi donmeli")
    void getProgressByPlayerId_ShouldReturnProgress_WhenPlayerExists() {
        // =========================================================================
        // GIVEN (Hazırlık Aşaması)
        // =========================================================================
        String playerId = "player-123";
        Progress progress = new Progress();
        progress.setPlayerId(playerId);

        when(progressRepository.findByPlayerId(playerId))
                .thenReturn(Optional.of(progress));

        // =========================================================================
        // WHEN (İşlemi Çalıştır)
        // =========================================================================
        Progress result = progressService.getProgressByPlayerId(playerId);

        // =========================================================================
        // THEN (Doğrulama)
        // =========================================================================
        assertNotNull(result, "Bulunan kayıt için dönen nesne null olmamalı");
        assertEquals(playerId, result.getPlayerId(), "Player ID eşleşmeli");

        verify(progressRepository, times(1)).findByPlayerId(playerId);
    }

    @Test
    @DisplayName("Oyuncu bulunamadiginda null donmeli")
    void getProgressByPlayerId_ShouldReturnNull_WhenPlayerDoesNotExist() {
        // =========================================================================
        // GIVEN (Hazırlık Aşaması)
        // =========================================================================
        String playerId = "unknown-player";

        // Veritabanında kayıtlı olmayan oyuncu
        when(progressRepository.findByPlayerId(playerId))
                .thenReturn(Optional.empty());

        // =========================================================================
        // WHEN (İşlemi Çalıştır)
        // =========================================================================
        Progress result = progressService.getProgressByPlayerId(playerId);

        // =========================================================================
        // THEN (Doğrulama)
        // =========================================================================
        assertNull(result, "Kayıt olmadığında servis null dönmeli");

        verify(progressRepository, times(1)).findByPlayerId(playerId);
    }
}