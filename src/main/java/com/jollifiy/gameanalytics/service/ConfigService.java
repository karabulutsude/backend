package com.jollifiy.gameanalytics.service;

import com.jollifiy.gameanalytics.entity.AppConfig;
import com.jollifiy.gameanalytics.repository.ConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConfigService {

    @Autowired
    private ConfigRepository configRepository;

    // Vaadin tablosu için tüm konfigürasyonları listeleyen metot
    public List<AppConfig> findAll() {
        return configRepository.findAll();
    }

    // Vaadin arayüzü için konfigürasyon kaydetme / güncelleme
    public AppConfig save(AppConfig appConfig) {
        return configRepository.save(appConfig);
    }

    // Vaadin arayüzü için konfigürasyon silme
    public void delete(AppConfig appConfig) {
        configRepository.delete(appConfig);
    }

    public String getConfigValue(String key) {
        Optional<AppConfig> config = configRepository.findByConfigKey(key);
        return config.map(AppConfig::getConfigValue).orElse(null);
    }

    public boolean isVersionSupported(String clientVersion) {
        String minVersion = getConfigValue("MIN_APP_VERSION");

        if (minVersion == null || minVersion.isEmpty()) {
            return true;
        }

        if (clientVersion == null || clientVersion.isEmpty()) {
            return false;
        }

        return compareVersions(clientVersion, minVersion) >= 0;
    }

    int compareVersions(String v1, String v2) {
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");

        int length = Math.max(parts1.length, parts2.length);
        for (int i = 0; i < length; i++) {
            int p1 = 0;
            if (i < parts1.length) {
                String cleaned1 = parts1[i].replaceAll("[^0-9]", "");
                if (!cleaned1.isEmpty()) {
                    p1 = Integer.parseInt(cleaned1);
                }
            }

            int p2 = 0;
            if (i < parts2.length) {
                String cleaned2 = parts2[i].replaceAll("[^0-9]", "");
                if (!cleaned2.isEmpty()) {
                    p2 = Integer.parseInt(cleaned2);
                }
            }

            if (p1 != p2) {
                return Integer.compare(p1, p2);
            }
        }
        return 0;
    }
}