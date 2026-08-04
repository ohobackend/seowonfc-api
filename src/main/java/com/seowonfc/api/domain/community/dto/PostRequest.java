package com.seowonfc.api.domain.community.dto;

import jakarta.validation.constraints.NotBlank;

public record PostRequest(@NotBlank String title, @NotBlank String content) {}