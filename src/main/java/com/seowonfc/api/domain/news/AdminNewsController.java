package com.seowonfc.api.domain.news;

import com.seowonfc.api.common.ApiResponse;
import com.seowonfc.api.domain.news.dto.NewsRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "[관리자] News", description = "구단 뉴스 관리 API")
@RestController
@RequestMapping("/api/v1/admin/news")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // 클래스 레벨에 걸어두면 이 컨트롤러 전체가 ADMIN 전용이 된다
public class AdminNewsController {

    private final NewsService newsService;

    @Operation(summary = "뉴스 등록")
    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody NewsRequest request) {
        return ApiResponse.success(newsService.create(request));
    }

    @Operation(summary = "뉴스 수정")
    @PutMapping("/{newsId}")
    public ApiResponse<Void> update(@PathVariable Long newsId, @Valid @RequestBody NewsRequest request) {
        newsService.update(newsId, request);
        return ApiResponse.success(null);
    }

    @Operation(summary = "뉴스 삭제")
    @DeleteMapping("/{newsId}")
    public ApiResponse<Void> delete(@PathVariable Long newsId) {
        newsService.delete(newsId);
        return ApiResponse.success(null);
    }
}