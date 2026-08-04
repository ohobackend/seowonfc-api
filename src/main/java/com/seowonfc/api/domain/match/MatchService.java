package com.seowonfc.api.domain.match;

import com.seowonfc.api.common.CustomException;
import com.seowonfc.api.common.ErrorCode;
import com.seowonfc.api.domain.match.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchService {

    private final MatchRepository matchRepository;
    private final StandingRepository standingRepository;

    public Page<MatchResponse> getList(Integer season, MatchStatus status, Pageable pageable) {
        return matchRepository.search(season, status, pageable).map(MatchResponse::from);
    }

    public MatchResponse getDetail(Long id) {
        return MatchResponse.from(findById(id));
    }

    @Transactional
    public Long create(MatchRequest request) {
        Match match = Match.builder()
                .season(request.season())
                .round(request.round())
                .competition(request.competition())
                .homeTeam(request.homeTeam())
                .awayTeam(request.awayTeam())
                .matchDate(request.matchDate())
                .stadium(request.stadium())
                .build();
        return matchRepository.save(match).getId();
    }

    @Transactional
    public void updateResult(Long id, MatchResultRequest request) {
        Match match = findById(id);
        match.updateResult(request.status(), request.homeScore(), request.awayScore());
    }

    @Transactional
    public void delete(Long id) {
        matchRepository.delete(findById(id));
    }

    // ---- Standing ----
    public List<StandingResponse> getStandings(Integer season) {
        return standingRepository.findBySeasonOrderByRankAsc(season).stream()
                .map(StandingResponse::from)
                .toList();
    }

    @Transactional
    public void upsertStanding(StandingRequest request) {
        Standing standing = Standing.builder()
                .season(request.season())
                .team(request.team())
                .rank(request.rank())
                .played(request.played())
                .win(request.win())
                .draw(request.draw())
                .lose(request.lose())
                .points(request.points())
                .goalDiff(request.goalDiff())
                .build();
        standingRepository.save(standing);
    }

    private Match findById(Long id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    }
}