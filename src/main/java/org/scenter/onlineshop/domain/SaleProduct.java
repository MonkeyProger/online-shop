package org.scenter.onlineshop.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.AUTO;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleProduct{
    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;

    private Long productId;

    private Integer amount;

}
