package ru.practicum.compilation.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.compilation.dto.CompilationUpdateDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.CompilationMapper;
import ru.practicum.compilation.storage.CompilationStorage;
import ru.practicum.event.storage.EventStorage;
import ru.practicum.util.UnionService;

@Slf4j
@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationStorage compilationStorage;
    private final EventStorage eventStorage;
    private final UnionService unionService;

    @Override
    @Transactional
    public CompilationDto addCompilation(CompilationNewDto compilationNewDto) {

        Compilation compilation = CompilationMapper.returnCompilation(compilationNewDto);

        if (compilation.getPinned() == null) {
            compilation.setPinned(false);
        }
        if (compilationNewDto.getEvents() == null || compilationNewDto.getEvents().isEmpty()) {
            compilation.setEvents(Collections.emptySet());
        } else {
            compilation.setEvents(eventStorage.findByIdIn(compilationNewDto.getEvents()));
        }

        compilation = compilationStorage.save(compilation);
        return CompilationMapper.returnCompilationDto(compilation);
    }

    @Override
    @Transactional

    public void deleteCompilation(Long compId) {

        unionService.getCompilationOrNotFound(compId);
        compilationStorage.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, CompilationUpdateDto compilationUpdateDto) {

        Compilation compilation = unionService.getCompilationOrNotFound(compId);

        if (compilation.getPinned() == null) {
            compilation.setPinned(false);
        }

        if (compilationUpdateDto.getEvents() == null || compilationUpdateDto.getEvents().isEmpty()) {
            compilation.setEvents(Collections.emptySet());
        } else {
            compilation.setEvents(eventStorage.findByIdIn(compilationUpdateDto.getEvents()));
        }

        if (compilationUpdateDto.getTitle() != null) {
            compilation.setTitle(compilationUpdateDto.getTitle());
        }

        compilation = compilationStorage.save(compilation);
        return CompilationMapper.returnCompilationDto(compilation);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {

        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Compilation> compilations;

        if (pinned) {
            compilations = compilationStorage.findByPinned(true, pageRequest);
        } else {
            compilations = compilationStorage.findAll(pageRequest).getContent();
        }
        return new ArrayList<>(CompilationMapper.returnCompilationDtoSet(compilations));
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {

        Compilation compilation = unionService.getCompilationOrNotFound(compId);

        return CompilationMapper.returnCompilationDto(compilation);
    }
}
