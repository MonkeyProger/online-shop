package org.scenter.onlineshop.requests;

import lombok.Data;
import org.scenter.onlineshop.domain.SaleProduct;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class PlaceOrderRequest {
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    private Set<SaleProduct> order;
}
