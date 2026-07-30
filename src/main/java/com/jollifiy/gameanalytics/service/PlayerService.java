package com.jollifiy.gameanalytics.service;

import com.jollifiy.gameanalytics.entity.Player;
import com.jollifiy.gameanalytics.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player login(String deviceId, String country) {
        Optional<Player> existingPlayer = playerRepository.findByDeviceId(deviceId);

        if (existingPlayer.isPresent()) {
            Player player = existingPlayer.get();

            if (country != null && !country.equals(player.getCountry())) {
                player.setCountry(country);
                return playerRepository.save(player);
            }

            return player;
        }

        Player newPlayer = new Player();
        newPlayer.setDeviceId(deviceId);
        newPlayer.setCountry(country);

        String playerId = UUID.randomUUID().toString();
        newPlayer.setPlayerId(playerId);
        newPlayer.setCreatedAt(LocalDateTime.now());

        return playerRepository.save(newPlayer);
    }
}