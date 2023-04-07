package org.scenter.onlineshop.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.GenerationType.AUTO;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ordering {
    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;

    private String userEmail;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<SaleProduct> cart = new HashSet<>();

    private Float total;

    private boolean active;
}
