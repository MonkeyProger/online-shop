package org.scenter.onlineshop.service.mapping;

import org.scenter.onlineshop.domain.Characteristic;
import org.scenter.onlineshop.domain.CharacteristicValue;
import org.scenter.onlineshop.dto.CharacteristicDTO;
import org.scenter.onlineshop.dto.CharacteristicValueDTO;

public class CharacteristicMapping {

    public static CharacteristicValueDTO valueToDTO(CharacteristicValue value){
        CharacteristicValueDTO dto = new CharacteristicValueDTO();
        dto.setId(value.getId());
        dto.setValue(value.getValue());
        return dto;
    }
    public static CharacteristicDTO characteristicToDTO(Characteristic characteristic){
        CharacteristicDTO dto = new CharacteristicDTO();
        dto.setId(characteristic.getId());
        dto.setName(characteristic.getName());
        dto.setValue(valueToDTO(characteristic.getValue()));
        return dto;
    }
}