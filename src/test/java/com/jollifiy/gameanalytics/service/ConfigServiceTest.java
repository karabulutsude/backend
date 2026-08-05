package com.jollifiy.gameanalytics.service;

import com.jollifiy.gameanalytics.entity.AppConfig;
import com.jollifiy.gameanalytics.repository.ConfigRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfigServiceTest {

    @Mock
    private ConfigRepository configRepository;

    @InjectMocks
    private ConfigService configService;

    @Test
    @DisplayName("Veritabanında config anahtarı varsa ilgili değer dönmeli")
    void getConfigValue_ShouldReturnConfigValue_WhenKeyExists() {
        // GIVEN
        String key = "MIN_APP_VERSION";
        String expectedValue = "1.0.0";

        AppConfig appConfig = new AppConfig();
        appConfig.setConfigKey(key);
        appConfig.setConfigValue(expectedValue);

        when(configRepository.findByConfigKey(key)).thenReturn(Optional.of(appConfig));

        // WHEN
        String result = configService.getConfigValue(key);

        // THEN
        assertEquals(expectedValue, result);
        verify(configRepository, times(1)).findByConfigKey(key);
    }

    @Test
    @DisplayName("Veritabanında config anahtarı yoksa null dönmeli")
    void getConfigValue_ShouldReturnNull_WhenKeyDoesNotExist() {
        // GIVEN
        String key = "UNKNOWN_KEY";
        when(configRepository.findByConfigKey(key)).thenReturn(Optional.empty());

        // WHEN
        String result = configService.getConfigValue(key);

        // THEN
        assertNull(result);
        verify(configRepository, times(1)).findByConfigKey(key);
    }

    @Test
    @DisplayName("Veritabanında minimum sürüm tanımlı değilse her sürüme izin verilmeli")
    void isVersionSupported_ShouldReturnTrue_WhenMinVersionNotConfigured() {
        // GIVEN
        when(configRepository.findByConfigKey("MIN_APP_VERSION")).thenReturn(Optional.empty());

        // WHEN
        boolean result = configService.isVersionSupported("1.0.0");

        // THEN
        assertTrue(result, "Min sürüm yoksa her sürüm desteklenmeli");
    }

    @Test
    @DisplayName("İstemci sürümü null veya boş gönderildiyse desteklenmiyor olmalı")
    void isVersionSupported_ShouldReturnFalse_WhenClientVersionIsNullEmpty() {
        // GIVEN
        AppConfig appConfig = new AppConfig();
        appConfig.setConfigKey("MIN_APP_VERSION");
        appConfig.setConfigValue("1.0.0");
        when(configRepository.findByConfigKey("MIN_APP_VERSION")).thenReturn(Optional.of(appConfig));

        // WHEN & THEN
        assertFalse(configService.isVersionSupported(null), "Null sürüm desteklenmemeli");
        assertFalse(configService.isVersionSupported(""), "Boş sürüm desteklenmemeli");
    }

    @Test
    @DisplayName("İstemci sürümü sistemin minimum sürümünden büyük veya eşitse desteklenmeli")
    void isVersionSupported_ShouldReturnTrue_WhenClientVersionIsHigherOrEqual() {
        // GIVEN
        AppConfig appConfig = new AppConfig();
        appConfig.setConfigKey("MIN_APP_VERSION");
        appConfig.setConfigValue("1.1.0");
        when(configRepository.findByConfigKey("MIN_APP_VERSION")).thenReturn(Optional.of(appConfig));

        // WHEN & THEN
        // Eşit sürüm
        assertTrue(configService.isVersionSupported("1.1.0"));

        // Daha yüksek ana sürüm
        assertTrue(configService.isVersionSupported("2.0.0"));

        // Daha yüksek alt sürüm (Semantic versioning kıyaslama testi)
        assertTrue(configService.isVersionSupported("1.2.1"));
    }

    @Test
    @DisplayName("İstemci sürümü sistemin minimum sürümünden küçükse desteklenmemeli")
    void isVersionSupported_ShouldReturnFalse_WhenClientVersionIsLower() {
        // GIVEN
        AppConfig appConfig = new AppConfig();
        appConfig.setConfigKey("MIN_APP_VERSION");
        appConfig.setConfigValue("1.1.0");
        when(configRepository.findByConfigKey("MIN_APP_VERSION")).thenReturn(Optional.of(appConfig));

        // WHEN & THEN
        // Daha düşük alt sürüm
        assertFalse(configService.isVersionSupported("1.0.9"));

        // Daha düşük ana sürüm
        assertFalse(configService.isVersionSupported("0.9.5"));
    }
}