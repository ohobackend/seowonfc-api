package com.seowonfc.api.domain.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record EventRequest(
        @NotBlank String title, @NotBlank String content,
        @NotNull LocalDateTime startDate, @NotNull LocalDateTime endDate
) {}