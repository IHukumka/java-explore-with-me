package ru.practicum.compilation.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.experimental.UtilityClass;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.EventMapper;

@UtilityClass
public class CompilationMapper {

    public CompilationDto returnCompilationDto(Compilation compilation) {

        List<EventShortDto> eventShortDtoList = EventMapper.returnEventShortDtoList(compilation.getEvents());

        Set<EventShortDto> eventShortDtoSet = new HashSet<>(eventShortDtoList);

        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(eventShortDtoSet)
                .build();
    }

    public Compilation returnCompilation(CompilationNewDto compilationNewDto) {
        return Compilation.builder()
                .title(compilationNewDto.getTitle())
                .pinned(compilationNewDto.getPinned())
                .build();
    }

    public Set<CompilationDto> returnCompilationDtoSet(Iterable<Compilation> compilations) {

        Set<CompilationDto> result = new HashSet<>();
        for (Compilation compilation : compilations) {
            result.add(returnCompilationDto(compilation));
        }
        return result;
    }
}
