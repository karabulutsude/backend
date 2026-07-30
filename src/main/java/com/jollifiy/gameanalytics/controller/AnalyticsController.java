package com.jollifiy.gameanalytics.controller;

import com.jollifiy.gameanalytics.dto.AnalyticsRequest;
import com.jollifiy.gameanalytics.entity.Analytics;
import com.jollifiy.gameanalytics.service.AnalyticsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @PostMapping("/send")
    public Analytics createEvent(@RequestBody AnalyticsRequest request) {
        return analyticsService.saveEvent(request);
    }

    @GetMapping
    public List<Analytics> getAllEvents() {
        return analyticsService.getAllEvents();
    }
}