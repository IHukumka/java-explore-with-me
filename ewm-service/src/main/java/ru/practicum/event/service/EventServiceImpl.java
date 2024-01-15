package ru.practicum.event.service;

import static ru.practicum.util.Util.START_HISTORY;
import static ru.practicum.util.enums.State.PUBLISHED;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.category.model.Category;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventUpdateDto;
import ru.practicum.event.dto.RequestUpdateDtoRequest;
import ru.practicum.event.dto.RequestUpdateDtoResult;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventMapper;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.LocationMapper;
import ru.practicum.event.storage.EventStorage;
import ru.practicum.event.storage.LocationStorage;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestMapper;
import ru.practicum.request.storage.RequestStorage;
import ru.practicum.user.model.User;
import ru.practicum.util.UnionService;
import ru.practicum.util.enums.State;
import ru.practicum.util.enums.StateAction;
import ru.practicum.util.enums.Status;

@Slf4j
@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class EventServiceImpl implements EventService {

    private final UnionService unionService;
    private final EventStorage eventStorage;
    private final RequestStorage requestStorage;
    private final LocationStorage locationStorage;
    private final StatsClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    @Transactional
    public EventFullDto addEvent(Long userId, EventNewDto eventNewDto) {

        User user = unionService.getUserOrNotFound(userId);
        Category category = unionService.getCategoryOrNotFound(eventNewDto.getCategory());
        Location location = locationStorage.save(LocationMapper.returnLocation(eventNewDto.getLocation()));
        Event event = EventMapper.returnEvent(eventNewDto, category, location, user);
        eventStorage.save(event);

        return EventMapper.returnEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getAllEventsByUserId(Long userId, Integer from, Integer size) {

        unionService.getUserOrNotFound(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Event> events = eventStorage.findByInitiatorId(userId, pageRequest);

        return EventMapper.returnEventShortDtoList(events);
    }

    @Override
    public EventFullDto getUserEventById(Long userId, Long eventId) {

        unionService.getUserOrNotFound(userId);
        unionService.getEventOrNotFound(eventId);
        Event event = eventStorage.findByInitiatorIdAndId(userId,eventId);

        return EventMapper.returnEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUserId(EventUpdateDto eventUpdateDto, Long userId, Long eventId) {

        User user = unionService.getUserOrNotFound(userId);
        Event event = unionService.getEventOrNotFound(eventId);

        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException(String.format("Пользователь %s не имеет доступа к событию id %s.",userId, eventId));
        }
        if (event.getState().equals(PUBLISHED)) {
            throw new ConflictException(String.format("Пользователь %s не может обновить событие %s, т.к. оно уже опубликовано.",userId, eventId));
        }

        Event updateEvent = baseUpdateEvent(event, eventUpdateDto);

        return EventMapper.returnEventFullDto(updateEvent);
    }

    @Override
    public List<RequestDto> getRequestsForEventIdByUserId(Long userId, Long eventId) {

        User user = unionService.getUserOrNotFound(userId);
        Event event = unionService.getEventOrNotFound(eventId);

        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException(String.format("Пользователь %s не имеет доступа к событию id %s.",userId, eventId));
        }

        List<Request> requests = requestStorage.findByEventId(eventId);

        return RequestMapper.returnRequestDtoList(requests);
    }

    @Override
    @Transactional
    public RequestUpdateDtoResult updateStatusRequestsForEventIdByUserId(RequestUpdateDtoRequest requestDto, Long userId, Long eventId) {

        User user = unionService.getUserOrNotFound(userId);
        Event event = unionService.getEventOrNotFound(eventId);

        RequestUpdateDtoResult result = RequestUpdateDtoResult.builder()
                .confirmedRequests(Collections.emptyList())
                .rejectedRequests(Collections.emptyList())
                .build();

        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException(String.format("Пользователь %s не имеет доступа к событию id %s",userId, eventId));
        }

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            return result;
        }

        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("Превышен лимит участников");
        }

        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();

        long vacantPlace = event.getParticipantLimit() - event.getConfirmedRequests();

        List<Request> requestsList = requestStorage.findAllById(requestDto.getRequestIds());

        for (Request request : requestsList) {
            if (!request.getStatus().equals(Status.PENDING)) {
                throw new ConflictException("Ошибка статуса запроса");
            }

            if (requestDto.getStatus().equals(Status.CONFIRMED) && vacantPlace > 0) {
                request.setStatus(Status.CONFIRMED);
                event.setConfirmedRequests(requestStorage.countAllByEventIdAndStatus(eventId, Status.CONFIRMED));
                confirmedRequests.add(request);
                vacantPlace--;
            } else {
                request.setStatus(Status.REJECTED);
                rejectedRequests.add(request);
            }
        }

        result.setConfirmedRequests(RequestMapper.returnRequestDtoList(confirmedRequests));
        result.setRejectedRequests(RequestMapper.returnRequestDtoList(rejectedRequests));

        eventStorage.save(event);
        requestStorage.saveAll(requestsList);

        return result;
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(EventUpdateDto eventUpdateDto, Long eventId) {

        Event event = unionService.getEventOrNotFound(eventId);

        if (eventUpdateDto.getStateAction() != null) {
            if (eventUpdateDto.getStateAction().equals(StateAction.PUBLISH_EVENT)) {

                if (!event.getState().equals(State.PENDING)) {
                    throw new ConflictException(String.format("Событие id %s уже опубликовано", event.getTitle()));
                }
                event.setPublishedOn(LocalDateTime.now());
                event.setState(State.PUBLISHED);

            } else {

                if (!event.getState().equals(State.PENDING)) {
                    throw new ConflictException(String.format("Событие id %s не может быть опубликовано", event.getTitle()));
                }
                event.setState(State.CANCELED);
            }
        }

        Event updateEvent = baseUpdateEvent(event, eventUpdateDto);

        return EventMapper.returnEventFullDto(updateEvent);
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories, String rangeStart, String rangeEnd, Integer from, Integer size) {

        LocalDateTime startTime = unionService.parseDate(rangeStart);
        LocalDateTime endTime = unionService.parseDate(rangeEnd);

        List<State> statesValue = new ArrayList<>();

        if (states != null) {
            for (String state : states) {
                statesValue.add(State.getStateValue(state));
            }
        }

        if (startTime != null && endTime != null) {
            if (startTime.isAfter(endTime)) {
                throw new ValidationException("Начало не может быть до окончания");
            }
        }

        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Event> events = eventStorage.findEventsByAdminFromParam(users, statesValue, categories,  startTime, endTime, pageRequest);

        return EventMapper.returnEventFullDtoList(events);
    }

    @Override
    public EventFullDto getEventById(Long eventId, String uri, String ip) {

        Event event = unionService.getEventOrNotFound(eventId);
        if (!event.getState().equals(PUBLISHED)) {
           throw new NotFoundException(Event.class, String.format("Событие не опубликовано", eventId));
        }

        sendInfo(uri, ip);
        event.setViews(getViewsEventById(event.getId()));
        eventStorage.save(event);

        return EventMapper.returnEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getEventsByPublic(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size, String uri, String ip) {

        LocalDateTime startTime = unionService.parseDate(rangeStart);
        LocalDateTime endTime = unionService.parseDate(rangeEnd);

        if (startTime != null && endTime != null) {
            if (startTime.isAfter(endTime)) {
                throw new ValidationException("Начало не может быть до окончания");
            }
        }

        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Event> events = eventStorage.findEventsByPublicFromParam(text, categories, paid, startTime, endTime, onlyAvailable, sort, pageRequest);

        sendInfo(uri, ip);
        for (Event event : events) {
            event.setViews(getViewsEventById(event.getId()));
            eventStorage.save(event);
        }

        return EventMapper.returnEventShortDtoList(events);
    }

    private Event baseUpdateEvent(Event event, EventUpdateDto eventUpdateDto) {

        if (eventUpdateDto.getAnnotation() != null && !eventUpdateDto.getAnnotation().isBlank()) {
            event.setAnnotation(eventUpdateDto.getAnnotation());
        }
        if (eventUpdateDto.getCategory() != null) {
            event.setCategory(unionService.getCategoryOrNotFound(eventUpdateDto.getCategory()));
        }
        if (eventUpdateDto.getDescription() != null && !eventUpdateDto.getDescription().isBlank()) {
            event.setDescription(eventUpdateDto.getDescription());
        }
        if (eventUpdateDto.getEventDate() != null) {
            event.setEventDate(eventUpdateDto.getEventDate());
        }
        if (eventUpdateDto.getLocation() != null) {
            event.setLocation(LocationMapper.returnLocation(eventUpdateDto.getLocation()));
        }
        if (eventUpdateDto.getPaid() != null) {
            event.setPaid(eventUpdateDto.getPaid());
        }
        if (eventUpdateDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventUpdateDto.getParticipantLimit());
        }
        if (eventUpdateDto.getRequestModeration() != null) {
            event.setRequestModeration(eventUpdateDto.getRequestModeration());
        }
        if (eventUpdateDto.getStateAction() != null) {
            if (eventUpdateDto.getStateAction() == StateAction.PUBLISH_EVENT) {
                event.setState(PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (eventUpdateDto.getStateAction() == StateAction.REJECT_EVENT ||
                eventUpdateDto.getStateAction() == StateAction.CANCEL_REVIEW) {
                event.setState(State.CANCELED);
            } else if (eventUpdateDto.getStateAction() == StateAction.SEND_TO_REVIEW) {
                event.setState(State.PENDING);
            }
        }
        if (eventUpdateDto.getTitle() != null && !eventUpdateDto.getTitle().isBlank()) {
            event.setTitle(eventUpdateDto.getTitle());
        }

        locationStorage.save(event.getLocation());
        return eventStorage.save(event);
    }

    private void sendInfo(String uri, String ip) {
        HitDto hitDto = HitDto.builder()
                .app("ewm-service")
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();
        client.addHit(hitDto);
    }

    private Long getViewsEventById(Long eventId) {

        String uri = "/events/" + eventId;
        ResponseEntity<Object> response = client.findStats(START_HISTORY, LocalDateTime.now(), uri, true);
        List<StatsDto> result = objectMapper.convertValue(response.getBody(), new TypeReference<>() {});

        if (result.isEmpty()) {
            return 0L;
        } else {
            return result.get(0).getHits();
        }
    }
}