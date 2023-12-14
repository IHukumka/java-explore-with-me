package ru.practicum.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class HitDto {

    private Long id;

    @NotNull
    @NotBlank
    @Size(max = 50)
    private String app;

    @NotNull
    @NotBlank
    @Size(max = 255)
    private String uri;

    @NotNull
    @NotBlank
    @Size(max = 15)
    private String ip;

    @NotNull
    @NotBlank
    private String timestamp;
}