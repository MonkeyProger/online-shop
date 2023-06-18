package org.scenter.onlineshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CharacteristicDTO {

    private Long id;

    private String name;

    private CharacteristicValueDTO value;
}
