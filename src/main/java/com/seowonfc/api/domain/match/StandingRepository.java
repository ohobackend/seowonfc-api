package com.seowonfc.api.domain.match;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StandingRepository extends JpaRepository<Standing, Long> {
    List<Standing> findBySeasonOrderByRankAsc(Integer season);
}