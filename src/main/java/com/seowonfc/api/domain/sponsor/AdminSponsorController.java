package com.seowonfc.api.domain.sponsor;

import com.seowonfc.api.common.ApiResponse;
import com.seowonfc.api.domain.sponsor.dto.SponsorRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "[관리자] Sponsor", description = "스폰서 관리 API")
@RestController
@RequestMapping("/api/v1/admin/sponsors")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminSponsorController {

    private final SponsorService sponsorService;

    @Operation(summary = "스폰서 등록")
    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody SponsorRequest request) {
        return ApiResponse.success(sponsorService.create(request));
    }

    @Operation(summary = "스폰서 정보 수정")
    @PutMapping("/{sponsorId}")
    public ApiResponse<Void> update(@PathVariable Long sponsorId, @Valid @RequestBody SponsorRequest request) {
        sponsorService.update(sponsorId, request);
        return ApiResponse.success(null);
    }

    @Operation(summary = "스폰서 삭제")
    @DeleteMapping("/{sponsorId}")
    public ApiResponse<Void> delete(@PathVariable Long sponsorId) {
        sponsorService.delete(sponsorId);
        return ApiResponse.success(null);
    }
}