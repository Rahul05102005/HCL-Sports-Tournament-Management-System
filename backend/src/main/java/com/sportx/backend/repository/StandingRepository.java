package com.sportx.backend.repository;

import com.sportx.backend.entity.StandingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StandingRepository extends JpaRepository<StandingEntity, Long> {

    @Query("SELECT s FROM StandingEntity s WHERE s.tournament.id = :tournamentId ORDER BY s.points DESC, s.goalDifference DESC, s.scoreFor DESC")
    List<StandingEntity> findRankedStandingsByTournamentId(@Param("tournamentId") Long tournamentId);

    Optional<StandingEntity> findByTournamentIdAndTeamId(Long tournamentId, Long teamId);
}
