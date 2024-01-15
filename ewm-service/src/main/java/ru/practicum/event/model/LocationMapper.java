package ru.practicum.event.model;

import lombok.experimental.UtilityClass;
import ru.practicum.event.dto.LocationDto;

@UtilityClass
public class LocationMapper {

    public LocationDto returnLocationDto(Location location) {
        LocationDto locationDto = LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
        return locationDto;
    }

    public Location returnLocation(LocationDto locationDto) {
        Location location = Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .build();
        return location;
    }
}