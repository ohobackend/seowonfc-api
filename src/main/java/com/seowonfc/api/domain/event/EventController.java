package com.seowonfc.api.domain.event;

import com.seowonfc.api.common.ApiResponse;
import com.seowonfc.api.domain.event.dto.EventResponse;
import com.seowonfc.api.domain.event.dto.WinnerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "[회원] Event", description = "이벤트 조회/응모 API")
@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @Operation(summary = "이벤트 목록")
    @GetMapping
    public ApiResponse<Page<EventResponse>> getList(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(eventService.getList(pageable));
    }

    @Operation(summary = "이벤트 상세")
    @GetMapping("/{eventId}")
    public ApiResponse<EventResponse> getDetail(@PathVariable Long eventId) {
        return ApiResponse.success(eventService.getDetail(eventId));
    }

    @Operation(summary = "이벤트 응모")
    @PostMapping("/{eventId}/apply")
    public ApiResponse<Void> apply(@PathVariable Long eventId, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        eventService.apply(eventId, userId);
        return ApiResponse.success(null);
    }

    @Operation(summary = "당첨자 조회")
    @GetMapping("/{eventId}/winners")
    public ApiResponse<List<WinnerResponse>> getWinners(@PathVariable Long eventId) {
        return ApiResponse.success(eventService.getWinners(eventId));
    }
}