/*
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
@Table(name = "progress")
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "player_id", nullable = false, unique = true, length = 8)
    private String playerId;

    @Column(name = "current_level")
    private Integer currentLevel;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

 */

/*
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
@Table(name = "progress")
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "player_id", nullable = false, unique = true, length = 8)
    private String playerId;

    @Column(name = "current_level")
    private Integer currentLevel;

    @Column(name = "total_coins")
    private Integer totalCoins = 0;

    @Column(name = "total_score")
    private Integer totalScore = 0;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

 */

//

/*
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
@Table(name = "progress")
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "player_id", nullable = false, unique = true, length = 8)
    private String playerId;

    @Column(name = "current_level")
    private Integer currentLevel;

    @Column(name = "total_coins")
    private Integer totalCoins = 0;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
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
@Table(name = "progress")
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "player_id", nullable = false, unique = true)
    private String playerId;

    @Column(name = "current_level")
    private Integer currentLevel;

    @Column(name = "total_coins")
    private Integer totalCoins = 0;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}