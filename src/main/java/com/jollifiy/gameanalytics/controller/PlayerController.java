package com.jollifiy.gameanalytics.controller;

import com.jollifiy.gameanalytics.dto.LoginRequest;
import com.jollifiy.gameanalytics.dto.LoginResponse;
import com.jollifiy.gameanalytics.entity.Player;
import com.jollifiy.gameanalytics.service.PlayerService;
import org.springframework.web.bind.annotation.*;

@RestController  // Spring'e bu sınıfın HTTP isteklerini karşılayacağını söylüyoruz
@RequestMapping("/player") // Bütün istekler "localhost:8080/player" ile başlayacak
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping("/login") // Endpoint: http://localhost:8080/player/login
    public LoginResponse login(@RequestBody LoginRequest request) {

        // Unity'den gelen deviceId ve country verisini Service'e aktarıyoruz
        Player player = playerService.login(request.getDeviceId(), request.getCountry());

        LoginResponse response = new LoginResponse();
        response.setPlayerId(player.getPlayerId());

        return response;
    }
}