package com.seowonfc.api.domain.news.dto;

import com.seowonfc.api.domain.news.NewsCategory;
import jakarta.validation.constraints.NotBlank;

public record NewsRequest(
        @NotBlank String title,
        @NotBlank String content,
        NewsCategory category,
        String thumbnailUrl
) {}