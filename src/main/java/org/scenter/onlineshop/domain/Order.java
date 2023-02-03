package org.scenter.onlineshop.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.List;

import static javax.persistence.GenerationType.AUTO;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;
    @OneToOne(cascade = CascadeType.MERGE)
    private AppUser user;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<SaleProduct> order;
}
