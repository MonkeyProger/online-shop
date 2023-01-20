package org.scenter.onlineshop.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.GenerationType.AUTO;
import static javax.persistence.FetchType.EAGER;

@Entity
@Data
@NoArgsConstructor @AllArgsConstructor
public class AppUser {
    @Id @GeneratedValue(strategy = AUTO)
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String password;
    @ManyToMany(fetch = EAGER)
    private Set<Role> roles = new HashSet<>();

}
