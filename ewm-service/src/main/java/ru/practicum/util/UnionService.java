package ru.practicum.util;

import java.time.LocalDateTime;

import ru.practicum.category.model.Category;
import ru.practicum.comments.model.Comment;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.model.Event;
import ru.practicum.request.model.Request;
import ru.practicum.user.model.User;

public interface UnionService {

    User getUserOrNotFound(Long userId);

    Category getCategoryOrNotFound(Long categoryId);

    Event getEventOrNotFound(Long eventId);

    Request getRequestOrNotFound(Long requestId);

    Comment getCommentOrNotFound(Long commentId);

    Compilation getCompilationOrNotFound(Long compId);

    LocalDateTime parseDate(String date);
}
