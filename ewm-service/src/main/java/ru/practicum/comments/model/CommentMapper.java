package ru.practicum.comments.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.experimental.UtilityClass;
import ru.practicum.comments.dto.CommentFullDto;
import ru.practicum.comments.dto.CommentNewDto;
import ru.practicum.comments.dto.CommentShortDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.model.UserMapper;


@UtilityClass
public class CommentMapper {

    public Comment returnComment(CommentNewDto commentNewDto, User user, Event event) {
        Comment comment = Comment.builder()
                .user(user)
                .event(event)
                .message(commentNewDto.getMessage())
                .created(LocalDateTime.now())
                .build();
        return comment;
    }

    public CommentFullDto returnCommentFullDto(Comment comment) {
        CommentFullDto commentFullDto = CommentFullDto.builder()
                .id(comment.getId())
                .user(UserMapper.returnUserDto(comment.getUser()))
                .event(EventMapper.returnEventFullDto(comment.getEvent()))
                .message(comment.getMessage())
                .created(comment.getCreated())
                .build();
        return commentFullDto;
    }

    public CommentShortDto returnCommentShortDto(Comment comment) {
        CommentShortDto commentShortDto = CommentShortDto.builder()
                .userName(comment.getUser().getName())
                .eventTitle(comment.getEvent().getTitle())
                .message(comment.getMessage())
                .created(comment.getCreated())
                .build();
        return commentShortDto;
    }

    public List<CommentFullDto> returnCommentFullDtoList(Iterable<Comment> comments) {
        List<CommentFullDto> result = new ArrayList<>();

        for (Comment comment : comments) {
            result.add(returnCommentFullDto(comment));
        }
        return result;
    }

    public List<CommentShortDto> returnCommentShortDtoList(Iterable<Comment> comments) {
        List<CommentShortDto> result = new ArrayList<>();

        for (Comment comment : comments) {
            result.add(returnCommentShortDto(comment));
        }
        return result;
    }
}
