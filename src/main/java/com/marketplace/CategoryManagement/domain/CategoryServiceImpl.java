package com.marketplace.CategoryManagement.domain;

import com.marketplace.CategoryManagement.api.CategoryDto;
import com.marketplace.CategoryManagement.api.CategoryRequestDto;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional
    public Category saveCategoryTest(Category category) {
        categoryRepository.save(category);
        return category;
    }

    public List<CategoryDto> getCategories() {
        return categoryRepository.getCategories();
    }

    public void createCategory(CategoryRequestDto categoryDto) {
        boolean checkIsThatTheSameCategory = getCategories().stream()
                .anyMatch(categoryDto1 -> categoryDto.categoryName().equals(categoryDto1.name()));
        if (checkIsThatTheSameCategory) {
            throw new IllegalArgumentException("You have already send the same category in to db");
        }
        Category category = new Category(categoryDto.categoryName());
        categoryRepository.save(category);
        System.out.println("category = " + category);
    }

    public void createCategories(List<CategoryRequestDto> categoryRequestDtos) {
        System.out.println("create categories executed");
        Map<String, Long> frequencyMap = categoryRequestDtos.stream()
                .collect(Collectors.groupingBy(
                        dto -> dto.categoryName().toLowerCase(),
                        Collectors.counting()));

        // Step 2: Check for duplicates within input DTOs
        String duplicatedElements = categoryRequestDtos.stream()
                .map(CategoryRequestDto::categoryName)
                .filter(name -> frequencyMap.get(name.toLowerCase()) > 1)
                .collect(Collectors.joining(", "));
        System.out.println("duplicated elements found nothing or not");

        if (!duplicatedElements.isBlank()) {
            throw new IllegalArgumentException("There are duplicated categories in the input: " + duplicatedElements);
        }

        System.out.println("duplicated element is not blank");

        // Step 3: Get lowercase category names from input DTOs
        List<String> categoryNameRequestDtosList = categoryRequestDtos.stream()
                .map(categoryRequestDto -> categoryRequestDto.categoryName().toLowerCase())
                .toList();

        System.out.println("found the category name request dtos list");
        // Step 4: Find duplicates between input DTOs and database categories
        // boolean isDbDuplicates = getCategories().stream()
        // .anyMatch(name -> name.equals())
        if (getCategories().size() > 1) {
            System.out.println("Still one");
            System.out.println("getCategories().getFirst().name() = " + getCategories().getFirst().name());
        }
        for (int i = 0; i < getCategories().size(); i++) {
            System.out.println("getCategories().get(i).name() = " + getCategories().get(i).name());
        }
        // System.out.println("categoryNameRequestDtosList = " +
        // categoryNameRequestDtosList);
        String dbDuplicates = getCategories().stream()
                .map(CategoryDto::name)
                .filter(name -> categoryNameRequestDtosList.contains(name.toLowerCase()))
                .collect(Collectors.joining(", "));

        // Step 5: Throw exception if duplicates are found in the database
        System.out.println("dbDuplicates = " + dbDuplicates);
        System.out.println("found duplicates nothing");
        if (!dbDuplicates.isBlank()) {
            throw new IllegalArgumentException("Categories already exist in the database: " + dbDuplicates);
        }

        // createCategories(categoryRequestDtos);
        System.out.println("transaction executed");
        List<Category> categories = categoryRequestDtos.stream()
                .map(s -> new Category(s.categoryName().toLowerCase()))
                .toList();
        System.out.println("categories = " + categories);
        System.out.println("categories.getFirst().getName() = " + categories.getFirst().getName());
        System.out.println("start to do transaction");
        if (categories.size() < 2) {
            System.out.println("category is less than two");
            Category category = new Category(categories.getFirst().getName());
            categoryRepository.save(category);
            return;
        }

        for (int i = 0; i < categories.size(); i++) {
            System.out.println("category is two or more");
            Category category = new Category(categories.get(i).getName());
            categoryRepository.save(category);

        }
        System.out.println("after persisting category");
        System.out.println("categories = " + categories);
        System.out.println("categories = " + categories);
    }

}
