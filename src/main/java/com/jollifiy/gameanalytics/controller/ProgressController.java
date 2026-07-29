package com.jollifiy.gameanalytics.controller;

import com.jollifiy.gameanalytics.dto.ProgressSaveRequest;
import com.jollifiy.gameanalytics.entity.Progress;
import com.jollifiy.gameanalytics.service.ProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/progress")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    // 1. Profil ekranı için progress verisini çeken endpoint: /progress/get?playerId=...
    @GetMapping("/get")
    public ResponseEntity<Progress> getProgress(@RequestParam String playerId) {
        Progress progress = progressService.getProgressByPlayerId(playerId);
        if (progress != null) {
            return ResponseEntity.ok(progress);
        }
        return ResponseEntity.notFound().build();
    }

    // 2. Oyundan çıkışta veya level geçildiğinde skor/coin kaydeden endpoint: /progress/save
    @PostMapping("/save")
    public ResponseEntity<Progress> saveProgress(@RequestBody ProgressSaveRequest request) {
        Progress savedProgress = progressService.saveProgress(request);
        return ResponseEntity.ok(savedProgress);
    }
}