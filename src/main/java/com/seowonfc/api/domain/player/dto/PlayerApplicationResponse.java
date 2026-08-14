package com.seowonfc.api.domain.player.dto;

import com.seowonfc.api.domain.player.ApplicationStatus;
import com.seowonfc.api.domain.player.PlayerApplication;
import com.seowonfc.api.domain.player.Position;
import java.time.LocalDateTime;

public record PlayerApplicationResponse(
        Long id, String applicantName, String name, Integer backNumber, Position position,
        String nationality, String profileImageUrl, ApplicationStatus status,
        String rejectReason, LocalDateTime createdAt
) {
    public static PlayerApplicationResponse from(PlayerApplication app) {
        return new PlayerApplicationResponse(app.getId(), app.getApplicant().getName(),
                app.getName(), app.getBackNumber(), app.getPosition(), app.getNationality(),
                app.getProfileImageUrl(), app.getStatus(), app.getRejectReason(), app.getCreatedAt());
    }
}