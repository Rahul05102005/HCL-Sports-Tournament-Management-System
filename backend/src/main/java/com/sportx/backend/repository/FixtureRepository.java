package com.sportx.backend.repository;

import com.sportx.backend.entity.FixtureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FixtureRepository extends JpaRepository<FixtureEntity, Long> {

    List<FixtureEntity> findByTournamentIdOrderByScheduledTimeAsc(Long tournamentId);

    List<FixtureEntity> findByRefereeId(Long refereeId);

    @Query("SELECT f FROM FixtureEntity f WHERE f.venue.id = :venueId AND f.scheduledTime BETWEEN :start AND :end")
    List<FixtureEntity> findVenueConflicts(
            @Param("venueId") Long venueId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT f FROM FixtureEntity f WHERE (f.homeTeam.id = :teamId OR f.awayTeam.id = :teamId) AND f.scheduledTime BETWEEN :start AND :end")
    List<FixtureEntity> findTeamConflicts(
            @Param("teamId") Long teamId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
