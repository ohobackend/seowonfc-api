package com.seowonfc.api.domain.community;

import com.seowonfc.api.common.ApiResponse;
import com.seowonfc.api.domain.community.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "[회원] Community", description = "팬 커뮤니티 API")
@RestController
@RequestMapping("/api/v1/boards/{boardType}/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @Operation(summary = "게시글 목록")
    @GetMapping
    public ApiResponse<Page<PostResponse>> getList(
            @PathVariable BoardType boardType,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(postService.getList(boardType, pageable));
    }

    @Operation(summary = "게시글 상세")
    @GetMapping("/{postId}")
    public ApiResponse<PostResponse> getDetail(@PathVariable Long postId) {
        return ApiResponse.success(postService.getDetail(postId));
    }

    @Operation(summary = "게시글 작성")
    @PostMapping
    public ApiResponse<Long> create(@PathVariable BoardType boardType,
                                    @Valid @RequestBody PostRequest request,
                                    Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(postService.create(boardType, userId, request));
    }

    @Operation(summary = "게시글 수정 (본인 글만)")
    @PutMapping("/{postId}")
    public ApiResponse<Void> update(@PathVariable Long postId,
                                    @Valid @RequestBody PostRequest request,
                                    Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        postService.update(postId, userId, request);
        return ApiResponse.success(null);
    }

    @Operation(summary = "게시글 삭제 (본인 글만)")
    @DeleteMapping("/{postId}")
    public ApiResponse<Void> delete(@PathVariable Long postId, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        postService.delete(postId, userId);
        return ApiResponse.success(null);
    }

    @Operation(summary = "좋아요")
    @PostMapping("/{postId}/like")
    public ApiResponse<Void> like(@PathVariable Long postId) {
        postService.like(postId);
        return ApiResponse.success(null);
    }

    @Operation(summary = "신고")
    @PostMapping("/{postId}/report")
    public ApiResponse<Void> report(@PathVariable Long postId) {
        postService.report(postId);
        return ApiResponse.success(null);
    }

    @Operation(summary = "댓글 목록")
    @GetMapping("/{postId}/comments")
    public ApiResponse<List<CommentResponse>> getComments(@PathVariable Long postId) {
        return ApiResponse.success(postService.getComments(postId));
    }

    @Operation(summary = "댓글 작성")
    @PostMapping("/{postId}/comments")
    public ApiResponse<Long> addComment(@PathVariable Long postId,
                                        @Valid @RequestBody CommentRequest request,
                                        Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(postService.addComment(postId, userId, request));
    }
}