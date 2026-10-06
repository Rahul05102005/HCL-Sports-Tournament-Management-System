package com.sportx.backend.controller;

import com.sportx.backend.dto.common.ApiResponse;
import com.sportx.backend.dto.standing.StandingResponseDto;
import com.sportx.backend.service.StandingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/standings")
@RequiredArgsConstructor
@Tag(name = "Standings", description = "Endpoints for tournament standings, points tables, and team rankings")
@CrossOrigin(origins = "*")
public class StandingsController {

    private final StandingService standingService;

    @GetMapping("/tournament/{tournamentId}")
    @Operation(summary = "Get Tournament Standings", description = "Retrieve ranked leaderboard for a tournament (ordered by points, goal diff, goals scored)")
    public ResponseEntity<ApiResponse<List<StandingResponseDto>>> getStandings(@PathVariable Long tournamentId) {
        List<StandingResponseDto> standings = standingService.getStandingsByTournament(tournamentId);
        return ResponseEntity.ok(ApiResponse.ok("Standings retrieved", standings));
    }
}
