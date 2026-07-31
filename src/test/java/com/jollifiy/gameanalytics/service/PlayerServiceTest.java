package com.jollifiy.gameanalytics.service;

import com.jollifiy.gameanalytics.entity.Player;
import com.jollifiy.gameanalytics.repository.PlayerRepository;
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
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerService playerService;

    @Test
    @DisplayName("Cihaz ID sistemde yoksa yeni oyuncu olusturulmali")
    void login_ShouldCreateNewPlayer_WhenDeviceDoesNotExist() {
        // 1. GIVEN (Hazırlık)
        String deviceId = "device-123";
        String country = "TR";

        when(playerRepository.findByDeviceId(deviceId)).thenReturn(Optional.empty());
        when(playerRepository.save(any(Player.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. WHEN (Eylem)
        Player result = playerService.login(deviceId, country);

        // 3. THEN (Kontroller / Verification)
        assertNotNull(result, "Dönen oyuncu nesnesi null olmamalı");
        assertEquals(deviceId, result.getDeviceId(), "Cihaz ID eşleşmeli");
        assertEquals(country, result.getCountry(), "Ülke eşleşmeli");
        assertNotNull(result.getPlayerId(), "Yeni rastgele Player ID üretilmeli");

        // Veritabanına kayıt metodunun tam 1 kez çağrıldığını kontrol ediyoruz
        verify(playerRepository, times(1)).save(any(Player.class));
    }

    @Test
    @DisplayName("Cihaz ID zaten varsa ve ulke degismediyse mevcut oyuncu donmeli")
    void login_ShouldReturnExistingPlayer_WhenDeviceExistsAndCountryUnchanged() {
        // 1. GIVEN
        String deviceId = "device-123";
        String country = "TR";

        Player existingPlayer = new Player();
        existingPlayer.setPlayerId("existing-uuid-123");
        existingPlayer.setDeviceId(deviceId);
        existingPlayer.setCountry(country);

        when(playerRepository.findByDeviceId(deviceId)).thenReturn(Optional.of(existingPlayer));

        // 2. WHEN
        Player result = playerService.login(deviceId, country);

        // 3. THEN
        assertNotNull(result);
        assertEquals("existing-uuid-123", result.getPlayerId());
        // Zaten var olan oyuncunun bilgisi değişmediği için save() metodu HİÇ çağrılmamalı:
        verify(playerRepository, never()).save(any(Player.class));
    }
}