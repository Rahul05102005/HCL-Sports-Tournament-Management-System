package com.sportx.backend.dto.match;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoreEventResponseDto {
    private Long id;
    private Long matchId;
    private Long teamId;
    private String teamName;
    private Long playerId;
    private String playerName;
    private Integer jerseyNumber;
    private String eventType;
    private Integer minuteOccurred;
    private String extraInfo;
    private LocalDateTime createdAt;
}
