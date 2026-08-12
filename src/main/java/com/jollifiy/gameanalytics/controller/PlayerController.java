package com.jollifiy.gameanalytics.controller;

import com.jollifiy.gameanalytics.dto.LoginRequest;
import com.jollifiy.gameanalytics.dto.LoginResponse;
import com.jollifiy.gameanalytics.entity.Player;
import com.jollifiy.gameanalytics.service.ConfigService;
import com.jollifiy.gameanalytics.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/players")
public class PlayerController {

    private final PlayerService playerService;
    private final ConfigService configService;

    public PlayerController(PlayerService playerService, ConfigService configService) {
        this.playerService = playerService;
        this.configService = configService;
    }

    @GetMapping
    public ResponseEntity<List<Player>> getAllPlayers() {
        return ResponseEntity.ok(playerService.findAll());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        boolean isSupported = configService.isVersionSupported(request.getClientVersion());

        if (!isSupported) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("FORCE_UPDATE_REQUIRED: Sürümünüz çok eski, lütfen güncelleyin.");
        }

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