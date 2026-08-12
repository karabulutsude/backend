package com.jollifiy.gameanalytics.controller;

import com.jollifiy.gameanalytics.dto.ProgressSaveRequest;
import com.jollifiy.gameanalytics.entity.Progress;
import com.jollifiy.gameanalytics.service.ProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/progress")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @GetMapping
    public ResponseEntity<List<Progress>> getAllProgress() {
        return ResponseEntity.ok(progressService.findAll());
    }

    @GetMapping("/get")
    public ResponseEntity<Progress> getProgress(@RequestParam String playerId) {
        Progress progress = progressService.getProgressByPlayerId(playerId);
        if (progress != null) {
            return ResponseEntity.ok(progress);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/save")
    public ResponseEntity<Progress> saveProgress(@RequestBody ProgressSaveRequest request) {
        Progress savedProgress = progressService.saveProgress(request);
        return ResponseEntity.ok(savedProgress);
    }
}