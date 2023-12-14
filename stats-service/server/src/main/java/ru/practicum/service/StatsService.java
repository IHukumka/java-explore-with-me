package ru.practicum.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;

import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;

public interface StatsService {

    List<StatsDto> get(LocalDateTime startDate, LocalDateTime endDate, List<String> uris, boolean unique);

	HitDto create(@Valid HitDto hitDto);
}
