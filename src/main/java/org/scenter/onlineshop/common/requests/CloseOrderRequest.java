package org.scenter.onlineshop.common.requests;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CloseOrderRequest {
    @NotNull
    private Long orderId;
}
