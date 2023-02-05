package org.scenter.onlineshop;


import org.scenter.onlineshop.domain.AppUser;
import org.scenter.onlineshop.domain.Product;
import org.scenter.onlineshop.domain.Role;
import org.scenter.onlineshop.services.OrderService;
import org.scenter.onlineshop.services.UserDetailsServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

import static org.scenter.onlineshop.domain.ERole.*;

/*
	Точка входа
	Инициализация ролей, пользователя со всеми правами доступа
*/

@SpringBootApplication
public class OnlineShopApplication {
	public static void main(String[] args) {
		SpringApplication.run(OnlineShopApplication.class, args);
	}

	@Bean
	CommandLineRunner run(UserDetailsServiceImpl userDetailsService, OrderService orderService) {
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		return args -> {
			Set<Role> allRoles = new HashSet<>();
			allRoles.add(new Role(null, ROLE_USER));
			allRoles.add(new Role(null, ROLE_MODERATOR));
			allRoles.add(new Role(null, ROLE_ADMIN));
			allRoles.forEach(userDetailsService::saveRole);

			AppUser admin = new AppUser("admin","admin","admin@admin.admin",encoder.encode("admin"));
			admin.setRoles(allRoles);
			userDetailsService.saveUser(admin);

			Product iphone = new Product(null, "Iphone", 100.5f, 10);
			Product rareIphone = new Product(null, "Rare Iphone", 9999.5f, 2);
			orderService.saveProduct(iphone); orderService.saveProduct(rareIphone);
		};
	}
}
