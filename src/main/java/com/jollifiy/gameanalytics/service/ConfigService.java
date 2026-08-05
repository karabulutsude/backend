package com.jollifiy.gameanalytics.service;

import com.jollifiy.gameanalytics.entity.AppConfig;
import com.jollifiy.gameanalytics.repository.ConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

// Bu sınıfın bir Spring Service bileşeni olduğunu belirtir
@Service
public class ConfigService {

    // Veritabanından konfigürasyon verilerini çekmek için kullanılan repository
    @Autowired
    private ConfigRepository configRepository;

    // Verilen anahtara (Örn: "MIN_APP_VERSION") göre veritabanından değeri çeken metot
    public String getConfigValue(String key) {
        Optional<AppConfig> config = configRepository.findByConfigKey(key);
        // Eğer kayıt varsa değerini döner, yoksa null döner
        return config.map(AppConfig::getConfigValue).orElse(null);
    }

    // İstemciden gelen sürümün, sistemin istediği minimum sürümü karşılayıp karşılamadığını kontrol eden metot
    public boolean isVersionSupported(String clientVersion) {
        // Veritabanından sistemin belirlediği minimum sürüm çekilir
        String minVersion = getConfigValue("MIN_APP_VERSION");

        // Eğer veritabanında henüz bir min version tanımlanmamışsa her sürüme geçici olarak izin verilir
        if (minVersion == null || minVersion.isEmpty()) {
            return true;
        }

        // Eğer kullanıcı sürüm göndermediyse desteklenmiyor kabul edilir
        if (clientVersion == null || clientVersion.isEmpty()) {
            return false;
        }

        // Sürüm karşılaştırması yapılır (clientVersion >= minVersion mi?)
        return compareVersions(clientVersion, minVersion) >= 0;
    }

    // İki sürüm stringini (Örn: "1.2.0" ve "1.1.5") parçalayarak sayısal olarak kıyaslayan yardımcı metot
    private int compareVersions(String v1, String v2) {
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");

        int length = Math.max(parts1.length, parts2.length);
        for (int i = 0; i < length; i++) {
            // Sürüm parçalarındaki sayısal değerler okunur (Harf vb. karakterler temizlenir)
            int p1 = i < parts1.length ? Integer.parseInt(parts1[i].replaceAll("[^0-9]", "")) : 0;
            int p2 = i < parts2.length ? Integer.parseInt(parts2[i].replaceAll("[^0-9]", "")) : 0;

            if (p1 != p2) {
                return Integer.compare(p1, p2); // Fark bulunduysa kıyaslama sonucu dönülür
            }
        }
        return 0; // Sürümler tamamen birbirine eşittir
    }
}