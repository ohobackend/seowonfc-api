package com.seowonfc.api.domain.sponsor;

import com.seowonfc.api.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sponsor extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String logoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SponsorTier tier;

    private String linkUrl;

    @Builder
    public Sponsor(String name, String logoUrl, SponsorTier tier, String linkUrl) {
        this.name = name;
        this.logoUrl = logoUrl;
        this.tier = tier;
        this.linkUrl = linkUrl;
    }

    public void update(String name, String logoUrl, SponsorTier tier, String linkUrl) {
        this.name = name;
        this.logoUrl = logoUrl;
        this.tier = tier;
        this.linkUrl = linkUrl;
    }
}