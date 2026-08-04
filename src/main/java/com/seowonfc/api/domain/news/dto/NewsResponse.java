package com.seowonfc.api.domain.news.dto;

import com.seowonfc.api.domain.news.News;
import com.seowonfc.api.domain.news.NewsCategory;
import java.time.LocalDateTime;

public record NewsResponse(
        Long id, String title, String content, NewsCategory category,
        String thumbnailUrl, Long viewCount, LocalDateTime publishedAt
) {
    public static NewsResponse from(News news) {
        return new NewsResponse(news.getId(), news.getTitle(), news.getContent(),
                news.getCategory(), news.getThumbnailUrl(), news.getViewCount(),
                news.getCreatedAt());
    }
}