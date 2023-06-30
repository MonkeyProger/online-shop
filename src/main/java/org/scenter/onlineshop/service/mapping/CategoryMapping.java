package org.scenter.onlineshop.service.mapping;

import org.scenter.onlineshop.domain.Category;
import org.scenter.onlineshop.dto.CategoryDTO;
import org.scenter.onlineshop.dto.ProductDTO;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryMapping {
    public static CategoryDTO convertCategoryToDTO(Category category){
        List<ProductDTO> productDTOS = category.getProducts()
                .stream()
                .map(ProductMapping::convertProductToDTO)
                .collect(Collectors.toList());
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setTitle(category.getTitle());
        dto.setProducts(productDTOS);
        dto.setParentId(category.getParentId());
        dto.setName(category.getName());
        return dto;
    }

    public static List<CategoryDTO> listToDTO(List<Category> categories){
        return categories.stream().map(CategoryMapping::convertCategoryToDTO)
                .collect(Collectors.toList());
    }
}