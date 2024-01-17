package ru.practicum.category.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.model.CategoryMapper;
import ru.practicum.category.storage.CategoryStorage;
import ru.practicum.event.storage.EventStorage;
import ru.practicum.exception.ConflictException;
import ru.practicum.util.UnionService;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryStorage categoryStorage;
    private final EventStorage eventStorage;
    private final UnionService unionService;

    @Transactional
    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {

        Category category = CategoryMapper.returnCategory(categoryDto);
        categoryStorage.save(category);

        return CategoryMapper.returnCategoryDto(category);
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto, Long categoryId) {

        Category category = unionService.getCategoryOrNotFound(categoryId);
        category.setName(categoryDto.getName());
        categoryStorage.save(category);

        return CategoryMapper.returnCategoryDto(category);
    }

    @Transactional
    @Override
    public void deleteCategory(Long categoryId) {

        unionService.getCategoryOrNotFound(categoryId);

        if (!eventStorage.findByCategoryId(categoryId).isEmpty()) {
            throw new ConflictException(String.format("Категория id %s используется и не может быть удалена", categoryId));
        }

        categoryStorage.deleteById(categoryId);
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {

        PageRequest pageRequest = PageRequest.of(from / size, size);

        return CategoryMapper.returnCategoryDtoList(categoryStorage.findAll(pageRequest));
    }

    @Override
    public CategoryDto getCategoryById(Long categoryId) {

        return CategoryMapper.returnCategoryDto(unionService.getCategoryOrNotFound(categoryId));
    }
}