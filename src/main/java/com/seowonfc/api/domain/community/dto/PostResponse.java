package com.seowonfc.api.domain.community.dto;

import com.seowonfc.api.domain.community.BoardType;
import com.seowonfc.api.domain.community.Post;
import java.time.LocalDateTime;

public record PostResponse(
        Long id, BoardType boardType, String authorName, String title,
        String content, Long likeCount, LocalDateTime createdAt
) {
    public static PostResponse from(Post post) {
        return new PostResponse(post.getId(), post.getBoardType(), post.getAuthor().getName(),
                post.getTitle(), post.getContent(), post.getLikeCount(), post.getCreatedAt());
    }
}