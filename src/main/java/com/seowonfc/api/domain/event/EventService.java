package com.seowonfc.api.domain.event;

import com.seowonfc.api.common.CustomException;
import com.seowonfc.api.common.ErrorCode;
import com.seowonfc.api.domain.event.dto.EventRequest;
import com.seowonfc.api.domain.event.dto.EventResponse;
import com.seowonfc.api.domain.event.dto.WinnerResponse;
import com.seowonfc.api.domain.user.User;
import com.seowonfc.api.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final EventEntryRepository eventEntryRepository;
    private final UserRepository userRepository;

    public Page<EventResponse> getList(Pageable pageable) {
        return eventRepository.findAll(pageable).map(EventResponse::from);
    }

    public EventResponse getDetail(Long eventId) {
        return EventResponse.from(findEvent(eventId));
    }

    @Transactional
    public void apply(Long eventId, Long userId) {
        Event event = findEvent(eventId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        if (eventEntryRepository.existsByEventIdAndUserId(eventId, userId)) {
            throw new CustomException(ErrorCode.CONFLICT); // 중복 응모 방지
        }

        eventEntryRepository.save(EventEntry.builder().event(event).user(user).build());
    }

    public List<WinnerResponse> getWinners(Long eventId) {
        return eventEntryRepository.findByEventIdAndIsWinnerTrue(eventId).stream()
                .map(entry -> new WinnerResponse(entry.getUser().getId(), entry.getUser().getName()))
                .toList();
    }

    // ---- 관리자용 ----
    @Transactional
    public Long create(EventRequest request) {
        Event event = Event.builder()
                .title(request.title())
                .content(request.content())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .build();
        return eventRepository.save(event).getId();
    }

    @Transactional
    public void update(Long eventId, EventRequest request) {
        Event event = findEvent(eventId);
        event.update(request.title(), request.content(), request.startDate(), request.endDate());
    }

    @Transactional
    public void delete(Long eventId) {
        eventRepository.delete(findEvent(eventId));
    }

    @Transactional
    public void selectWinners(Long eventId, List<Long> userIds) {
        List<EventEntry> entries = eventEntryRepository.findByEventId(eventId);
        entries.stream()
                .filter(entry -> userIds.contains(entry.getUser().getId()))
                .forEach(EventEntry::markAsWinner);
    }

    private Event findEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    }
}