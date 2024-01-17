package ru.practicum.event.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Location;

@Repository
public interface LocationStorage extends JpaRepository<Location, Long> {
}
