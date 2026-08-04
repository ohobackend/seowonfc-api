package com.seowonfc.api.domain.player;

import com.seowonfc.api.common.ApiResponse;
import com.seowonfc.api.domain.player.dto.PlayerResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springdoc.core.annotations.ParameterObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@Tag(name = "[회원] Player", description = "선수단 조회 API")
@RestController
@RequestMapping("/api/v1/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @Operation(summary = "선수단 명단 조회")
    @GetMapping
    public ApiResponse<Page<PlayerResponse>> getList(
            @RequestParam(required = false) Position position,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(playerService.getList(position, pageable));
    }

    @Operation(summary = "선수 상세 조회")
    @GetMapping("/{playerId}")
    public ApiResponse<PlayerResponse> getDetail(@PathVariable Long playerId) {
        return ApiResponse.success(playerService.getDetail(playerId));
    }
}