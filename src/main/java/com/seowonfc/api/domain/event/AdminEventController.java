package com.seowonfc.api.domain.event;

import com.seowonfc.api.common.ApiResponse;
import com.seowonfc.api.domain.event.dto.EventRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "[관리자] Event", description = "이벤트 관리 API")
@RestController
@RequestMapping("/api/v1/admin/events")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminEventController {

    private final EventService eventService;

    @Operation(summary = "이벤트 등록")
    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody EventRequest request) {
        return ApiResponse.success(eventService.create(request));
    }

    @Operation(summary = "이벤트 수정")
    @PutMapping("/{eventId}")
    public ApiResponse<Void> update(@PathVariable Long eventId, @Valid @RequestBody EventRequest request) {
        eventService.update(eventId, request);
        return ApiResponse.success(null);
    }

    @Operation(summary = "이벤트 삭제")
    @DeleteMapping("/{eventId}")
    public ApiResponse<Void> delete(@PathVariable Long eventId) {
        eventService.delete(eventId);
        return ApiResponse.success(null);
    }

    @Operation(summary = "당첨자 선정")
    @PostMapping("/{eventId}/winners")
    public ApiResponse<Void> selectWinners(@PathVariable Long eventId, @RequestBody List<Long> userIds) {
        eventService.selectWinners(eventId, userIds);
        return ApiResponse.success(null);
    }
}