package com.marketplace.Sub_Category_Management.domain;

import com.marketplace.Sub_Category_Management.api.SubCategoryDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

@Service("subCategory_SubCategoryManagement")
@Log4j2
public class SubCategoryServiceImpl implements SubCategoryService {

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Override
    public List<SubCategoryDto> getSubCategories() {
        System.out.println("begin transaction");
        return subCategoryRepository.getSubCategories();
    }

    @Override
    public void createSubCategory(String subCategoryName, Category category) {
        System.out.println("create sub category");
        boolean checkIsThatTheSameSubCategoryName = getSubCategories().stream()
                .anyMatch(subCategoryDto -> subCategoryDto.name().equals(subCategoryName));
        System.out.println("check is the same sub category name");
        if (checkIsThatTheSameSubCategoryName) {
            throw new IllegalArgumentException("You have already create subCategory");
        }
        SubCategory subCategory = new SubCategory(subCategoryName, category);
        subCategoryRepository.save(subCategory);
        System.out.println("subCategory = " + subCategory);
    }

    @Override
    public void createSubCategories(List<String> subCategoryNames, Category category) {
        Map<String, Long> frequencyMap = subCategoryNames.stream()
                .collect(Collectors.groupingBy(
                        String::toLowerCase,
                        Collectors.counting()));

        // Step 2: Check for duplicates within input DTOs
        String duplicateElements = subCategoryNames.stream()
                .map(String::toLowerCase)
                .filter(name -> frequencyMap.get(name.toLowerCase()) > 1)
                .collect(Collectors.joining(", "));

        if (!duplicateElements.isBlank()) {
            throw new IllegalArgumentException("There are duplicated categories in the input: " + duplicateElements);
        }

        System.out.println("duplicated element is not blank");

        List<String> subCategoryNameRequestDtosList = subCategoryNames.stream()
                .map(String::toLowerCase)
                .toList();

        System.out.println("found the category name request dtos list");

        String dbDuplicates = getSubCategories().stream()
                .map(SubCategoryDto::name)
                .filter(name -> subCategoryNameRequestDtosList.contains(name.toLowerCase()))
                .collect(Collectors.joining());

        System.out.println("dbDuplicates = " + dbDuplicates);
        System.out.println("found duplicates nothing");
        if (!dbDuplicates.isBlank()) {
            throw new IllegalArgumentException("Categories already exist in the database: " + dbDuplicates);
        }

        List<SubCategory> subCategories = null;
        subCategories = subCategoryNames.stream()
                .map(s -> new SubCategory(s, category))
                .toList();

        if (subCategories.size() < 2) {
            SubCategory subCategory = new SubCategory(subCategories.getFirst().getSubCategoryName(), category);
            Set<SubCategory> subCategories1 = new HashSet<>();
            subCategories1.add(subCategory);
            category.setSubCategories(subCategories1);
            subCategoryRepository.save(subCategory);
            return;
        }

        for (int i = 0; i < subCategories.size(); i++) {
            SubCategory subCategory = new SubCategory(subCategories.get(i).getSubCategoryName(), category);
            Set<SubCategory> subCategories1 = new HashSet<>();
            subCategories1.add(subCategory);
            category.setSubCategories(subCategories1);
            subCategoryRepository.save(subCategory);
        }
        System.out.println("subCategories = " + subCategories);

    }
}
