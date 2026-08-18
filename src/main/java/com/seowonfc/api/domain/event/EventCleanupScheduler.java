package com.seowonfc.api.domain.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventCleanupScheduler {

    private final EventRepository eventRepository;
    private final EventEntryRepository eventEntryRepository;

    @Scheduled(cron = "0 0 3 * * *")
    public void deleteExpiredEvents() {
        LocalDate cutoff = LocalDate.now().minusDays(3);
        List<Event> expired = eventRepository.findByEventDateBefore(cutoff);

        for (Event event : expired) {
            eventEntryRepository.deleteAll(eventEntryRepository.findByEventId(event.getId()));
            eventRepository.delete(event);
        }

        if (!expired.isEmpty()) {
            log.info("Deleted {} expired events (eventDate before {})", expired.size(), cutoff);
        }
    }
}
