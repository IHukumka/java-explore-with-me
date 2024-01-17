package ru.practicum.category.model;

import java.util.ArrayList;
import java.util.List;

import lombok.experimental.UtilityClass;
import ru.practicum.category.dto.CategoryDto;

@UtilityClass
public class CategoryMapper {
    public CategoryDto returnCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public Category returnCategory(CategoryDto categoryDto) {
    	return Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .build();
    }

    public List<CategoryDto> returnCategoryDtoList(Iterable<Category> categories) {
        List<CategoryDto> result = new ArrayList<>();

        for (Category category : categories) {
            result.add(returnCategoryDto(category));
        }
        return result;
    }
}