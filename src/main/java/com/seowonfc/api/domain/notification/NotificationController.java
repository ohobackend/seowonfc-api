package com.seowonfc.api.domain.notification;

import com.seowonfc.api.common.ApiResponse;
import com.seowonfc.api.domain.notification.dto.NotificationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "[회원] Notification", description = "내 알림 API")
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "내 알림 목록 조회")
    @GetMapping("/me")
    public ApiResponse<Page<NotificationResponse>> getMyNotifications(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(notificationService.getMyNotifications(userId, pageable));
    }

    @Operation(summary = "알림 읽음 처리")
    @PutMapping("/{notificationId}/read")
    public ApiResponse<Void> markAsRead(@PathVariable Long notificationId, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        notificationService.markAsRead(notificationId, userId);
        return ApiResponse.success(null);
    }
}