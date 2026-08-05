package com.jollifiy.gameanalytics.service;

import com.jollifiy.gameanalytics.entity.Player;
import com.jollifiy.gameanalytics.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final ConfigService configService; //  Config servisi bağımlılığı eklendi


    public PlayerService(PlayerRepository playerRepository, ConfigService configService) {
        this.playerRepository = playerRepository;
        this.configService = configService; // bu kıskı eklediğm
    }

    // Metoda clientVersion parametresi eklendi
    public Player login(String deviceId, String country, String clientVersion) {
        log.info("Oyuncu giriş isteği alındı. Device ID: {}, Ülke: {}, Sürüm: {}", deviceId, country, clientVersion); //log a version kısmını ekledim

        // Versiyon Kontrolü (Force Update Kuralı)
        boolean isSupported = configService.isVersionSupported(clientVersion);
        if (!isSupported) {
            log.warn("Desteklenmeyen sürüm ile giriş denemesi! Device ID: {}, Client Sürüm: {}", deviceId, clientVersion);
            // Sürüm yetersizse hata fırlatılır (Unity tarafı bunu 'Force Update' olarak ele alacak)
            throw new RuntimeException("FORCE_UPDATE_REQUIRED: Uygulamanız güncel değil, lütfen güncelleyin.");
        }

        Optional<Player> existingPlayer = playerRepository.findByDeviceId(deviceId);

        if (existingPlayer.isPresent()) {
            Player player = existingPlayer.get();

            if (country != null && !country.equals(player.getCountry())) {
                log.info("Oyuncunun ülkesi güncelleniyor. Player ID: {}, Eski Ülke: {}, Yeni Ülke: {}",
                        player.getPlayerId(), player.getCountry(), country);
                player.setCountry(country);
                return playerRepository.save(player);
            }

            log.info("Mevcut oyuncu giriş yaptı. Player ID: {}", player.getPlayerId());
            return player;
        }

        Player newPlayer = new Player();
        newPlayer.setDeviceId(deviceId);
        newPlayer.setCountry(country);

        String playerId = UUID.randomUUID().toString();
        newPlayer.setPlayerId(playerId);
        newPlayer.setCreatedAt(LocalDateTime.now());

        Player savedPlayer = playerRepository.save(newPlayer);
        log.info("Yeni oyuncu başarıyla oluşturuldu. Player ID: {}", savedPlayer.getPlayerId());

        return savedPlayer;
    }
}