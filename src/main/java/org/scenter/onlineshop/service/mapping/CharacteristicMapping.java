package org.scenter.onlineshop.service.mapping;

import org.scenter.onlineshop.domain.Characteristic;
import org.scenter.onlineshop.domain.CharacteristicValue;
import org.scenter.onlineshop.dto.CharacteristicDTO;
import org.scenter.onlineshop.dto.CharacteristicValueDTO;

import java.util.List;
import java.util.stream.Collectors;

public class CharacteristicMapping {

    public static CharacteristicValueDTO valueToDTO(CharacteristicValue value) {
        CharacteristicValueDTO dto = new CharacteristicValueDTO();
        dto.setId(value.getId());
        dto.setValue(value.getValue());
        dto.setCharactName(value.getCharacteristic().getName());
        return dto;
    }

    public static CharacteristicDTO characteristicToDTO(Characteristic characteristic) {
        CharacteristicDTO dto = new CharacteristicDTO();
        dto.setId(characteristic.getId());
        dto.setName(characteristic.getName());
        List<CharacteristicValueDTO> valueDTOs = characteristic.getValues().stream()
                .map(CharacteristicMapping::valueToDTO)
                .collect(Collectors.toList());
        dto.setValues(valueDTOs);
        return dto;
    }
}