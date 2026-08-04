package com.seowonfc.api.domain.sponsor.dto;

import com.seowonfc.api.domain.sponsor.SponsorTier;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SponsorRequest(
        @NotBlank String name, String logoUrl,
        @NotNull SponsorTier tier, String linkUrl
) {}