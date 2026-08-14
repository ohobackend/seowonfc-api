package com.seowonfc.api.domain.player;

import com.seowonfc.api.common.BaseTimeEntity;
import com.seowonfc.api.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlayerApplication extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;

    @Column(nullable = false)
    private String name;

    private Integer backNumber;

    @Enumerated(EnumType.STRING)
    private Position position;

    private String nationality;
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    private String rejectReason;

    @Builder
    public PlayerApplication(User applicant, String name, Integer backNumber, Position position,
                             String nationality, String profileImageUrl) {
        this.applicant = applicant;
        this.name = name;
        this.backNumber = backNumber;
        this.position = position;
        this.nationality = nationality;
        this.profileImageUrl = profileImageUrl;
        this.status = ApplicationStatus.PENDING;
    }

    public void approve() {
        this.status = ApplicationStatus.APPROVED;
    }

    public void reject(String reason) {
        this.status = ApplicationStatus.REJECTED;
        this.rejectReason = reason;
    }
}