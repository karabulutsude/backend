package com.jollifiy.gameanalytics.controller;

import com.jollifiy.gameanalytics.dto.LoginRequest;
import com.jollifiy.gameanalytics.dto.LoginResponse;
import com.jollifiy.gameanalytics.entity.Player;
import com.jollifiy.gameanalytics.service.ConfigService;
import com.jollifiy.gameanalytics.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/player")
public class PlayerController {

    private final PlayerService playerService;
    private final ConfigService configService; // <-- ConfigService eklendi

    public PlayerController(PlayerService playerService, ConfigService configService) {
        this.playerService = playerService;
        this.configService = configService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        // 1. İstemci sürümü destekleniyor mu kontrol et
        boolean isSupported = configService.isVersionSupported(request.getClientVersion());

        if (!isSupported) {
            // Desteklenmiyorsa Unity'nin yakalayacağı hata mesajı ve 400 Bad Request dön
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("FORCE_UPDATE_REQUIRED: Sürümünüz çok eski, lütfen güncelleyin.");
        }

        // 2. Sürüm uygunsa normal login işlemlerine devam et
        Player player = playerService.login(
                request.getDeviceId(),
                request.getCountry(),
                request.getClientVersion()
        );

        LoginResponse response = new LoginResponse();
        response.setPlayerId(player.getPlayerId());

        return ResponseEntity.ok(response);
    }
}