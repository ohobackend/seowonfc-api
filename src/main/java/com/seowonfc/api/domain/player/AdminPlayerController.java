package com.seowonfc.api.domain.player;

import com.seowonfc.api.common.ApiResponse;
import com.seowonfc.api.domain.player.dto.PlayerRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "[관리자] Player", description = "선수단 관리 API")
@RestController
@RequestMapping("/api/v1/admin/players")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminPlayerController {

    private final PlayerService playerService;

    @Operation(summary = "선수 등록")
    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody PlayerRequest request) {
        return ApiResponse.success(playerService.create(request));
    }

    @Operation(summary = "선수 정보 수정")
    @PutMapping("/{playerId}")
    public ApiResponse<Void> update(@PathVariable Long playerId, @Valid @RequestBody PlayerRequest request) {
        playerService.update(playerId, request);
        return ApiResponse.success(null);
    }

    @Operation(summary = "선수 삭제")
    @DeleteMapping("/{playerId}")
    public ApiResponse<Void> delete(@PathVariable Long playerId) {
        playerService.delete(playerId);
        return ApiResponse.success(null);
    }
}