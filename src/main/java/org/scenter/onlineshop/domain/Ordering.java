package org.scenter.onlineshop.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.AUTO;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ordering {
    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;
    @ManyToOne
    private AppUser user;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE, targetEntity = SaleProduct.class)
    private List<SaleProduct> cart = new ArrayList<>();
    private Float total;
}
