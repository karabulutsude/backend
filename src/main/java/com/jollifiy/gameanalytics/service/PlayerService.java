/*
package com.jollifiy.gameanalytics.service;


import com.jollifiy.gameanalytics.entity.Player;
import com.jollifiy.gameanalytics.repository.PlayerRepository;
import org.springframework.stereotype.Service;
 // import com.jollifiy.gameanalytics.dto.ProfileRequest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player login(String deviceId) {

        Optional<Player> existingPlayer = playerRepository.findByDeviceId(deviceId);  // Repository'ye diyoruz ki: "Bu deviceId ile kayıt var mı?"

        if (existingPlayer.isPresent()) { // Eğer varsa... Yani oyuncu daha önce oyuna girmiş.
            return existingPlayer.get(); // Eski oyuncuyu döndür.
        }

        Player newPlayer = new Player(); // Bulunamadıysa, Yeni oyuncu oluşturuyoruz.

        newPlayer.setDeviceId(deviceId);  // Unity'den gelen cihaz kimliğini kaydediyoruz.

        String playerId = UUID.randomUUID()   // 8 karakterlik benzersiz Player ID oluşturuyoruz.
                .toString()
                .replace("-", "")
                .substring(0, 8);

        newPlayer.setPlayerId(playerId);
        newPlayer.setCreatedAt(LocalDateTime.now()); // Hesabın oluşturulma zamanını backend belirliyor.

        return playerRepository.save(newPlayer); // Oyuncuyu veritabanına kaydediyoruz ve kaydedilen nesneyi geri döndürüyoruz.
    }
}

//
    public Player saveProfile(ProfileRequest request) {

        Player player = playerRepository.findByPlayerId(request.getPlayerId())
                .orElseThrow(() -> new RuntimeException("Player bulunamadı."));

        player.setFirstName(request.getFirstName());
        player.setLastName(request.getLastName());
        player.setUsername(request.getUsername());
        player.setCountry(request.getCountry());

        return playerRepository.save(player);
    }

 */


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

        // Repository'ye diyoruz ki: "Bu deviceId ile kayıt var mı?"
        Optional<Player> existingPlayer = playerRepository.findByDeviceId(deviceId);


        // Eğer varsa... Yani oyuncu daha önce oyuna girmiş.
        if (existingPlayer.isPresent()) {
            Player player = existingPlayer.get();

            // İsteğe bağlı: Ülke bilgisi boş geldiyse veya değiştiyse güncelleyelim
            if (country != null && !country.equals(player.getCountry())) {
                player.setCountry(country);
                return playerRepository.save(player);
            }

            return player; // Eski oyuncuyu döndür.
        }

        // Bulunamadıysa, yeni oyuncu oluşturuyoruz.
        Player newPlayer = new Player();
        newPlayer.setDeviceId(deviceId);  // Unity'den gelen cihaz kimliği
        newPlayer.setCountry(country);    // Unity'den gelen ülke bilgisi

        // 32 karakterlik benzersiz Player ID oluşturuyoruz.
        String playerId = UUID.randomUUID().toString();


        newPlayer.setPlayerId(playerId);
        newPlayer.setCreatedAt(LocalDateTime.now()); // Hesabın oluşturulma zamanı

        return playerRepository.save(newPlayer); // Oyuncuyu veritabanına kaydedip döndürüyoruz.
    }
}

