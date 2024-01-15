package ru.practicum.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.exception.StatsValidationException;
import ru.practicum.model.HitMapper;
import ru.practicum.storage.HitStorage;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HitServiceImpl implements HitService {

    private final HitStorage hitStorage;

    @Transactional
    @Override
    public void addHit(HitDto hitDto) {

        hitStorage.save(HitMapper.returnHit(hitDto));
    }

    @Override
    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

        if (start != null && end != null) {
            if (start.isAfter(end)) {
                throw new StatsValidationException("Начало не может быть раньше окончания");
            }
        }

        if (uris == null || uris.isEmpty()) {
            if (unique) {
                return hitStorage.findAllStatsByUniqIp(start, end);
            } else {
                return hitStorage.findAllStats(start, end);
            }
        } else {
            if (unique) {
                return hitStorage.findStatsByUrisByUniqIp(start, end, uris);
            } else {
                return hitStorage.findStatsByUris(start, end, uris);
            }
        }
    }
}
