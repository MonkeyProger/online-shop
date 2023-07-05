package org.scenter.onlineshop.common.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PlaceCharactOnProductRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String value;

}
