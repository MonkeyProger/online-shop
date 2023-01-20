package org.scenter.onlineshop;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
}
