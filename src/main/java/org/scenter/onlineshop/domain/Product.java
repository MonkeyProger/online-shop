package org.scenter.onlineshop.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.AUTO;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;

    private String name;

    private String title;

    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Comment> comments = new ArrayList<>();

    private Float price;

    private Float salePrice;

    private Integer amount;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<ProductFile> images = new ArrayList<>();

    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Characteristic> characteristics = new ArrayList<>();

}
