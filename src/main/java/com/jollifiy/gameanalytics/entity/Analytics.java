/*
package com.jollifiy.gameanalytics.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "analytics")
public class Analytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventName;
    private Integer score;
    private Integer coinCount;
    private Integer level;
    private String playerId;
    private String playTime;
    private String deathReason;
    private String completionPercentage;
    private Integer healthRemaining;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Getter ve Setter Metotları...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public Integer getCoinCount() { return coinCount; }
    public void setCoinCount(Integer coinCount) { this.coinCount = coinCount; }

    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }

    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }

    public String getPlayTime() { return playTime; }
    public void setPlayTime(String playTime) { this.playTime = playTime; }

    public String getDeathReason() { return deathReason; }
    public void setDeathReason(String deathReason) { this.deathReason = deathReason; }

    public String getCompletionPercentage() { return completionPercentage; }
    public void setCompletionPercentage(String completionPercentage) { this.completionPercentage = completionPercentage; }

    public Integer getHealthRemaining() { return healthRemaining; }
    public void setHealthRemaining(Integer healthRemaining) { this.healthRemaining = healthRemaining; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

 */

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
    private Integer completionPercentage; // Integer olarak güncellendi

    @Column(name = "play_time")
    private Double playTime;              // Double olarak güncellendi

    @Column(name = "health_remaining")
    private Integer healthRemaining;

    @Column(name = "death_reason")
    private String deathReason;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}