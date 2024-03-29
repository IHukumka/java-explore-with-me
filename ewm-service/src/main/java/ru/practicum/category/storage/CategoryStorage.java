package ru.practicum.category.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.practicum.category.model.Category;

@Repository
public interface CategoryStorage extends JpaRepository<Category, Long> {
}
