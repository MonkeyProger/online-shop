package org.scenter.onlineshop.common.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.scenter.onlineshop.domain.SaleProduct;

import java.util.Set;

@Data
@AllArgsConstructor
public class OrderResponse {
    Set<SaleProduct> cart;
    Float amount;
    String date;
    Long id;
}
