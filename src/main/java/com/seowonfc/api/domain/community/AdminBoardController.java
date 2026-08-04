package com.seowonfc.api.domain.community;

import com.seowonfc.api.common.ApiResponse;
import com.seowonfc.api.domain.community.dto.PostResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "[관리자] Community", description = "커뮤니티 모더레이션 API")
@RestController
@RequestMapping("/api/v1/admin/boards")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminBoardController {

    private final PostService postService;

    @Operation(summary = "신고된 게시글 목록")
    @GetMapping("/reports")
    public ApiResponse<Page<PostResponse>> getReports(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(postService.getReportedPosts(pageable));
    }

    @Operation(summary = "게시글 강제 삭제")
    @DeleteMapping("/posts/{postId}")
    public ApiResponse<Void> forceDelete(@PathVariable Long postId) {
        postService.forceDelete(postId);
        return ApiResponse.success(null);
    }

    @Operation(summary = "댓글 강제 삭제")
    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<Void> forceDeleteComment(@PathVariable Long commentId) {
        postService.forceDeleteComment(commentId);
        return ApiResponse.success(null);
    }
}