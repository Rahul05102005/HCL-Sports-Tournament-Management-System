package com.sportx.backend;

import com.sportx.backend.dto.match.FinalizeMatchRequest;
import com.sportx.backend.dto.match.MatchResponseDto;
import com.sportx.backend.dto.standing.StandingResponseDto;
import com.sportx.backend.service.MatchService;
import com.sportx.backend.service.StandingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
class MatchServiceTest {

    @Autowired
    private MatchService matchService;

    @Autowired
    private StandingService standingService;

    @Test
    @Transactional
    void testGetLiveMatches() {
        List<MatchResponseDto> liveMatches = matchService.getLiveMatches();
        assertNotNull(liveMatches);
        assertFalse(liveMatches.isEmpty(), "Expected at least 1 seeded live match");
        assertEquals("LIVE", liveMatches.get(0).getMatchStatus());
    }

    @Test
    @Transactional
    void testFinalizeMatchUpdatesStandings() {
        // Find seeded live match (PSG vs KEC, match 2)
        List<MatchResponseDto> liveMatches = matchService.getLiveMatches();
        assertFalse(liveMatches.isEmpty());
        Long matchId = liveMatches.get(0).getId();
        Long tournamentId = liveMatches.get(0).getTournamentId();

        FinalizeMatchRequest request = FinalizeMatchRequest.builder()
                .homeScore(2)
                .awayScore(0)
                .notes("PSG sealed 2-0 victory with a second-half header.")
                .build();

        MatchResponseDto finalized = matchService.finalizeMatch(matchId, request);

        assertEquals("COMPLETED", finalized.getMatchStatus());
        assertEquals(2, finalized.getHomeScore());
        assertEquals(0, finalized.getAwayScore());
        assertFalse(finalized.isDraw());
        assertNotNull(finalized.getWinnerTeamId());

        // Verify standings updated
        List<StandingResponseDto> standings = standingService.getStandingsByTournament(tournamentId);
        assertNotNull(standings);
        assertFalse(standings.isEmpty());
    }
}
