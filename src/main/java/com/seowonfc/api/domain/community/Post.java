package com.seowonfc.api.domain.community;

import com.seowonfc.api.common.BaseTimeEntity;
import com.seowonfc.api.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardType boardType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    private String content;

    private Long likeCount = 0L;
    private Long reportCount = 0L;

    @Builder
    public Post(BoardType boardType, User author, String title, String content) {
        this.boardType = boardType;
        this.author = author;
        this.title = title;
        this.content = content;
        this.likeCount = 0L;
        this.reportCount = 0L;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void increaseLike() {
        this.likeCount++;
    }

    public void increaseReport() {
        this.reportCount++;
    }

    public boolean isAuthor(Long userId) {
        return this.author.getId().equals(userId);
    }
}