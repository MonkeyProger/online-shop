package org.scenter.onlineshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scenter.onlineshop.domain.AppUser;
import org.scenter.onlineshop.domain.Category;
import org.scenter.onlineshop.domain.Product;
import org.scenter.onlineshop.domain.Role;
import org.scenter.onlineshop.repo.CategoryRepo;
import org.scenter.onlineshop.repo.ProductRepo;
import org.scenter.onlineshop.repo.RoleRepo;
import org.scenter.onlineshop.repo.UserRepo;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = userRepo.findByEmail(email);
        if (user == null) {
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        } else {
            log.info("User found in the database: {}",email);
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        });
        return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPassword(),authorities);
    }
    @Override
    public AppUser saveUser(AppUser user) {
        log.info("Saving new user {} to the userRepo",user.getName());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    @Override
    public Role saveRole(Role role) {
        log.info("Saving new role {} to the roleRepo",role.getName());
        return roleRepo.save(role);
    }

    @Override
    public Product saveProduct(Product product) {
        log.info("Saving new product {} to the productRepo",product.getName());
        return productRepo.save(product);
    }

    @Override
    public Category saveCategory(Category category) {
        log.info("Saving new category {} to the categoryRepo",category.getName());
        return categoryRepo.save(category);
    }

    @Override
    public void addRoleToUser(String email, String roleName) {
        log.info("Adding role {} to user {}",roleName, email);
        AppUser user = userRepo.findByEmail(email);
        Role role = roleRepo.findByName(roleName);
        user.getRoles().add(role);
    }

    @Override
    public AppUser getUser(String email) {
        log.info("Fetching user {}",email);
        return userRepo.findByEmail(email);
    }

    @Override
    public List<AppUser> getUsers() {
        log.info("Fetching all users");
        return userRepo.findAll();
    }
}
