package ru.practicum.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import ru.practicum.dto.HitDto;

public class HitMapper {

	public static HitDto toDto(Hit hit) {
		return HitDto.builder()
				.id(hit.getId())
				.app(hit.getApp())
				.ip(hit.getIp())
				.uri(hit.getUri())
				.timestamp(hit.getTimestamp().toString())
				.build();
	}

	public static Hit toHit(HitDto hitDto) {
		return Hit.builder()
				.app(hitDto.getApp())
				.ip(hitDto.getIp())
				.uri(hitDto.getUri())
				.timestamp(LocalDateTime.parse(hitDto.getTimestamp(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
				.build();
	}
}
