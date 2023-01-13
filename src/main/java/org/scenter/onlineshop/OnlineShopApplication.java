package org.scenter.onlineshop;

import org.scenter.onlineshop.domain.AppUser;
import org.scenter.onlineshop.domain.Role;
import org.scenter.onlineshop.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

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
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	CommandLineRunner run(UserService userService) {
		return args -> {
			userService.saveRole(new Role(null,"ROLE_USER"));
			userService.saveRole(new Role(null,"ROLE_ADMIN"));

			userService.saveUser(new AppUser(null, "John",
					"John", "john@gmail.com","123",new ArrayList<>()));
			userService.saveUser(new AppUser(null, "Damian",
					"Damian", "damian@gmail.com","123",new ArrayList<>()));
			userService.saveUser(new AppUser(null, "Ivan",
					"Ivan", "ivan@gmail.com","123",new ArrayList<>()));

			userService.addRoleToUser("Johny","ROLE_USER");
			userService.addRoleToUser("vanya123","ROLE_ADMIN");
			userService.addRoleToUser("vanya123","ROLE_USER");
		};
	}
}
