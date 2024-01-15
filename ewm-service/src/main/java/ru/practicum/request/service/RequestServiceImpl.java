package ru.practicum.request.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.event.model.Event;
import ru.practicum.event.storage.EventStorage;
import ru.practicum.exception.ConflictException;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestMapper;
import ru.practicum.request.storage.RequestStorage;
import ru.practicum.user.model.User;
import ru.practicum.util.UnionService;
import ru.practicum.util.enums.State;
import ru.practicum.util.enums.Status;

@Slf4j
@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestStorage requestStorage;
    private final EventStorage eventStorage;
    private final UnionService unionService;

    @Override
    @Transactional
    public RequestDto addRequest(Long userId, Long eventId) {

        User user = unionService.getUserOrNotFound(userId);
        Event event = unionService.getEventOrNotFound(eventId);

        if (event.getParticipantLimit() <= event.getConfirmedRequests() && event.getParticipantLimit() != 0) {
            throw new ConflictException(String.format("Превышен лимит запросов"));
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException(String.format("Пользователь id %s является организатором", user.getId()));
        }

        if (requestStorage.findByRequesterIdAndEventId(userId, eventId).isPresent()) {
            throw new ConflictException(String.format("Повторный запрос не допускается", event.getTitle()));
        }

        if (event.getState() != State.PUBLISHED) {
            throw new ConflictException(String.format("Событие id %s не опубликовано", eventId));
        } else {

            Request request = Request.builder()
                    .requester(user)
                    .event(event)
                    .created(LocalDateTime.now())
                    .status(Status.PENDING)
                    .build();

            if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
                request.setStatus(Status.CONFIRMED);
                request = requestStorage.save(request);
                event.setConfirmedRequests(requestStorage.countAllByEventIdAndStatus(eventId, Status.CONFIRMED));
                eventStorage.save(event);

                return RequestMapper.returnRequestDto(request);
            }

            request = requestStorage.save(request);

            return RequestMapper.returnRequestDto(request);
        }
    }

    @Override
    public List<RequestDto> getRequestsByUserId(Long userId) {

        unionService.getUserOrNotFound(userId);
        List<Request> requestList = requestStorage.findByRequesterId(userId);

        return RequestMapper.returnRequestDtoList(requestList);
    }

    @Override
    @Transactional
    public RequestDto cancelRequest(Long userId, Long requestId) {

        unionService.getUserOrNotFound(userId);
        Request request = unionService.getRequestOrNotFound(requestId);
        request.setStatus(Status.CANCELED);

        return RequestMapper.returnRequestDto(requestStorage.save(request));
    }
}
