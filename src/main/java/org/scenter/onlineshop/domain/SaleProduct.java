package org.scenter.onlineshop.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleProduct {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private Long productId;
    private Integer amount;
}
