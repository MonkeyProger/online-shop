package org.scenter.onlineshop.service.mapping;

import org.scenter.onlineshop.domain.Category;
import org.scenter.onlineshop.domain.Product;
import org.scenter.onlineshop.dto.CharacteristicDTO;
import org.scenter.onlineshop.dto.CommentDTO;
import org.scenter.onlineshop.dto.ProductDTO;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProductMapping {
    public static ProductDTO convertProductToDTO(Product product){
        Set<Long> categoryIds = product
                .getCategories().stream()
                .map(Category::getId).collect(Collectors.toSet());
        Set<CharacteristicDTO> characteristicDTOS = product
                .getCharacteristics().stream()
                .map(CharacteristicMapping::characteristicToDTO).collect(Collectors.toSet());
        List<CommentDTO> commentsList = product
                .getComments().stream()
                .map(CommentMapping::commentToDTO).collect(Collectors.toList());
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setTitle(product.getTitle());
        dto.setDescription(product.getDescription());
        dto.setComments(commentsList);
        dto.setPrice(product.getPrice());
        dto.setSalePrice(product.getSalePrice());
        dto.setAmount(product.getAmount());
        dto.setCategoryId(categoryIds);
        dto.setImages(product.getImages());
        dto.setCharacteristics(characteristicDTOS);
        return dto;
    }

    public static List<ProductDTO> listToDTO(List<Product> products){
        return products.stream().map(ProductMapping::convertProductToDTO)
                .collect(Collectors.toList());
    }
}