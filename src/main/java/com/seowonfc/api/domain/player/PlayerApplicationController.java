package com.seowonfc.api.domain.player;

import com.seowonfc.api.common.ApiResponse;
import com.seowonfc.api.domain.player.dto.PlayerApplicationRequest;
import com.seowonfc.api.domain.player.dto.PlayerApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "[회원] Player Application", description = "선수 등록 신청 API")
@RestController
@RequestMapping("/api/v1/player-applications")
@RequiredArgsConstructor
public class PlayerApplicationController {

    private final PlayerApplicationService applicationService;

    @Operation(summary = "선수 등록 신청")
    @PostMapping
    public ApiResponse<Long> apply(@Valid @RequestBody PlayerApplicationRequest request,
                                   Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(applicationService.apply(userId, request));
    }

    @Operation(summary = "내 신청 목록 조회")
    @GetMapping("/me")
    public ApiResponse<Page<PlayerApplicationResponse>> getMyApplications(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(applicationService.getMyApplications(userId, pageable));
    }
}