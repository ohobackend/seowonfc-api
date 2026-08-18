package com.seowonfc.api.domain.sponsor.dto;

import com.seowonfc.api.domain.sponsor.SponsorTier;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SponsorApplicationRequest(
        @NotBlank String companyName,
        @NotBlank String contactName,
        @Email @NotBlank String contactEmail,
        String contactPhone,
        @NotNull SponsorTier desiredTier,
        String message,
        String linkUrl
) {}
