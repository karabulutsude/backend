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

    @Mock
    private ConfigService configService; // <-- Yeni eklenen config servisi mock'landı

    @InjectMocks
    private PlayerService playerService;

    @Test
    @DisplayName("Cihaz ID sistemde yoksa ve sürüm destekleniyorsa yeni oyuncu olusturulmali")
    void login_ShouldCreateNewPlayer_WhenDeviceDoesNotExistAndVersionSupported() {
        // GIVEN
        String deviceId = "device-123";
        String country = "TR";
        String clientVersion = "1.0.0";

        when(configService.isVersionSupported(clientVersion)).thenReturn(true);

        when(playerRepository.findByDeviceId(deviceId))
                .thenReturn(Optional.empty());

        when(playerRepository.save(any(Player.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        Player result = playerService.login(deviceId, country, clientVersion);

        // THEN
        assertNotNull(result, "Dönen oyuncu nesnesi null olmamalı");
        assertEquals(deviceId, result.getDeviceId(), "Cihaz ID eşleşmeli");
        assertEquals(country, result.getCountry(), "Ülke eşleşmeli");
        assertNotNull(result.getPlayerId(), "Yeni rastgele Player ID üretilmeli");

        verify(configService, times(1)).isVersionSupported(clientVersion);
        verify(playerRepository, times(1)).save(any(Player.class));
    }

    @Test
    @DisplayName("Cihaz ID zaten varsa, ülke değişmediyse ve sürüm destekleniyorsa mevcut oyuncu donmeli")
    void login_ShouldReturnExistingPlayer_WhenDeviceExistsCountryUnchangedAndVersionSupported() {
        // GIVEN
        String deviceId = "device-123";
        String country = "TR";
        String clientVersion = "1.0.0";

        Player existingPlayer = new Player();
        existingPlayer.setPlayerId("existing-uuid-123");
        existingPlayer.setDeviceId(deviceId);
        existingPlayer.setCountry(country);

        when(configService.isVersionSupported(clientVersion)).thenReturn(true);

        when(playerRepository.findByDeviceId(deviceId))
                .thenReturn(Optional.of(existingPlayer));

        // WHEN
        Player result = playerService.login(deviceId, country, clientVersion);

        // THEN
        assertNotNull(result);
        assertEquals("existing-uuid-123", result.getPlayerId());

        verify(configService, times(1)).isVersionSupported(clientVersion);
        verify(playerRepository, never()).save(any(Player.class));
    }

    @Test
    @DisplayName("Cihaz ID zaten varsa, ülke değiştiyse ve sürüm destekleniyorsa ülke güncellenip kaydedilmeli")
    void login_ShouldUpdateCountryOfExistingPlayer_WhenDeviceExistsCountryChangedAndVersionSupported() {
        // GIVEN
        String deviceId = "device-123";
        String newCountry = "US";
        String clientVersion = "1.0.0";

        Player existingPlayer = new Player();
        existingPlayer.setPlayerId("existing-uuid-123");
        existingPlayer.setDeviceId(deviceId);
        existingPlayer.setCountry("TR");

        when(configService.isVersionSupported(clientVersion)).thenReturn(true);

        when(playerRepository.findByDeviceId(deviceId))
                .thenReturn(Optional.of(existingPlayer));

        when(playerRepository.save(any(Player.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        Player result = playerService.login(deviceId, newCountry, clientVersion);

        // THEN
        assertNotNull(result);
        assertEquals("existing-uuid-123", result.getPlayerId());
        assertEquals(newCountry, result.getCountry(), "Ülke yeni gelen değerle güncellenmeli");

        verify(configService, times(1)).isVersionSupported(clientVersion);
        verify(playerRepository, times(1)).save(any(Player.class));
    }

    @Test
    @DisplayName("İstemci sürümü desteklenmiyorsa Force Update hatası fırlatılmalı ve DB işlemleri yapılmamalı")
    void login_ShouldThrowException_WhenClientVersionNotSupported() {
        // GIVEN
        String deviceId = "device-123";
        String country = "TR";
        String clientVersion = "0.8.0"; // Desteklenmeyen eski sürüm

        when(configService.isVersionSupported(clientVersion)).thenReturn(false);

        // WHEN & THEN
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            playerService.login(deviceId, country, clientVersion);
        });

        assertTrue(exception.getMessage().contains("FORCE_UPDATE_REQUIRED"), "Hata mesajı FORCE_UPDATE_REQUIRED içermeli");

        // Sürüm yetersiz olduğu için veritabanına asla sorgu atılmamalı
        verify(configService, times(1)).isVersionSupported(clientVersion);
        verify(playerRepository, never()).findByDeviceId(anyString());
        verify(playerRepository, never()).save(any(Player.class));
    }
}