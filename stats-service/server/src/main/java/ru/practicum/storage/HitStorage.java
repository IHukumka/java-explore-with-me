package ru.practicum.storage;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.practicum.dto.StatsDto;
import ru.practicum.model.Hit;

public interface HitStorage extends JpaRepository<Hit, Long> {
    @Query("SELECT new ru.practicum.dto.StatsDto("
    		+ "h.uri, "
    		+ "h.app, "
    		+ "COUNT (h.id)) "
    		+ "FROM Hit AS h "
    		+ "WHERE (timestamp > :start "
    		+ "AND timestamp < :end) "
    		+ "GROUP BY app, uri, ip "
    		+ "ORDER BY COUNT(h.id) DESC")
	List<StatsDto> getStatsWithinTimeFrame(@Param("start") LocalDateTime start, @Param("end")LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.StatsDto("
    		+ "h.uri, "
    		+ "h.app, "
    		+ "COUNT (DISTINCT h.ip)) "
    		+ "FROM Hit AS h "
    		+ "WHERE (timestamp > :start "
    		+ "AND timestamp < :end) "
    		+ "GROUP BY app, uri, ip "
    		+ "ORDER BY COUNT(h.ip) DESC")
    List<StatsDto> getStatsWithinTimeFrameForUniqueIps(@Param("start") LocalDateTime start, @Param("end")LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.StatsDto("
    		+ "h.uri, "
    		+ "h.app, "
    		+ "COUNT (h.id)) "
    		+ "FROM Hit AS h "
    		+ "WHERE (timestamp > :start "
    		+ "AND timestamp < :end) "
    		+ "AND uri IN :uris "
    		+ "GROUP BY app, uri, ip "
    		+ "ORDER BY COUNT(h.id) DESC")
    List<StatsDto> getStatsWithinTimeFrameForUris(@Param("start") LocalDateTime start, @Param("end")LocalDateTime end, @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.dto.StatsDto("
    		+ "h.uri, "
    		+ "h.app, "
    		+ "COUNT (DISTINCT h.ip)) "
    		+ "FROM Hit AS h "
    		+ "WHERE (timestamp > :start "
    		+ "AND timestamp < :end) "
    		+ "AND uri IN :uris "
    		+ "GROUP BY app, uri, ip "
    		+ "ORDER BY COUNT(h.ip) DESC")
    List<StatsDto> getStatsWithinTimeFrameUniqueIpAndForUris(@Param("start") LocalDateTime start, @Param("end")LocalDateTime end, @Param("uris") List<String> uris);

}
