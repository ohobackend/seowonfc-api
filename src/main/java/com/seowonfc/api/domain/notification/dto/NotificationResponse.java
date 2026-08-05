package com.seowonfc.api.domain.notification.dto;

import com.seowonfc.api.domain.notification.Notification;
import java.time.LocalDateTime;

public record NotificationResponse(
        Long id, String title, String content, Boolean isRead, LocalDateTime createdAt
) {
    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(n.getId(), n.getTitle(), n.getContent(),
                n.getIsRead(), n.getCreatedAt());
    }
}