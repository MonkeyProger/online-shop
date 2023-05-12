package org.scenter.onlineshop.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.scenter.onlineshop.domain.Characteristic;
import org.scenter.onlineshop.domain.Comment;
import org.scenter.onlineshop.domain.ProductFile;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String title;
    private String description;
    private List<Comment> comments;
    private Float price;
    private Float salePrice;
    private Integer amount;
    private Set<Long> categoryId;
    private List<ProductFile> images;
    private Set<Characteristic> characteristics;
}
