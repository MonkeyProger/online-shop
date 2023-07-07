package org.scenter.onlineshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scenter.onlineshop.domain.ProductFile;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long id;
    private String title;
    private String description;
    private List<CommentDTO> comments;
    private Float price;
    private Float salePrice;
    private Integer amount;
    private Set<Long> categoryId;
    private List<ProductFile> images;
    private Set<CharacteristicValueDTO> characteristics;
}
