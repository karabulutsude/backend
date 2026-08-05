package com.jollifiy.gameanalytics.entity;

import jakarta.persistence.*;

// Bu sınıfın bir veritabanı tablosuna karşılık gelen JPA Entity'si olduğunu belirtir
@Entity
// Veritabanında oluşturulacak tablonun adının "app_config" olacağını tanımlar
@Table(name = "app_config")
public class AppConfig {

    // Tablonun benzersiz kimlik (Primary Key) kolonunu belirtir
    @Id
    // ID değerinin veritabanı tarafından otomatik olarak artırılacağını (Auto Increment) söyler
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Konfigürasyonun anahtar adı (Örn: "MIN_APP_VERSION"). Boş geçilemez ve benzersiz olmalıdır.
    @Column(nullable = false, unique = true)
    private String configKey;

    // Konfigürasyonun karşılığı olan değer (Örn: "1.0.0"). Boş geçilemez.
    @Column(nullable = false)
    private String configValue;

    // --- GETTER VE SETTER METOTLARI ---
    // Bu metotlar, özel (private) değişkenlere dışarıdan erişmek ve değer atamak için kullanılır.

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }
}