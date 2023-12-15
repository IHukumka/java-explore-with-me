package ru.practicum.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.dto.HitDto;
import ru.practicum.service.StatsService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService service;

	@PostMapping(value = "/hit")
	@ResponseBody
	public ResponseEntity<Object> create(@RequestBody @Valid HitDto hitDto) {
		log.info("Получен запрос к эндпоинту: 'POST_HITS'. ");
		return ResponseEntity.ok(service.create(hitDto));
	}

    @GetMapping(value = "/stats")
    @ResponseBody
	public ResponseEntity<Object> getAll(
			@RequestParam String start,
			@RequestParam String end,
			@RequestParam(defaultValue = "") List<String> uris,
			@RequestParam(defaultValue = "false") boolean unique) {
		log.info("Получен запрос к эндпоинту: 'GET_STATS'. ");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	    LocalDateTime startDate = LocalDateTime.parse(start, formatter);
	    LocalDateTime endDate = LocalDateTime.parse(end, formatter);
		if (endDate.isBefore(startDate)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		return ResponseEntity.ok(service.get(startDate, endDate, uris, unique));
	}

}