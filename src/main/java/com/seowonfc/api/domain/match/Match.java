package com.seowonfc.api.domain.match;

import com.seowonfc.api.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Match extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer season;
    private Integer round;
    private String competition;

    @Column(nullable = false)
    private String homeTeam;

    @Column(nullable = false)
    private String awayTeam;

    private LocalDateTime matchDate;
    private String stadium;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchStatus status;

    private Integer homeScore;
    private Integer awayScore;

    @Builder
    public Match(Integer season, Integer round, String competition, String homeTeam,
                 String awayTeam, LocalDateTime matchDate, String stadium) {
        this.season = season;
        this.round = round;
        this.competition = competition;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.matchDate = matchDate;
        this.stadium = stadium;
        this.status = MatchStatus.SCHEDULED;
    }

    public void updateResult(MatchStatus status, Integer homeScore, Integer awayScore) {
        this.status = status;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
    }
}