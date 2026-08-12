package com.jollifiy.gameanalytics.controller;

import com.jollifiy.gameanalytics.entity.AppConfig;
import com.jollifiy.gameanalytics.repository.ConfigRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/config")
public class ConfigController {

    private final ConfigRepository configRepository;

    public ConfigController(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    @GetMapping
    public ResponseEntity<List<AppConfig>> getAllConfigs() {
        return ResponseEntity.ok(configRepository.findAll());
    }
}