package ru.practicum.user.storage;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.practicum.user.model.User;

import java.util.List;

@Repository
public interface UserStorage extends JpaRepository<User, Long> {
    List<User> findByIdInOrderByIdAsc(List<Long> ids, PageRequest pageRequest);
}