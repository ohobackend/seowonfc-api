package com.seowonfc.api.domain.match;

import com.seowonfc.api.common.ApiResponse;
import com.seowonfc.api.domain.match.dto.MatchRequest;
import com.seowonfc.api.domain.match.dto.MatchResultRequest;
import com.seowonfc.api.domain.match.dto.StandingRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "[관리자] Match", description = "경기/순위 관리 API")
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminMatchController {

    private final MatchService matchService;

    @Operation(summary = "경기 일정 등록")
    @PostMapping("/api/v1/admin/matches")
    public ApiResponse<Long> create(@Valid @RequestBody MatchRequest request) {
        return ApiResponse.success(matchService.create(request));
    }

    @Operation(summary = "경기 결과/상태 수정")
    @PutMapping("/api/v1/admin/matches/{matchId}")
    public ApiResponse<Void> updateResult(@PathVariable Long matchId,
                                          @Valid @RequestBody MatchResultRequest request) {
        matchService.updateResult(matchId, request);
        return ApiResponse.success(null);
    }

    @Operation(summary = "경기 삭제")
    @DeleteMapping("/api/v1/admin/matches/{matchId}")
    public ApiResponse<Void> delete(@PathVariable Long matchId) {
        matchService.delete(matchId);
        return ApiResponse.success(null);
    }

    @Operation(summary = "순위표 등록/갱신")
    @PutMapping("/api/v1/admin/standings")
    public ApiResponse<Void> upsertStanding(@Valid @RequestBody StandingRequest request) {
        matchService.upsertStanding(request);
        return ApiResponse.success(null);
    }
}