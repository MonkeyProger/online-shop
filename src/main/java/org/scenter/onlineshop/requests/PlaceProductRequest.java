package org.scenter.onlineshop.requests;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PlaceProductRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String title;

    private Boolean saveComments;

    @NotNull
    private Float price;

    @NotNull
    private Float salePrice;

    @NotNull
    private Integer amount;
}
