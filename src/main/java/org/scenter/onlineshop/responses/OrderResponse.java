package org.scenter.onlineshop.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderResponse {
    Float amount;
    String date;
    Long id;
}
