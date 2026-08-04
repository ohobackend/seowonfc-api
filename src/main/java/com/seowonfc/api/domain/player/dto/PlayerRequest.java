package com.seowonfc.api.domain.player.dto;

import com.seowonfc.api.domain.player.Position;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PlayerRequest(
        @NotBlank String name,
        @NotNull Integer backNumber,
        @NotNull Position position,
        String nationality,
        String profileImageUrl
) {}