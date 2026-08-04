package com.seowonfc.api.domain.match.dto;

import com.seowonfc.api.domain.match.MatchStatus;
import jakarta.validation.constraints.NotNull;

public record MatchResultRequest(
        @NotNull MatchStatus status, Integer homeScore, Integer awayScore
) {}