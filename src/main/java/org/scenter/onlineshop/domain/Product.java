package org.scenter.onlineshop.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.*;

import static javax.persistence.GenerationType.AUTO;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;

    private String title;

    private String description;

    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Comment> comments = new ArrayList<>();

    private Float price;

    private Float salePrice;

    private Integer amount;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<Category> categories;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<ProductFile> images = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.REMOVE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<CharacteristicValue> characteristicValues = new HashSet<>();
}
