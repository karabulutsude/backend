package com.jollifiy.gameanalytics.service;

import com.jollifiy.gameanalytics.entity.Player;
import com.jollifiy.gameanalytics.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final ConfigService configService;

    public PlayerService(PlayerRepository playerRepository, ConfigService configService) {
        this.playerRepository = playerRepository;
        this.configService = configService;
    }

    // Vaadin tablosu için tüm oyuncuları listeleyen metot
    public List<Player> findAll() {
        return playerRepository.findAll();
    }

    // Vaadin için oyuncu kaydetme / güncelleme metodu
    public Player save(Player player) {
        if (player.getPlayerId() == null || player.getPlayerId().isEmpty()) {
            player.setPlayerId(UUID.randomUUID().toString());
        }
        return playerRepository.save(player);
    }

    // Vaadin için oyuncu silme metodu
    public void delete(Player player) {
        playerRepository.delete(player);
    }

    public Player login(String deviceId, String country, String clientVersion) {
        log.info("Oyuncu giriş isteği alındı. Device ID: {}, Ülke: {}, Sürüm: {}", deviceId, country, clientVersion);

        // Versiyon Kontrolü (Force Update Kuralı)
        boolean isSupported = configService.isVersionSupported(clientVersion);
        if (!isSupported) {
            log.warn("Desteklenmeyen sürüm ile giriş denemesi! Device ID: {}, Client Sürüm: {}", deviceId, clientVersion);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "FORCE_UPDATE_REQUIRED: Uygulamanız güncel değil, lütfen güncelleyin."
            );
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