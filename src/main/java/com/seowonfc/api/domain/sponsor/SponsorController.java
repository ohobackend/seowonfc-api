package com.seowonfc.api.domain.sponsor;

import com.seowonfc.api.common.ApiResponse;
import com.seowonfc.api.domain.sponsor.dto.SponsorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "[회원] Sponsor", description = "스폰서 조회 API")
@RestController
@RequestMapping("/api/v1/sponsors")
@RequiredArgsConstructor
public class SponsorController {

    private final SponsorService sponsorService;

    @Operation(summary = "스폰서 목록 조회")
    @GetMapping
    public ApiResponse<List<SponsorResponse>> getList(
            @RequestParam(required = false) SponsorTier tier) {
        return ApiResponse.success(sponsorService.getList(tier));
    }
}