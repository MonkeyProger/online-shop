package org.scenter.onlineshop.service;

import org.scenter.onlineshop.domain.AppUser;
import org.scenter.onlineshop.domain.Category;
import org.scenter.onlineshop.domain.Product;
import org.scenter.onlineshop.domain.Role;

import java.util.List;

/*
    TODO Определить все возможные связи компонент
    TODO Делить/Не делить на несколько сервисов
*/

public interface UserService {
    AppUser saveUser(AppUser user);
    Role saveRole(Role role);
    Product saveProduct(Product product);
    Category saveCategory(Category category);
    void addRoleToUser(String email, String roleName);
    AppUser getUser(String email);
    List<AppUser> getUsers();
}
