package com.jollifiy.gameanalytics.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jollifiy.gameanalytics.dto.LoginRequest;
import com.jollifiy.gameanalytics.entity.AppConfig;
import com.jollifiy.gameanalytics.entity.Player;
import com.jollifiy.gameanalytics.repository.ConfigRepository;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PlayerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ConfigRepository configRepository;

    @Mock
    private PlayerRepository playerServiceMockRepository;

    @Mock
    private ConfigService configService;

    @InjectMocks
    private PlayerService playerService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        playerRepository.deleteAll();
        configRepository.deleteAll();
    }

    @Test
    @WithMockUser
    @DisplayName("POST /player/login - Yeni cihaz ile giris yapildiginda oyuncu olusturulmali ve playerId donmeli")
    void shouldLoginOrCreatePlayer() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setDeviceId("device-uuid-123");
        request.setCountry("TR");
        request.setClientVersion("1.0.0");

        mockMvc.perform(post("/player/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())) // <-- Eklendi
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerId", is(notNullValue())));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /player/login - Desteklenmeyen eski sürüm ile giris yapildiginda 400 Bad Request ve FORCE_UPDATE_REQUIRED dönmeli")
    void shouldReturnBadRequestWhenVersionNotSupported() throws Exception {
        AppConfig minVersionConfig = new AppConfig();
        minVersionConfig.setConfigKey("MIN_APP_VERSION");
        minVersionConfig.setConfigValue("1.0.0");
        configRepository.save(minVersionConfig);

        LoginRequest request = new LoginRequest();
        request.setDeviceId("device-uuid-old");
        request.setCountry("TR");
        request.setClientVersion("0.5.0");

        MvcResult result = mockMvc.perform(post("/player/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())) // <-- Eklendi
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertTrue(responseBody.contains("FORCE_UPDATE_REQUIRED"));
    }

    @Test
    @DisplayName("[Unit] Cihaz ID sistemde yoksa yeni oyuncu olusturulmali")
    void login_ShouldCreateNewPlayer_WhenDeviceDoesNotExist() {
        String deviceId = "device-123";
        String country = "TR";
        String clientVersion = "1.0.0";

        when(configService.isVersionSupported(clientVersion)).thenReturn(true);
        when(playerServiceMockRepository.findByDeviceId(deviceId))
                .thenReturn(Optional.empty());
        when(playerServiceMockRepository.save(any(Player.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Player result = playerService.login(deviceId, country, clientVersion);

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
        verify(playerServiceMockRepository, times(1)).save(any(Player.class));
    }
}