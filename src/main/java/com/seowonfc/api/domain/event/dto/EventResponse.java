package com.seowonfc.api.domain.event.dto;

import com.seowonfc.api.domain.event.Event;
import java.time.LocalDateTime;

public record EventResponse(
        Long id, String title, String content, LocalDateTime startDate, LocalDateTime endDate
) {
    public static EventResponse from(Event event) {
        return new EventResponse(event.getId(), event.getTitle(), event.getContent(),
                event.getStartDate(), event.getEndDate());
    }
}