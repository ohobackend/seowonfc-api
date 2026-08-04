package com.seowonfc.api.domain.news;

import com.seowonfc.api.common.ApiResponse;
import com.seowonfc.api.domain.news.dto.NewsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@Tag(name = "[회원] News", description = "구단 뉴스 조회 API")
@RestController
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @Operation(summary = "뉴스 목록 조회")
    @GetMapping
    public ApiResponse<Page<NewsResponse>> getList(
            @RequestParam(required = false) NewsCategory category,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(newsService.getList(category, pageable));
    }

    @Operation(summary = "뉴스 상세 조회")
    @GetMapping("/{newsId}")
    public ApiResponse<NewsResponse> getDetail(@PathVariable Long newsId) {
        return ApiResponse.success(newsService.getDetail(newsId));
    }
}