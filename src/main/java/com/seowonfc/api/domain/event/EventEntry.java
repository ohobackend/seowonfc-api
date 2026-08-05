package com.seowonfc.api.domain.event;

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
public class EventEntry extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Boolean isWinner = false;

    @Builder
    public EventEntry(Event event, User user) {
        this.event = event;
        this.user = user;
        this.isWinner = false;
    }

    public void markAsWinner() {
        this.isWinner = true;
    }
}