package com.seowonfc.api.domain.event;

import com.seowonfc.api.common.ApiResponse;
import com.seowonfc.api.domain.event.dto.EventRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Tag(name = "[관리자] Event", description = "이벤트 관리 API")
@RestController
@RequestMapping("/api/v1/admin/events")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminEventController {

    private final EventService eventService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "이벤트 등록")
    @PostMapping(consumes = "multipart/form-data")
    public ApiResponse<Long> create(
            @RequestParam("data") String data,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        EventRequest request = objectMapper.readValue(data, EventRequest.class);
        return ApiResponse.success(eventService.create(request, file));
    }

    @Operation(summary = "이벤트 수정")
    @PutMapping(value = "/{eventId}", consumes = "multipart/form-data")
    public ApiResponse<Void> update(
            @PathVariable Long eventId,
            @RequestParam("data") String data,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        EventRequest request = objectMapper.readValue(data, EventRequest.class);
        eventService.update(eventId, request, file);
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
