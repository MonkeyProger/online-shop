package org.scenter.onlineshop.requests;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CharacteristicRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String value;
}
