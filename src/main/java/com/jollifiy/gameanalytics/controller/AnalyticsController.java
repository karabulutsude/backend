/*
package com.jollifiy.gameanalytics.controller;

import com.jollifiy.gameanalytics.entity.Analytics;
import com.jollifiy.gameanalytics.service.AnalyticsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @PostMapping
    public Analytics createEvent(@RequestBody Analytics analytics) {
        return analyticsService.saveEvent(analytics);
    }

    @GetMapping
    public List<Analytics> getAllEvents() {
        return analyticsService.getAllEvents();
    }
}
 */

package com.jollifiy.gameanalytics.controller;

import com.jollifiy.gameanalytics.dto.AnalyticsRequest;
import com.jollifiy.gameanalytics.entity.Analytics;
import com.jollifiy.gameanalytics.service.AnalyticsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    // Unity'den gelen /analytics/send isteğini karşılayacak şekilde güncellendi
    @PostMapping("/send")
    public Analytics createEvent(@RequestBody AnalyticsRequest request) {
        return analyticsService.saveEvent(request); // Servis metodunun AnalyticsRequest aldığından emin ol
    }

    @GetMapping
    public List<Analytics> getAllEvents() {
        return analyticsService.getAllEvents();
    }
}