package com.seowonfc.api.domain.notification;

import com.seowonfc.api.common.CustomException;
import com.seowonfc.api.common.ErrorCode;
import com.seowonfc.api.domain.notification.dto.NotificationPushRequest;
import com.seowonfc.api.domain.notification.dto.NotificationResponse;
import com.seowonfc.api.domain.user.User;
import com.seowonfc.api.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public Page<NotificationResponse> getMyNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByReceiverId(userId, pageable).map(NotificationResponse::from);
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        if (!notification.getReceiver().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN); // 남의 알림은 못 읽음 처리
        }
        notification.markAsRead();
    }

    // ---- 관리자용: 발송 ----
    @Transactional
    public void push(NotificationPushRequest request) {
        List<User> targets = (request.userIds() == null || request.userIds().isEmpty())
                ? userRepository.findAll()              // 대상 없으면 전체 발송
                : userRepository.findAllById(request.userIds());

        List<Notification> notifications = targets.stream()
                .map(user -> Notification.builder()
                        .receiver(user)
                        .title(request.title())
                        .content(request.content())
                        .build())
                .toList();

        notificationRepository.saveAll(notifications);
    }
}