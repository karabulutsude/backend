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

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player login(String deviceId, String country) {
        log.info("Oyuncu giriş isteği alındı. Device ID: {}, Ülke: {}", deviceId, country);
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