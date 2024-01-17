package ru.practicum.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.practicum.request.model.Request;
import ru.practicum.util.enums.Status;

import java.util.List;
import java.util.Optional;

public interface RequestStorage extends JpaRepository<Request, Long> {

    List<Request> findByRequesterId(Long userId);

    List<Request> findByEventId(Long eventId);

    Optional<Request> findByRequesterIdAndEventId(Long userId, Long eventId);

    Long countAllByEventIdAndStatus(Long eventId, Status status);
}
