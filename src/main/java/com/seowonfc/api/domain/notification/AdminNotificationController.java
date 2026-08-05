package com.seowonfc.api.domain.notification;

import com.seowonfc.api.common.ApiResponse;
import com.seowonfc.api.domain.notification.dto.NotificationPushRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "[관리자] Notification", description = "알림 발송 API")
@RestController
@RequestMapping("/api/v1/admin/notifications")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminNotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "알림 발송 (userIds 비우면 전체 발송)")
    @PostMapping("/push")
    public ApiResponse<Void> push(@Valid @RequestBody NotificationPushRequest request) {
        notificationService.push(request);
        return ApiResponse.success(null);
    }
}