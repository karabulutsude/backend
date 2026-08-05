package com.jollifiy.gameanalytics.controller;

import com.jollifiy.gameanalytics.dto.LoginRequest;
import com.jollifiy.gameanalytics.dto.LoginResponse;
import com.jollifiy.gameanalytics.entity.Player;
import com.jollifiy.gameanalytics.service.PlayerService;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/player")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        // request nesnesinden gelen clientVersion bilgisi service katmanına iletiliyor
        Player player = playerService.login(
                request.getDeviceId(),
                request.getCountry(),
                request.getClientVersion()
        );

        LoginResponse response = new LoginResponse();
        response.setPlayerId(player.getPlayerId());

        return response;
    }
}