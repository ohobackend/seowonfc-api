package com.seowonfc.api.domain.match.dto;

import com.seowonfc.api.domain.match.Match;
import com.seowonfc.api.domain.match.MatchStatus;
import java.time.LocalDateTime;

public record MatchResponse(
        Long id, Integer season, Integer round, String competition,
        String homeTeam, String awayTeam, LocalDateTime matchDate, String stadium,
        MatchStatus status, Integer homeScore, Integer awayScore
) {
    public static MatchResponse from(Match match) {
        return new MatchResponse(match.getId(), match.getSeason(), match.getRound(),
                match.getCompetition(), match.getHomeTeam(), match.getAwayTeam(),
                match.getMatchDate(), match.getStadium(), match.getStatus(),
                match.getHomeScore(), match.getAwayScore());
    }
}