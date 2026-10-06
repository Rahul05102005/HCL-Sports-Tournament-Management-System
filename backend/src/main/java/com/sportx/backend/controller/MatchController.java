package com.sportx.backend.controller;

import com.sportx.backend.dto.common.ApiResponse;
import com.sportx.backend.dto.match.*;
import com.sportx.backend.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Matches & Scoring", description = "Endpoints for fixture scheduling, live score updates, timeline events, and match finalization")
@CrossOrigin(origins = "*")
public class MatchController {

    private final MatchService matchService;

    @PostMapping("/fixtures")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @Operation(summary = "Schedule Fixture", description = "Schedule a fixture with conflict detection (rest periods and venue availability)")
    public ResponseEntity<ApiResponse<FixtureResponseDto>> createFixture(@Valid @RequestBody FixtureCreateRequest request) {
        FixtureResponseDto fixture = matchService.createFixture(request);
        return new ResponseEntity<>(ApiResponse.ok("Fixture scheduled successfully", fixture), HttpStatus.CREATED);
    }

    @GetMapping("/tournaments/{tournamentId}/matches")
    @Operation(summary = "List Tournament Matches", description = "Retrieve all scheduled, live, and completed matches for a tournament")
    public ResponseEntity<ApiResponse<List<MatchResponseDto>>> getMatchesByTournament(@PathVariable Long tournamentId) {
        List<MatchResponseDto> matches = matchService.getMatchesByTournament(tournamentId);
        return ResponseEntity.ok(ApiResponse.ok("Matches retrieved", matches));
    }

    @GetMapping("/matches/live")
    @Operation(summary = "Get Live Matches", description = "Retrieve all matches that are currently in LIVE status")
    public ResponseEntity<ApiResponse<List<MatchResponseDto>>> getLiveMatches() {
        List<MatchResponseDto> matches = matchService.getLiveMatches();
        return ResponseEntity.ok(ApiResponse.ok("Live matches retrieved", matches));
    }

    @GetMapping("/matches/{id}")
    @Operation(summary = "Get Match Details", description = "Retrieve details and current score of a specific match")
    public ResponseEntity<ApiResponse<MatchResponseDto>> getMatchById(@PathVariable Long id) {
        MatchResponseDto match = matchService.getMatchById(id);
        return ResponseEntity.ok(ApiResponse.ok("Match retrieved", match));
    }

    @PostMapping("/matches/{id}/score-events")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER', 'REFEREE')")
    @Operation(summary = "Log Score Event", description = "Log a goal, point, or card; updates running score and match status to LIVE")
    public ResponseEntity<ApiResponse<ScoreEventResponseDto>> addScoreEvent(
            @PathVariable Long id,
            @Valid @RequestBody ScoreEventRequest request
    ) {
        ScoreEventResponseDto event = matchService.addScoreEvent(id, request);
        return new ResponseEntity<>(ApiResponse.ok("Score event recorded", event), HttpStatus.CREATED);
    }

    @GetMapping("/matches/{id}/score-events")
    @Operation(summary = "Get Score Events", description = "Retrieve chronological timeline events for a match")
    public ResponseEntity<ApiResponse<List<ScoreEventResponseDto>>> getScoreEvents(@PathVariable Long id) {
        List<ScoreEventResponseDto> events = matchService.getScoreEvents(id);
        return ResponseEntity.ok(ApiResponse.ok("Match events retrieved", events));
    }

    @PostMapping("/matches/{id}/finalize")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER', 'REFEREE')")
    @Operation(summary = "Finalize Match", description = "Finalizes the match, locks the scores, marks the winner, and updates standings tables atomically in an ACID transaction")
    public ResponseEntity<ApiResponse<MatchResponseDto>> finalizeMatch(
            @PathVariable Long id,
            @Valid @RequestBody FinalizeMatchRequest request
    ) {
        MatchResponseDto match = matchService.finalizeMatch(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Match finalized and standings updated", match));
    }
}
