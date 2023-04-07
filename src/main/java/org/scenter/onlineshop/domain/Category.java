package org.scenter.onlineshop.domain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.AUTO;
import static javax.persistence.FetchType.EAGER;

@Entity
@Data
@NoArgsConstructor @AllArgsConstructor
public class Category {

    @Id @GeneratedValue(strategy = AUTO)
    private Long id;

    private String name;

    private String title;

    private Long parentId;

    @OneToMany(fetch = EAGER)
    private List<Product> products = new ArrayList<>();

}
