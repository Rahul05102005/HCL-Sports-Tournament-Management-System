package com.sportx.backend.dto.match;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoreEventRequest {

    @NotNull(message = "Team ID is required")
    private Long teamId;

    private Long playerId;

    @NotBlank(message = "Event type is required (e.g. GOAL, POINT, YELLOW_CARD, RED_CARD)")
    private String eventType;

    private Integer minuteOccurred;

    private String extraInfo;
}
