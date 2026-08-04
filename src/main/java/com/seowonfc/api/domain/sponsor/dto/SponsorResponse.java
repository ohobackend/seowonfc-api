package com.seowonfc.api.domain.sponsor.dto;

import com.seowonfc.api.domain.sponsor.Sponsor;
import com.seowonfc.api.domain.sponsor.SponsorTier;

public record SponsorResponse(Long id, String name, String logoUrl, SponsorTier tier, String linkUrl) {
    public static SponsorResponse from(Sponsor sponsor) {
        return new SponsorResponse(sponsor.getId(), sponsor.getName(), sponsor.getLogoUrl(),
                sponsor.getTier(), sponsor.getLinkUrl());
    }
}