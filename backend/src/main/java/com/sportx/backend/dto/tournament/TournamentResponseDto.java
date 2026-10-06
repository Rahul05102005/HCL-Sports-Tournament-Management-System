package com.sportx.backend.dto.tournament;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TournamentResponseDto {
    private Long id;
    private String name;
    private Long sportId;
    private String sportName;
    private Long organizerId;
    private String organizerName;
    private String format;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Integer minTeams;
    private Integer maxTeams;
    private Integer currentTeamsCount;
    private String description;
    private LocalDateTime createdAt;
}
