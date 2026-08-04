package com.seowonfc.api.domain.player;

import com.seowonfc.api.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Player extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Integer backNumber;

    @Enumerated(EnumType.STRING)
    private Position position;

    private String nationality;

    private String profileImageUrl;

    @Builder
    public Player(String name, Integer backNumber, Position position,
                  String nationality, String profileImageUrl) {
        this.name = name;
        this.backNumber = backNumber;
        this.position = position;
        this.nationality = nationality;
        this.profileImageUrl = profileImageUrl;
    }

    public void update(String name, Integer backNumber, Position position,
                       String nationality, String profileImageUrl) {
        this.name = name;
        this.backNumber = backNumber;
        this.position = position;
        this.nationality = nationality;
        this.profileImageUrl = profileImageUrl;
    }
}