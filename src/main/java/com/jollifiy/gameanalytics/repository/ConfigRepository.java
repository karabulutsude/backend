package com.jollifiy.gameanalytics.repository;

import com.jollifiy.gameanalytics.entity.AppConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Bu arayüzün bir Spring Data JPA Repository bileşeni olduğunu belirtir
@Repository
public interface ConfigRepository extends JpaRepository<AppConfig, Long> {

    // Verilen configKey değerine (Örn: "MIN_APP_VERSION") göre veritabanında arama yapar.
    // Eğer kayıt bulunamazsa uygulama patlamasın diye sonuç 'Optional' sarmalayıcısı içinde döndürülür.
    Optional<AppConfig> findByConfigKey(String configKey);
}