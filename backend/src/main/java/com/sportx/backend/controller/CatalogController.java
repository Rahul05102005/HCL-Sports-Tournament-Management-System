package com.sportx.backend.controller;

import com.sportx.backend.dto.common.ApiResponse;
import com.sportx.backend.dto.sport.SportResponseDto;
import com.sportx.backend.dto.sport.VenueResponseDto;
import com.sportx.backend.service.SportVenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Catalog", description = "Endpoints for sports and venue master catalog")
@CrossOrigin(origins = "*")
public class CatalogController {

    private final SportVenueService sportVenueService;

    @GetMapping("/sports")
    @Operation(summary = "List Sports", description = "Retrieve list of all supported sports with squad and rule metadata")
    public ResponseEntity<ApiResponse<List<SportResponseDto>>> getAllSports() {
        List<SportResponseDto> sports = sportVenueService.getAllSports();
        return ResponseEntity.ok(ApiResponse.ok("Sports retrieved", sports));
    }

    @GetMapping("/venues")
    @Operation(summary = "List Venues", description = "Retrieve list of all sports facilities and venues")
    public ResponseEntity<ApiResponse<List<VenueResponseDto>>> getAllVenues() {
        List<VenueResponseDto> venues = sportVenueService.getAllVenues();
        return ResponseEntity.ok(ApiResponse.ok("Venues retrieved", venues));
    }
}
