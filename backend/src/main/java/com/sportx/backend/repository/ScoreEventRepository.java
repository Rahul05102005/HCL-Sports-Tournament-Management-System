package com.sportx.backend.repository;

import com.sportx.backend.entity.ScoreEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreEventRepository extends JpaRepository<ScoreEventEntity, Long> {
    List<ScoreEventEntity> findByMatchIdOrderByMinuteOccurredAsc(Long matchId);
}
