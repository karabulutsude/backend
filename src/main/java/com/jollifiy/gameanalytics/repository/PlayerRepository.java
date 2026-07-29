package com.jollifiy.gameanalytics.repository;

import com.jollifiy.gameanalytics.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {  //Bu sayede otomatik olarak şu metodlara sahip oluyoruz: save(), findAll(), findById(), delete()
    Optional<Player> findByDeviceId(String deviceId); // Login sırasında deviceId ile oyuncu arıyoruz.

    Optional<Player> findByPlayerId(String playerId); // Profil kaydederken playerId ile oyuncu arıyoruz.
    /*
    Burada hiç SQL yazmadık.
    Ama Spring bunu otomatik olarak şuna çeviriyor:

    SELECT *
    FROM player
    WHERE device_id = ?

    Neden Optional<Player>?
    Çünkü aradığımız deviceId veritabanında olabilir de olmayabilir de.
     */

}