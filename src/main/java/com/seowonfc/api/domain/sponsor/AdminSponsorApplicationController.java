package com.seowonfc.api.domain.sponsor;

import com.seowonfc.api.common.ApiResponse;
import com.seowonfc.api.domain.sponsor.dto.RejectRequest;
import com.seowonfc.api.domain.sponsor.dto.SponsorApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "[관리자] Sponsor Application", description = "스폰서 신청 승인/반려 API")
@RestController
@RequestMapping("/api/v1/admin/sponsor-applications")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminSponsorApplicationController {

    private final SponsorApplicationService applicationService;

    @Operation(summary = "대기 중인 스폰서 신청 목록")
    @GetMapping
    public ApiResponse<Page<SponsorApplicationResponse>> getPending(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(applicationService.getPending(pageable));
    }

    @Operation(summary = "스폰서 신청 승인 (Sponsor로 등록)")
    @PostMapping("/{applicationId}/approve")
    public ApiResponse<Long> approve(@PathVariable Long applicationId) {
        return ApiResponse.success(applicationService.approve(applicationId));
    }

    @Operation(summary = "스폰서 신청 반려")
    @PostMapping("/{applicationId}/reject")
    public ApiResponse<Void> reject(@PathVariable Long applicationId, @RequestBody RejectRequest request) {
        applicationService.reject(applicationId, request.reason());
        return ApiResponse.success(null);
    }
}
