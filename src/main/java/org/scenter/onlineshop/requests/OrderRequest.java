package org.scenter.onlineshop.requests;

import lombok.Data;
import org.scenter.onlineshop.domain.SaleProduct;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class OrderRequest {
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    private List<SaleProduct> order;
}
