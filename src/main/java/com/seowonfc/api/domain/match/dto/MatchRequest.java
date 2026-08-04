package com.seowonfc.api.domain.match.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record MatchRequest(
        @NotNull Integer season, Integer round, String competition,
        @NotBlank String homeTeam, @NotBlank String awayTeam,
        @NotNull LocalDateTime matchDate, String stadium
) {}