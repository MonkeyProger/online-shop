package org.scenter.onlineshop.responses;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderResponse {
    Float amount;
    String date;
}
