package ru.practicum.comments.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.comments.dto.CommentFullDto;
import ru.practicum.comments.dto.CommentNewDto;
import ru.practicum.comments.dto.CommentShortDto;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.model.CommentMapper;
import ru.practicum.comments.storage.CommentStorage;
import ru.practicum.event.model.Event;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.model.User;
import ru.practicum.util.UnionService;

@Slf4j
@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final UnionService unionService;
    private final CommentStorage commentStorage;

    @Override
    @Transactional
    public CommentFullDto addComment(Long userId, Long eventId, CommentNewDto commentNewDto) {

        User user = unionService.getUserOrNotFound(userId);
        Event event = unionService.getEventOrNotFound(eventId);

        Comment comment = CommentMapper.returnComment(commentNewDto, user,event);
        comment = commentStorage.save(comment);

        return CommentMapper.returnCommentFullDto(comment);
    }

    @Override
    @Transactional
    public CommentFullDto updateComment(Long userId, Long commentId, CommentNewDto commentNewDto) {

        Comment comment = unionService.getCommentOrNotFound(commentId);

        if (!userId.equals(comment.getUser().getId())) {
            throw new ConflictException(String.format("Пользователь id %s не является автором комментария %s.",userId, commentId));
        }

        if (commentNewDto.getMessage() != null && !commentNewDto.getMessage().isBlank()) {
            comment.setMessage(commentNewDto.getMessage());
        }

        comment = commentStorage.save(comment);

        return CommentMapper.returnCommentFullDto(comment);
    }

    @Override
    @Transactional
    public void deletePrivateComment(Long userId, Long commentId) {

        Comment comment = unionService.getCommentOrNotFound(commentId);
        unionService.getUserOrNotFound(userId);

        if (!comment.getUser().getId().equals(userId)) {
            throw new ConflictException(String.format("Пользователь id %s не является автором комментария %s.",userId, commentId));
        }

        commentStorage.deleteById(commentId);
    }

    @Override
    public List<CommentShortDto> getCommentsByUserId(String rangeStart, String rangeEnd, Long userId, Integer from, Integer size) {

        unionService.getUserOrNotFound(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size);

        LocalDateTime startTime = unionService.parseDate(rangeStart);
        LocalDateTime endTime = unionService.parseDate(rangeEnd);

        if (startTime != null && endTime != null) {
            if (startTime.isAfter(endTime)) {
                throw new ValidationException("Начало не может быть после завершения");
            }
            if (endTime.isAfter(LocalDateTime.now()) || startTime.isAfter(LocalDateTime.now())) {
                throw new ValidationException("Дата больше текущей");
            }
        }

        List<Comment> commentList = commentStorage.getCommentsByUserId(userId, startTime, endTime, pageRequest);

        return CommentMapper.returnCommentShortDtoList(commentList);
    }

    @Override
    public List<CommentFullDto> getComments(String rangeStart, String rangeEnd, Integer from, Integer size) {

        PageRequest pageRequest = PageRequest.of(from / size, size);

        LocalDateTime startTime = unionService.parseDate(rangeStart);
        LocalDateTime endTime = unionService.parseDate(rangeEnd);

        if (startTime != null && endTime != null) {
            if (startTime.isAfter(endTime)) {
                throw new ValidationException("Начало не может быть после завершения");
            }
            if (endTime.isAfter(LocalDateTime.now()) || startTime.isAfter(LocalDateTime.now())) {
                throw new ValidationException("Дата больше текущей");
            }
        }

        List<Comment> commentList = commentStorage.getComments(startTime, endTime, pageRequest);

        return CommentMapper.returnCommentFullDtoList(commentList);
    }

    @Override
    @Transactional
    public void deleteAdminComment(Long commentId) {

        unionService.getCommentOrNotFound(commentId);
        commentStorage.deleteById(commentId);
    }

    @Override
    public List<CommentShortDto> getCommentsByEventId(String rangeStart, String rangeEnd, Long eventId, Integer from, Integer size) {

        unionService.getEventOrNotFound(eventId);
        PageRequest pageRequest = PageRequest.of(from / size, size);

        LocalDateTime startTime = unionService.parseDate(rangeStart);
        LocalDateTime endTime = unionService.parseDate(rangeEnd);

        if (startTime != null && endTime != null) {
            if (startTime.isAfter(endTime)) {
                throw new ValidationException("Начало не может быть после завершения");
            }
            if (endTime.isAfter(LocalDateTime.now()) || startTime.isAfter(LocalDateTime.now())) {
                throw new ValidationException("Дата больше текущей");
            }
        }

        List<Comment> commentList = commentStorage.getCommentsByEventId(eventId, startTime, endTime, pageRequest);

        return CommentMapper.returnCommentShortDtoList(commentList);
    }
}