package org.scenter.onlineshop.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

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
    @NaturalId
    private Long productId;
    @NaturalId
    private Integer amount;

    public SaleProduct(Long productId, Integer amount){
        this.productId = productId;
        this.amount = amount;
    }
}
