package com.seowonfc.api.domain.community.dto;

import com.seowonfc.api.domain.community.Comment;
import java.time.LocalDateTime;

public record CommentResponse(Long id, String authorName, String content, LocalDateTime createdAt) {
    public static CommentResponse from(Comment comment) {
        return new CommentResponse(comment.getId(), comment.getAuthor().getName(),
                comment.getContent(), comment.getCreatedAt());
    }
}