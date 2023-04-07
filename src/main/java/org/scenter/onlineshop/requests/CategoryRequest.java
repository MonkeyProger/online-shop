package org.scenter.onlineshop.requests;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class CategoryRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String title;

    private Long parentId;

    private List<String> products;
}
