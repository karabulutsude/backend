package com.jollifiy.gameanalytics.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "analytics")
public class Analytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "player_id", nullable = false)
    private String playerId;

    @Column(name = "event_name")
    private String eventName;

    @Column(name = "level")
    private Integer level;

    @Column(name = "score")
    private Integer score;

    @Column(name = "coin_count")
    private Integer coinCount;

    @Column(name = "completion_percentage")
    private Integer completionPercentage;

    @Column(name = "play_time")
    private Double playTime;

    @Column(name = "health_remaining")
    private Integer healthRemaining;

    @Column(name = "death_reason")
    private String deathReason;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}