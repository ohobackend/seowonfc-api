package com.seowonfc.api.domain.event.dto;

import com.seowonfc.api.domain.event.Event;
import java.time.LocalDate;

public record EventResponse(
        Long id, String title, String content, LocalDate eventDate, String imageUrl
) {
    public static EventResponse from(Event event) {
        return new EventResponse(event.getId(), event.getTitle(), event.getContent(),
                event.getEventDate(), event.getImageUrl());
    }
}
