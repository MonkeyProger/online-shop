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

    //@ManyToOne
    //private AppUser user;

    private String userEmail;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<SaleProduct> cart = new ArrayList<>();
    private Float total;
}
