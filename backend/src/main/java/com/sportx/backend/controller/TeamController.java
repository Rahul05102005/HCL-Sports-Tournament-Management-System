package com.sportx.backend.controller;

import com.sportx.backend.dto.common.ApiResponse;
import com.sportx.backend.dto.team.*;
import com.sportx.backend.service.TeamService;
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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Teams & Players", description = "Endpoints for team registration, squad validation, and roster management")
@CrossOrigin(origins = "*")
public class TeamController {

    private final TeamService teamService;

    @PostMapping("/teams")
    @Operation(summary = "Register Team", description = "Register a college sports team for a tournament")
    public ResponseEntity<ApiResponse<TeamResponseDto>> createTeam(
            @Valid @RequestBody TeamCreateRequest request,
            Authentication authentication
    ) {
        TeamResponseDto response = teamService.createTeam(request, authentication);
        return new ResponseEntity<>(ApiResponse.ok("Team registered successfully", response), HttpStatus.CREATED);
    }

    @GetMapping("/tournaments/{tournamentId}/teams")
    @Operation(summary = "List Tournament Teams", description = "Retrieve all registered teams for a given tournament")
    public ResponseEntity<ApiResponse<List<TeamResponseDto>>> getTeamsByTournament(@PathVariable Long tournamentId) {
        List<TeamResponseDto> teams = teamService.getTeamsByTournament(tournamentId);
        return ResponseEntity.ok(ApiResponse.ok("Teams retrieved", teams));
    }

    @GetMapping("/teams/{id}")
    @Operation(summary = "Get Team Details", description = "Retrieve details and squad status of a specific team")
    public ResponseEntity<ApiResponse<TeamResponseDto>> getTeamById(@PathVariable Long id) {
        TeamResponseDto team = teamService.getTeamById(id);
        return ResponseEntity.ok(ApiResponse.ok("Team retrieved", team));
    }

    @PostMapping("/teams/{id}/players")
    @PreAuthorize("@securityEvaluator.isTeamManager(authentication, #id)")
    @Operation(summary = "Add Player to Team", description = "Add a new player to the team roster with squad size checks")
    public ResponseEntity<ApiResponse<PlayerResponseDto>> addPlayer(
            @PathVariable Long id,
            @Valid @RequestBody PlayerCreateRequest request
    ) {
        PlayerResponseDto response = teamService.addPlayerToTeam(id, request);
        return new ResponseEntity<>(ApiResponse.ok("Player added to roster successfully", response), HttpStatus.CREATED);
    }

    @GetMapping("/teams/{id}/players")
    @Operation(summary = "Get Team Players", description = "Retrieve all players in the team squad")
    public ResponseEntity<ApiResponse<List<PlayerResponseDto>>> getPlayersByTeam(@PathVariable Long id) {
        List<PlayerResponseDto> players = teamService.getPlayersByTeam(id);
        return ResponseEntity.ok(ApiResponse.ok("Players retrieved", players));
    }
}
