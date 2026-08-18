package com.seowonfc.api.domain.sponsor.dto;

import com.seowonfc.api.domain.sponsor.SponsorApplication;
import com.seowonfc.api.domain.sponsor.SponsorApplicationStatus;
import com.seowonfc.api.domain.sponsor.SponsorTier;

import java.time.LocalDateTime;

public record SponsorApplicationResponse(
        Long id, String companyName, String contactName, String contactEmail, String contactPhone,
        SponsorTier desiredTier, String logoUrl, String message, String linkUrl,
        SponsorApplicationStatus status, String rejectReason, LocalDateTime createdAt
) {
    public static SponsorApplicationResponse from(SponsorApplication application) {
        return new SponsorApplicationResponse(application.getId(), application.getCompanyName(),
                application.getContactName(), application.getContactEmail(), application.getContactPhone(),
                application.getDesiredTier(), application.getLogoUrl(), application.getMessage(),
                application.getLinkUrl(), application.getStatus(), application.getRejectReason(),
                application.getCreatedAt());
    }
}
