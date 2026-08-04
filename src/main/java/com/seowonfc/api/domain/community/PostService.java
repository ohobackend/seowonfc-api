package com.seowonfc.api.domain.community;

import com.seowonfc.api.common.CustomException;
import com.seowonfc.api.common.ErrorCode;
import com.seowonfc.api.domain.community.dto.*;
import com.seowonfc.api.domain.user.User;
import com.seowonfc.api.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public Page<PostResponse> getList(BoardType boardType, Pageable pageable) {
        return postRepository.findByBoardType(boardType, pageable).map(PostResponse::from);
    }

    public PostResponse getDetail(Long postId) {
        return PostResponse.from(findById(postId));
    }

    @Transactional
    public Long create(BoardType boardType, Long userId, PostRequest request) {
        User author = findUser(userId);
        Post post = Post.builder()
                .boardType(boardType)
                .author(author)
                .title(request.title())
                .content(request.content())
                .build();
        return postRepository.save(post).getId();
    }

    @Transactional
    public void update(Long postId, Long userId, PostRequest request) {
        Post post = findById(postId);
        validateAuthor(post, userId);
        post.update(request.title(), request.content());
    }

    @Transactional
    public void delete(Long postId, Long userId) {
        Post post = findById(postId);
        validateAuthor(post, userId);
        postRepository.delete(post);
    }

    @Transactional
    public void like(Long postId) {
        findById(postId).increaseLike();
    }

    @Transactional
    public void report(Long postId) {
        findById(postId).increaseReport();
    }

    @Transactional
    public Long addComment(Long postId, Long userId, CommentRequest request) {
        Post post = findById(postId);
        User author = findUser(userId);
        Comment comment = Comment.builder().post(post).author(author).content(request.content()).build();
        return commentRepository.save(comment).getId();
    }

    public List<CommentResponse> getComments(Long postId) {
        return commentRepository.findByPostId(postId).stream()
                .map(CommentResponse::from)
                .toList();
    }

    // ---- 관리자용 ----
    public Page<PostResponse> getReportedPosts(Pageable pageable) {
        return postRepository.findByReportCountGreaterThan(0L, pageable).map(PostResponse::from);
    }

    @Transactional
    public void forceDelete(Long postId) {
        postRepository.delete(findById(postId));
    }

    @Transactional
    public void forceDeleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        commentRepository.delete(comment);
    }

    // ---- 공통 ----
    private Post findById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    }

    private void validateAuthor(Post post, Long userId) {
        if (!post.isAuthor(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }
}