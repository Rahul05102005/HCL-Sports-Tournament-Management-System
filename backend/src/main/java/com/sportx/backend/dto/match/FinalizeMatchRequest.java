package com.sportx.backend.dto.match;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinalizeMatchRequest {

    @NotNull(message = "Home score is required")
    private Integer homeScore;

    @NotNull(message = "Away score is required")
    private Integer awayScore;

    private String notes;
}
