package ru.practicum.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.model.HitMapper;
import ru.practicum.storage.HitStorage;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final HitStorage storage;

    @Override
	public HitDto create(HitDto hitDto) {
		return HitMapper.toDto(storage.save(HitMapper.toHit(hitDto)));
	}

	@Override
	public List<StatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
		List<StatsDto> result = new ArrayList<>();
		if (uris.isEmpty()) {
			if (unique) {
				result = storage.getStatsWithinTimeFrameForUniqueIps(start, end);
			} else {
				result = storage.getStatsWithinTimeFrame(start, end);
			}
		} else {
			if (unique) {
				result = storage.getStatsWithinTimeFrameUniqueIpAndForUris(start, end, uris);
			} else {
				result = storage.getStatsWithinTimeFrameForUris(start, end, uris);
			}
		}
		return result;
	}
}
