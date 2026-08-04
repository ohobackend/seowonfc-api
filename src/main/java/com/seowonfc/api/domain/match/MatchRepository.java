package com.seowonfc.api.domain.match;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchRepository extends JpaRepository<Match, Long> {

    @Query("""
           SELECT m FROM Match m
           WHERE (:season IS NULL OR m.season = :season)
             AND (:status IS NULL OR m.status = :status)
           ORDER BY m.matchDate ASC
           """)
    Page<Match> search(@Param("season") Integer season,
                       @Param("status") MatchStatus status,
                       Pageable pageable);
}