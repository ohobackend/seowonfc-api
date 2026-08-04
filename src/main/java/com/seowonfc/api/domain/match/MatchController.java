package com.seowonfc.api.domain.match;

import com.seowonfc.api.common.ApiResponse;
import com.seowonfc.api.domain.match.dto.MatchResponse;
import com.seowonfc.api.domain.match.dto.StandingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "[회원] Match", description = "경기 일정/순위 조회 API")
@RestController
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @Operation(summary = "경기 목록 조회")
    @GetMapping("/api/v1/matches")
    public ApiResponse<Page<MatchResponse>> getList(
            @RequestParam(required = false) Integer season,
            @RequestParam(required = false) MatchStatus status,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(matchService.getList(season, status, pageable));
    }

    @Operation(summary = "경기 상세 조회")
    @GetMapping("/api/v1/matches/{matchId}")
    public ApiResponse<MatchResponse> getDetail(@PathVariable Long matchId) {
        return ApiResponse.success(matchService.getDetail(matchId));
    }

    @Operation(summary = "리그 순위표 조회")
    @GetMapping("/api/v1/standings")
    public ApiResponse<List<StandingResponse>> getStandings(@RequestParam Integer season) {
        return ApiResponse.success(matchService.getStandings(season));
    }
}