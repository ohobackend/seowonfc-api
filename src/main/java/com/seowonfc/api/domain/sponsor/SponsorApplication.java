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
public class SponsorApplication extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String contactName;

    @Column(nullable = false)
    private String contactEmail;

    private String contactPhone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SponsorTier desiredTier;

    private String logoUrl;

    @Lob
    private String message;

    private String linkUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SponsorApplicationStatus status;

    private String rejectReason;

    @Builder
    public SponsorApplication(String companyName, String contactName, String contactEmail,
                              String contactPhone, SponsorTier desiredTier, String logoUrl,
                              String message, String linkUrl) {
        this.companyName = companyName;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.desiredTier = desiredTier;
        this.logoUrl = logoUrl;
        this.message = message;
        this.linkUrl = linkUrl;
        this.status = SponsorApplicationStatus.PENDING;
    }

    public void approve() {
        this.status = SponsorApplicationStatus.APPROVED;
    }

    public void reject(String reason) {
        this.status = SponsorApplicationStatus.REJECTED;
        this.rejectReason = reason;
    }
}
