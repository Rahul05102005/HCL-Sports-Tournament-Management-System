package com.sportx.backend.service;

import com.sportx.backend.dto.sport.SportResponseDto;
import com.sportx.backend.dto.sport.VenueResponseDto;
import com.sportx.backend.repository.SportRepository;
import com.sportx.backend.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SportVenueService {

    private final SportRepository sportRepository;
    private final VenueRepository venueRepository;

    @Transactional(readOnly = true)
    public List<SportResponseDto> getAllSports() {
        return sportRepository.findAll().stream()
                .map(s -> SportResponseDto.builder()
                        .id(s.getId())
                        .name(s.getName())
                        .category(s.getCategory())
                        .minSquadSize(s.getMinSquadSize())
                        .maxSquadSize(s.getMaxSquadSize())
                        .matchDurationMinutes(s.getMatchDurationMinutes())
                        .rulesConfig(s.getRulesConfig())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VenueResponseDto> getAllVenues() {
        return venueRepository.findAll().stream()
                .map(v -> VenueResponseDto.builder()
                        .id(v.getId())
                        .name(v.getName())
                        .location(v.getLocation())
                        .capacity(v.getCapacity())
                        .status(v.getStatus())
                        .facilities(v.getFacilities())
                        .build())
                .collect(Collectors.toList());
    }
}
