package com.sportx.backend.repository;

import com.sportx.backend.entity.MatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<MatchEntity, Long> {

    Optional<MatchEntity> findByFixtureId(Long fixtureId);

    @Query("SELECT m FROM MatchEntity m WHERE m.fixture.tournament.id = :tournamentId ORDER BY m.fixture.scheduledTime ASC")
    List<MatchEntity> findByTournamentId(@Param("tournamentId") Long tournamentId);

    @Query("SELECT m FROM MatchEntity m WHERE m.matchStatus = 'LIVE'")
    List<MatchEntity> findLiveMatches();

    @Query("SELECT m FROM MatchEntity m WHERE m.fixture.tournament.id = :tournamentId AND m.matchStatus = 'COMPLETED'")
    List<MatchEntity> findCompletedMatchesByTournament(@Param("tournamentId") Long tournamentId);
}
