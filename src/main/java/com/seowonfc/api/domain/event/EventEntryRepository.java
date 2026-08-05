package com.seowonfc.api.domain.event;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventEntryRepository extends JpaRepository<EventEntry, Long> {
    boolean existsByEventIdAndUserId(Long eventId, Long userId);
    List<EventEntry> findByEventIdAndIsWinnerTrue(Long eventId);
    List<EventEntry> findByEventId(Long eventId);
}