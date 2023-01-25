package org.scenter.onlineshop;


import org.scenter.onlineshop.domain.AppUser;
import org.scenter.onlineshop.domain.Role;
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
	User ( Имя , Фамилия , Email ,  пароль ) | Email , пароль нужны для аутентификации
	Roles ( Название ) : ADMIN , USER ...
	Product ( Название , цена , наличие/остатки )
	Category ( Название )

*/

@SpringBootApplication
public class OnlineShopApplication {
	public static void main(String[] args) {
		SpringApplication.run(OnlineShopApplication.class, args);
	}

	@Bean
	CommandLineRunner run(UserDetailsServiceImpl userDetailsService) {
		return args -> {
			Set<Role> allRoles = new HashSet<>();
			allRoles.add(new Role(null, ROLE_USER));
			allRoles.add(new Role(null, ROLE_MODERATOR));
			allRoles.add(new Role(null, ROLE_ADMIN));
			allRoles.forEach(userDetailsService::saveRole);

			AppUser admin = new AppUser("admin","admin","admin@admin.admin","admin");
			admin.setRoles(allRoles);
			userDetailsService.saveUser(admin);
		};
	}
}
