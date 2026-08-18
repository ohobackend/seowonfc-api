package com.seowonfc.api.domain.news;

import com.seowonfc.api.common.ApiResponse;
import com.seowonfc.api.domain.news.dto.NewsRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

@Tag(name = "[관리자] News", description = "구단 뉴스 관리 API")
@RestController
@RequestMapping("/api/v1/admin/news")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // 클래스 레벨에 걸어두면 이 컨트롤러 전체가 ADMIN 전용이 된다
public class AdminNewsController {

    private final NewsService newsService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "뉴스 등록")
    @PostMapping(consumes = "multipart/form-data")
    public ApiResponse<Long> create(
            @RequestParam("data") String data,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        NewsRequest request = objectMapper.readValue(data, NewsRequest.class);
        return ApiResponse.success(newsService.create(request, file));
    }

    @Operation(summary = "뉴스 수정")
    @PutMapping(value = "/{newsId}", consumes = "multipart/form-data")
    public ApiResponse<Void> update(
            @PathVariable Long newsId,
            @RequestParam("data") String data,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        NewsRequest request = objectMapper.readValue(data, NewsRequest.class);
        newsService.update(newsId, request, file);
        return ApiResponse.success(null);
    }

    @Operation(summary = "뉴스 삭제")
    @DeleteMapping("/{newsId}")
    public ApiResponse<Void> delete(@PathVariable Long newsId) {
        newsService.delete(newsId);
        return ApiResponse.success(null);
    }
}
