package ru.practicum.comments.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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
public class CommentNewDto {

    @NotBlank(message = "Комментарий не может быть пустым.")
    @Size(max = 500, message = "Превышен лимит количества знаков в комментарии - 500 знаков.")
    String message;
}
