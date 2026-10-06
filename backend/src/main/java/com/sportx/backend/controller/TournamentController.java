package com.sportx.backend.controller;

import com.sportx.backend.dto.common.ApiResponse;
import com.sportx.backend.dto.match.MatchResponseDto;
import com.sportx.backend.dto.tournament.TournamentCreateRequest;
import com.sportx.backend.dto.tournament.TournamentPulseDto;
import com.sportx.backend.dto.tournament.TournamentResponseDto;
import com.sportx.backend.service.TournamentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tournaments")
@RequiredArgsConstructor
@Tag(name = "Tournaments", description = "Endpoints for creating, managing, and monitoring sports tournaments")
@CrossOrigin(origins = "*")
public class TournamentController {

    private final TournamentService tournamentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @Operation(summary = "Create Tournament", description = "Create a new tournament (Requires ADMIN or ORGANIZER role)")
    public ResponseEntity<ApiResponse<TournamentResponseDto>> createTournament(
            @Valid @RequestBody TournamentCreateRequest request,
            Authentication authentication
    ) {
        TournamentResponseDto response = tournamentService.createTournament(request, authentication);
        return new ResponseEntity<>(ApiResponse.ok("Tournament created successfully", response), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List Tournaments", description = "Retrieve list of all tournaments with optional status filter or search keyword")
    public ResponseEntity<ApiResponse<List<TournamentResponseDto>>> getAllTournaments(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search
    ) {
        List<TournamentResponseDto> response = tournamentService.getAllTournaments(status, search);
        return ResponseEntity.ok(ApiResponse.ok("Tournaments retrieved", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Tournament Details", description = "Retrieve detailed information for a specific tournament")
    public ResponseEntity<ApiResponse<TournamentResponseDto>> getTournamentById(@PathVariable Long id) {
        TournamentResponseDto response = tournamentService.getTournamentById(id);
        return ResponseEntity.ok(ApiResponse.ok("Tournament retrieved", response));
    }

    @PostMapping("/{id}/fixtures/generate")
    @PreAuthorize("@securityEvaluator.isTournamentOwner(authentication, #id)")
    @Operation(summary = "Generate Fixtures", description = "Automatically generates Round-Robin fixtures for all approved teams (Requires Tournament Owner/Admin)")
    public ResponseEntity<ApiResponse<List<MatchResponseDto>>> generateFixtures(@PathVariable Long id) {
        List<MatchResponseDto> fixtures = tournamentService.generateFixtures(id);
        return ResponseEntity.ok(ApiResponse.ok("Fixtures generated successfully", fixtures));
    }

    @GetMapping("/{id}/pulse")
    @Operation(summary = "Tournament Pulse", description = "Aggregates real-time pulse data: live matches, completion rate, leaderboards, and recent alerts")
    public ResponseEntity<ApiResponse<TournamentPulseDto>> getTournamentPulse(@PathVariable Long id) {
        TournamentPulseDto pulse = tournamentService.getTournamentPulse(id);
        return ResponseEntity.ok(ApiResponse.ok("Tournament Pulse data retrieved", pulse));
    }
}
