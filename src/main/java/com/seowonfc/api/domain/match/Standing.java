package com.seowonfc.api.domain.match;

import com.seowonfc.api.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Standing extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer season;
    private String team;
    private Integer rank;
    private Integer played;
    private Integer win;
    private Integer draw;
    private Integer lose;
    private Integer points;
    private Integer goalDiff;

    @Builder
    public Standing(Integer season, String team, Integer rank, Integer played,
                    Integer win, Integer draw, Integer lose, Integer points, Integer goalDiff) {
        this.season = season;
        this.team = team;
        this.rank = rank;
        this.played = played;
        this.win = win;
        this.draw = draw;
        this.lose = lose;
        this.points = points;
        this.goalDiff = goalDiff;
    }

    public void update(Integer rank, Integer played, Integer win, Integer draw,
                       Integer lose, Integer points, Integer goalDiff) {
        this.rank = rank;
        this.played = played;
        this.win = win;
        this.draw = draw;
        this.lose = lose;
        this.points = points;
        this.goalDiff = goalDiff;
    }
}