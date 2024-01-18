package ru.practicum.util;

import static ru.practicum.util.Util.FORMATTER;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.practicum.category.model.Category;
import ru.practicum.category.storage.CategoryStorage;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.storage.CommentStorage;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.storage.CompilationStorage;
import ru.practicum.event.model.Event;
import ru.practicum.event.storage.EventStorage;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.model.Request;
import ru.practicum.request.storage.RequestStorage;
import ru.practicum.user.model.User;
import ru.practicum.user.storage.UserStorage;

@Service
@RequiredArgsConstructor
public class UnionServiceImpl implements UnionService {

    private final UserStorage userStorage;
    private final CategoryStorage categoryStorage;
    private final EventStorage eventStorage;
    private final RequestStorage requestStorage;
    private final CompilationStorage compilationStorage;
    private final CommentStorage commentRepository;

    @Override
    public User getUserOrNotFound(Long userId) {

        Optional<User> user = userStorage.findById(userId);

        if (user.isEmpty()) {
            throw new NotFoundException(User.class, "User id " + userId + " not found.");
        } else {
            return user.get();
        }
    }

    @Override
    public Category getCategoryOrNotFound(Long categoryId) {

        Optional<Category> category = categoryStorage.findById(categoryId);

        if (category.isEmpty()) {
            throw new NotFoundException(Category.class, "Category id " + categoryId + " not found.");
        } else {
            return category.get();
        }
    }

    @Override
    public Event getEventOrNotFound(Long eventId) {

        Optional<Event> event = eventStorage.findById(eventId);

        if (event.isEmpty()) {
            throw new NotFoundException(Event.class, "Event id " + eventId + " not found.");
        } else {
            return event.get();
        }
    }

    @Override
    public Request getRequestOrNotFound(Long requestId) {

        Optional<Request> request = requestStorage.findById(requestId);

        if (request.isEmpty()) {
            throw new NotFoundException(Request.class, "Request id " + requestId + " not found.");
        } else {
            return request.get();
        }
    }

    @Override
    public Compilation getCompilationOrNotFound(Long compId) {

        Optional<Compilation> compilation = compilationStorage.findById(compId);

        if (compilation.isEmpty()) {
            throw new NotFoundException(Compilation.class, "Compilation id " + compId + " not found.");
        } else {
            return compilation.get();
        }
    }

    @Override
    public LocalDateTime parseDate(String date) {
        if (date != null) {
            return LocalDateTime.parse(date, FORMATTER);
        } else {
            return null;
        }
    }

    @Override
    public Comment getCommentOrNotFound(Long commentId) {

        Optional<Comment> comment = commentRepository.findById(commentId);

        if (comment.isEmpty()) {
            throw new NotFoundException(Comment.class, "Comment id " + commentId + " not found.");
        } else {
            return comment.get();
        }
    }
}