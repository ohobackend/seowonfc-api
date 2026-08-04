package com.seowonfc.api.domain.match.dto;

import com.seowonfc.api.domain.match.Standing;

public record StandingResponse(
        Long id, Integer season, String team, Integer rank, Integer played,
        Integer win, Integer draw, Integer lose, Integer points, Integer goalDiff
) {
    public static StandingResponse from(Standing s) {
        return new StandingResponse(s.getId(), s.getSeason(), s.getTeam(), s.getRank(),
                s.getPlayed(), s.getWin(), s.getDraw(), s.getLose(), s.getPoints(), s.getGoalDiff());
    }
}