package com.sportx.backend.repository;

import com.sportx.backend.entity.AnnouncementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<AnnouncementEntity, Long> {
    List<AnnouncementEntity> findByTournamentIdOrderByCreatedAtDesc(Long tournamentId);
}
