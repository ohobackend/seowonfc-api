package com.seowonfc.api.domain.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record NotificationPushRequest(
        List<Long> userIds,   // null 또는 빈 리스트면 "전체 발송"으로 처리
        @NotBlank String title,
        @NotNull String content
) {}