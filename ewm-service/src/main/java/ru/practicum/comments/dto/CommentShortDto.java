package ru.practicum.comments.dto;

import static ru.practicum.util.Util.DATE_FORMAT;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentShortDto {

    String userName;

    String eventTitle;

    String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    LocalDateTime created;
}
