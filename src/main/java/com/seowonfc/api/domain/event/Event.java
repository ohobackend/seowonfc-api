package com.seowonfc.api.domain.event;

import com.seowonfc.api.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    private String content;

    @Column(nullable = false)
    private LocalDate eventDate;

    private String imageUrl;

    @Builder
    public Event(String title, String content, LocalDate eventDate, String imageUrl) {
        this.title = title;
        this.content = content;
        this.eventDate = eventDate;
        this.imageUrl = imageUrl;
    }

    public void update(String title, String content, LocalDate eventDate, String imageUrl) {
        this.title = title;
        this.content = content;
        this.eventDate = eventDate;
        this.imageUrl = imageUrl;
    }
}
