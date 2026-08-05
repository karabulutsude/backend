package com.jollifiy.gameanalytics.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jollifiy.gameanalytics.dto.LoginRequest;
import com.jollifiy.gameanalytics.entity.Player;
import com.jollifiy.gameanalytics.repository.PlayerRepository;
import com.jollifiy.gameanalytics.service.ConfigService;
import com.jollifiy.gameanalytics.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PlayerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlayerRepository playerRepository;

    // --- UNIT TESTLER İÇİN MOCK TANIMLARI ---
    @Mock
    private PlayerRepository playerServiceMockRepository;

    @Mock
    private ConfigService configService; // <-- PlayerService bağımlılığı için eklendi

    @InjectMocks
    private PlayerService playerService;

    @BeforeEach
    void setUp() {
        playerRepository.deleteAll();
    }

    // ==========================================
    //  İNTEGRASYON TESTLERİ (MockMvc & H2 DB)
    // ==========================================

    @Test
    @DisplayName("POST /player/login - Yeni cihaz ile giris yapildiginda oyuncu olusturulmali ve playerId donmeli")
    void shouldLoginOrCreatePlayer() throws Exception {
        // Hazırlık: Giriş isteği DTO nesnesi oluşturulur
        LoginRequest request = new LoginRequest();
        request.setDeviceId("device-uuid-123");
        request.setCountry("TR");
        request.setClientVersion("1.0.0"); // <-- Sürüm bilgisi eklendi

        // Eylem & Doğrulama: /player/login adresine POST isteği simüle edilir
        mockMvc.perform(post("/player/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerId", is(notNullValue())));
    }

    // ==========================================
    //  BİRİM (UNIT) TESTLERİ (Mockito)
    // ==========================================

    @Test
    @DisplayName("[Unit] Cihaz ID sistemde yoksa yeni oyuncu olusturulmali")
    void login_ShouldCreateNewPlayer_WhenDeviceDoesNotExist() {
        String deviceId = "device-123";
        String country = "TR";
        String clientVersion = "1.0.0";

        when(configService.isVersionSupported(clientVersion)).thenReturn(true);

        // Cihazın daha önce kayıtlı olmadığı durumu simüle edilir (Optional.empty)
        when(playerServiceMockRepository.findByDeviceId(deviceId))
                .thenReturn(Optional.empty());

        when(playerServiceMockRepository.save(any(Player.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN: Servisin login metodu 3 parametre ile çağrılır
        Player result = playerService.login(deviceId, country, clientVersion);

        // THEN: Yeni oyuncunun başarıyla oluşturulduğu doğrulanır
        assertNotNull(result, "Dönen oyuncu nesnesi null olmamalı");
        assertEquals(deviceId, result.getDeviceId(), "Cihaz ID eşleşmeli");
        assertEquals(country, result.getCountry(), "Ülke eşleşmeli");
        assertNotNull(result.getPlayerId(), "Yeni rastgele Player ID üretilmeli");

        verify(configService, times(1)).isVersionSupported(clientVersion);
        verify(playerServiceMockRepository, times(1)).save(any(Player.class));
    }

    @Test
    @DisplayName("[Unit] Cihaz ID zaten varsa ve ulke degismediyse mevcut oyuncu donmeli")
    void login_ShouldReturnExistingPlayer_WhenDeviceExistsAndCountryUnchanged() {
        String deviceId = "device-123";
        String country = "TR";
        String clientVersion = "1.0.0";

        when(configService.isVersionSupported(clientVersion)).thenReturn(true);

        // Zaten kayıtlı olan mevcut oyuncu nesnesi hazırlanır
        Player existingPlayer = new Player();
        existingPlayer.setPlayerId("existing-uuid-123");
        existingPlayer.setDeviceId(deviceId);
        existingPlayer.setCountry(country);

        when(playerServiceMockRepository.findByDeviceId(deviceId))
                .thenReturn(Optional.of(existingPlayer));

        Player result = playerService.login(deviceId, country, clientVersion);

        assertNotNull(result);
        assertEquals("existing-uuid-123", result.getPlayerId());

        verify(configService, times(1)).isVersionSupported(clientVersion);
        // Ülke değişmediği için save metodunun hiç çağrılmamış olması gerekir
        verify(playerServiceMockRepository, never()).save(any(Player.class));
    }

    @Test
    @DisplayName("[Unit] Cihaz ID zaten varsa ve ulke degistiyse ulke guncellenip kaydedilmeli")
    void login_ShouldUpdateCountyOfExistingPlayer_WhenDeviceExistsAndCountryChanged() {
        String deviceId = "device-123";
        String newCountry = "US";
        String clientVersion = "1.0.0";

        when(configService.isVersionSupported(clientVersion)).thenReturn(true);

        Player existingPlayer = new Player();
        existingPlayer.setPlayerId("existing-uuid-123");
        existingPlayer.setDeviceId(deviceId);
        existingPlayer.setCountry("TR");

        when(playerServiceMockRepository.findByDeviceId(deviceId))
                .thenReturn(Optional.of(existingPlayer));

        when(playerServiceMockRepository.save(any(Player.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Player result = playerService.login(deviceId, newCountry, clientVersion);

        assertNotNull(result);
        assertEquals("existing-uuid-123", result.getPlayerId());
        assertEquals(newCountry, result.getCountry(), "Ülke yeni gelen değerle güncellenmeli");

        verify(configService, times(1)).isVersionSupported(clientVersion);
        // Ülke güncellendiği için save metodu 1 kez çağrılmalıdır
        verify(playerServiceMockRepository, times(1)).save(any(Player.class));
    }
}