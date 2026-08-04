package com.seowonfc.api.domain.match.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StandingRequest(
        @NotNull Integer season, @NotBlank String team, @NotNull Integer rank,
        Integer played, Integer win, Integer draw, Integer lose, Integer points, Integer goalDiff
) {}