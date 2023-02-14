package org.scenter.onlineshop;


import org.scenter.onlineshop.domain.AppUser;
import org.scenter.onlineshop.domain.Category;
import org.scenter.onlineshop.domain.Product;
import org.scenter.onlineshop.domain.Role;
import org.scenter.onlineshop.services.StockService;
import org.scenter.onlineshop.services.UserDetailsServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.scenter.onlineshop.domain.ERole.*;

@SpringBootApplication
public class OnlineShopApplication {
	public static void main(String[] args) {
		SpringApplication.run(OnlineShopApplication.class, args);
	}

	@Bean
	CommandLineRunner run(UserDetailsServiceImpl userDetailsService, StockService stockService) {
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

			stockService.saveProduct(new Product(null, "Iphone14","Смартфон Apple IPhone 14",null, 50.50f, 100));
			stockService.saveProduct(new Product(null, "Iphone13","Смартфон Apple IPhone 13",null, 25.25f, 4));
			stockService.saveProduct(new Product(null,"SamsungGS8","Смартфон Samsung Galaxy S8",null,25f,200));
			stockService.saveProduct(new Product(null,"HuaweiP50","Смартфон Huawei P50",null,23f,200));
			stockService.saveProduct(new Product(null,"AppleAirPodsPro","Наушники Apple AirPods Pro",null,15f,200));
			stockService.saveProduct(new Product(null,"CaseHuaweiP50","Наушники Apple AirPods Pro",null,15f,200));
			stockService.saveProduct(new Product(null,"YandexStation","Умная колонка Яндекс Станция",null,20f,200));

			stockService.saveCategory(new Category(null,"Smartphones","Смартфоны",null,new ArrayList<>()));
			stockService.saveCategory(new Category(null,"Apple","Apple", null,new ArrayList<>()));
			stockService.saveCategory(new Category(null,"Samsung","Samsung",null,new ArrayList<>()));
			stockService.saveCategory(new Category(null,"AudioEquipment","Аудиотехника",null,new ArrayList<>()));
			stockService.saveCategory(new Category(null,"Headphones","Наушники",null,new ArrayList<>()));
			stockService.saveCategory(new Category(null,"PortableSpeakers","Портативные колонки",null,new ArrayList<>()));

			stockService.saveParentToCategory("Apple","Smartphones");
			stockService.saveParentToCategory("Samsung","Smartphones");
			stockService.saveParentToCategory("Headphones","AudioEquipment");
			stockService.saveParentToCategory("PortableSpeakers","AudioEquipment");

			stockService.saveProductToCategory("Iphone14", "Apple");
			stockService.saveProductToCategory("Iphone13", "Apple");
			stockService.saveProductToCategory("HuaweiP50", "Smartphones");
			stockService.saveProductToCategory("SamsungGS8", "Samsung");
			stockService.saveProductToCategory("AppleAirPodsPro", "Headphones");
			stockService.saveProductToCategory("YandexStation", "PortableSpeakers");
		};
	}
}
