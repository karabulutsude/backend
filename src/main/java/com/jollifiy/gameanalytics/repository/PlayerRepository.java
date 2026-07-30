package com.jollifiy.gameanalytics.repository;

import com.jollifiy.gameanalytics.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByDeviceId(String deviceId);
    Optional<Player> findByPlayerId(String playerId);
}