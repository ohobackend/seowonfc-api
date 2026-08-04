package com.seowonfc.api.domain.news;

import com.seowonfc.api.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class News extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    private String content;

    @Enumerated(EnumType.STRING)
    private NewsCategory category;

    private String thumbnailUrl;

    @Column(nullable = false)
    private Long viewCount = 0L;

    @Builder
    public News(String title, String content, NewsCategory category, String thumbnailUrl) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.thumbnailUrl = thumbnailUrl;
        this.viewCount = 0L;
    }

    public void update(String title, String content, NewsCategory category, String thumbnailUrl) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.thumbnailUrl = thumbnailUrl;
    }

    public void increaseView() {
        this.viewCount++;
    }
}