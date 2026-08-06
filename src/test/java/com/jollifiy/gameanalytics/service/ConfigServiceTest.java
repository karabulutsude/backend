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

    // ==========================================
    //  getConfigValue Testleri
    // ==========================================

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

    // ==========================================
    //  isVersionSupported Testleri (Tam 4 Adet)
    // ==========================================

    @Test
    @DisplayName("[isVersionSupported - 1] Veritabanında minimum sürüm tanımlı değilse her sürüm desteklenmeli")
    void isVersionSupported_ShouldReturnTrue_WhenMinVersionNotConfigured() {
        when(configRepository.findByConfigKey("MIN_APP_VERSION")).thenReturn(Optional.empty());

        boolean result = configService.isVersionSupported("1.0.0");

        assertTrue(result, "Min sürüm yoksa her sürüm desteklenmeli");
    }

    @Test
    @DisplayName("[isVersionSupported - 2] İstemci sürümü null veya boş gönderildiyse desteklenmiyor olmalı")
    void isVersionSupported_ShouldReturnFalse_WhenClientVersionIsNullEmpty() {
        AppConfig appConfig = new AppConfig();
        appConfig.setConfigKey("MIN_APP_VERSION");
        appConfig.setConfigValue("1.0.0");
        when(configRepository.findByConfigKey("MIN_APP_VERSION")).thenReturn(Optional.of(appConfig));

        assertFalse(configService.isVersionSupported(null), "Null sürüm desteklenmemeli");
        assertFalse(configService.isVersionSupported(""), "Boş sürüm desteklenmemeli");
    }

    @Test
    @DisplayName("[isVersionSupported - 3] İstemci sürümü sistemin minimum sürümünden büyük veya eşitse desteklenmeli")
    void isVersionSupported_ShouldReturnTrue_WhenClientVersionIsHigherOrEqual() {
        AppConfig appConfig = new AppConfig();
        appConfig.setConfigKey("MIN_APP_VERSION");
        appConfig.setConfigValue("1.1.0");
        when(configRepository.findByConfigKey("MIN_APP_VERSION")).thenReturn(Optional.of(appConfig));

        assertTrue(configService.isVersionSupported("1.1.0"));
        assertTrue(configService.isVersionSupported("2.0.0"));
    }

    @Test
    @DisplayName("[isVersionSupported - 4] İstemci sürümü sistemin minimum sürümünden küçükse desteklenmemeli")
    void isVersionSupported_ShouldReturnFalse_WhenClientVersionIsLower() {
        AppConfig appConfig = new AppConfig();
        appConfig.setConfigKey("MIN_APP_VERSION");
        appConfig.setConfigValue("1.1.0");
        when(configRepository.findByConfigKey("MIN_APP_VERSION")).thenReturn(Optional.of(appConfig));

        assertFalse(configService.isVersionSupported("1.0.9"));
        assertFalse(configService.isVersionSupported("0.9.5"));
    }

    // ==========================================
    //  compareVersions & Çoklu Format Testleri
    // ==========================================

    /*
    @ParameterizedTest(name = "Min Sürüm: {0} | İstemci Sürüm: {1} -> Beklenen Sonuç: {2}")
    @CsvSource({
            "1.1.0, 1.1.1, true",      // Üç basamaklı vs üç basamaklı (büyük)
            "1.1, 1.1.1, true",        // İki basamaklı vs üç basamaklı
            "2.1, 1.9.9, false",       // İki basamaklı vs üç basamaklı (küçük)
            "1.1.1, 1.1, false",       // Üç basamaklı vs iki basamaklı (eksik segment -> 0 tamamlanır)
            "1.1.0, 1.1.1-beta, true", // Ek/Harf içeren versiyon testleri
            "1.0.0, 1.0.0-RC1, true",  // Release candidate testleri
            "2.0.0, 1.9.8, false"      // Farklı ana sürüm kıyaslaması
    })
    @DisplayName("[compareVersions] Farklı basamak uzunlukları ve formatlar çoklu senaryolarla test edilmeli")
    void compareVersions_ShouldHandleVariousFormatsAndLengths(String minVersion, String clientVersion, boolean expectedResult) {
        AppConfig appConfig = new AppConfig();
        appConfig.setConfigKey("MIN_APP_VERSION");
        appConfig.setConfigValue(minVersion);
        when(configRepository.findByConfigKey("MIN_APP_VERSION")).thenReturn(Optional.of(appConfig));

        boolean result = configService.isVersionSupported(clientVersion);

        assertEquals(expectedResult, result, "Sürüm kıyaslama mantığı hatalı çalışıyor: " + clientVersion + " vs min " + minVersion);
    }

     */
    // ==========================================
    //  Klasik Çoklu Sürüm Kıyaslama Testleri
    // ==========================================

    @Test
    @DisplayName("Üç basamaklı birinci sürüm, üç basamaklı ikinci sürümden büyükse pozitif değer dönmeli")
    void compareVersions_ShouldReturnPositive_WhenFirstVersionIsHigher() {
        int result = configService.compareVersions("1.1.1", "1.1.0");

        assertTrue(result > 0);
    }

    @Test
    @DisplayName("İki basamaklı sürüm karşısında üç basamaklı sürüm gelirse pozitif değer dönmeli")
    void compareVersions_ShouldReturnPositive_WhenComparingThreeDigitWithTwoDigit() {
        int result = configService.compareVersions("1.1.1", "1.1");

        assertTrue(result > 0);
    }

    @Test
    @DisplayName("Üç basamaklı sürüm karşısında daha kısa (eksik basamaklı) sürüm küçük kalırsa negatif değer dönmeli")
    void compareVersions_ShouldReturnNegative_WhenComparingShorterVersion() {
        int result = configService.compareVersions("1.1", "1.1.1");

        assertTrue(result < 0);
    }

    @Test
    @DisplayName("Harf veya ek içeren versiyonlar (örn: 1.1.1-beta) doğru şekilde temizlenip karşılaştırılabilmeli")
    void compareVersions_ShouldHandleVersionWithSuffix() {
        int result = configService.compareVersions("1.1.1-beta", "1.1.0");

        assertTrue(result > 0);
    }
}
