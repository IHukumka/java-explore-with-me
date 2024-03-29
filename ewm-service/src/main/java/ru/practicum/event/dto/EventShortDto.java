package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;

import static ru.practicum.util.Util.DATE_FORMAT;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDto {

    String annotation;

    CategoryDto category;

    Long confirmedRequests;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    LocalDateTime eventDate;

    Long id;

    UserShortDto initiator;

    Boolean paid;

    String title;

    Long views;
}