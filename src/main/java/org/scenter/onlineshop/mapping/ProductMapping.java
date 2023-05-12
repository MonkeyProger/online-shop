package org.scenter.onlineshop.mapping;

import org.scenter.onlineshop.domain.Category;
import org.scenter.onlineshop.domain.Product;
import org.scenter.onlineshop.domain.dto.ProductDTO;

import java.util.Set;
import java.util.stream.Collectors;

public class ProductMapping {
    public static ProductDTO convertProductToDTO(Product product){
        Set<Long> categoryIds = product
                .getCategories().stream()
                .map(Category::getId).collect(Collectors.toSet());
        return new ProductDTO(
                product.getId(),
                product.getTitle(),
                product.getDescription(),
                product.getComments(),
                product.getPrice(),
                product.getSalePrice(),
                product.getAmount(),
                categoryIds,
                product.getImages(),
                product.getCharacteristics()
        );
    }
}