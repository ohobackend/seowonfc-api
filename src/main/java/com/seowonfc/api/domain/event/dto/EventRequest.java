package com.seowonfc.api.domain.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record EventRequest(
        @NotBlank String title,
        @NotBlank String content,
        @NotNull LocalDate eventDate,
        String imageUrl
) {}
