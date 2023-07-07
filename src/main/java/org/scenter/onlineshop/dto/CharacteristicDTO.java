package org.scenter.onlineshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CharacteristicDTO {

    private Long id;

    private String name;

    private List<CharacteristicValueDTO> values;
}
