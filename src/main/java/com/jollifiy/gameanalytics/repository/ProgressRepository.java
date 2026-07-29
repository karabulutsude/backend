package com.jollifiy.gameanalytics.repository;

import com.jollifiy.gameanalytics.entity.Progress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProgressRepository extends JpaRepository<Progress, Long> {

    Optional<Progress> findByPlayerId(String playerId); //Bu metodun görevi: "Bu oyuncunun daha önce Progress kaydı var mı?"

}