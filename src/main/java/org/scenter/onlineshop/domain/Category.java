package org.scenter.onlineshop.domain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

import static javax.persistence.GenerationType.AUTO;
import static javax.persistence.FetchType.EAGER;

/*
    TODO Определить отношение у categories&products
*/

@Entity
@Data
@NoArgsConstructor @AllArgsConstructor
public class Category {
    @Id @GeneratedValue(strategy = AUTO)
    private Long id;
    private String name;
    @OneToMany(fetch = EAGER)
    private Collection<Category> categories = new ArrayList<>();
    @OneToMany(fetch = EAGER)
    private Collection<Product> products = new ArrayList<>();

}
