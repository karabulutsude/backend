package com.jollifiy.gameanalytics;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // Test profilini devreye sokar
class GameAnalyticsApplicationTests {

    @Test
    void contextLoads() {
        // Bu test sadece Spring Context'in başarılı bir şekilde ayağa kalkıp kalkmadığını kontrol eder.
    }
}