package org.scenter.onlineshop.common.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class CharacteristicRequest {

    @NotBlank
    private String name;

    @NotNull
    @NotEmpty
    private List<String> values;
}
