package org.ms.authentificationservice;

import org.ms.authentificationservice.entities.AppRole;
import org.ms.authentificationservice.entities.AppUser;
import org.ms.authentificationservice.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class AuthentificationserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthentificationserviceApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public CommandLineRunner initData(UserService userService) {
		return args -> {
			// Ajouter les rôles
			AppRole roleUser = userService.addRole(new AppRole(null, "USER"));
			AppRole roleAdmin = userService.addRole(new AppRole(null, "ADMIN"));

			// Ajouter les utilisateurs
			AppUser user1 = new AppUser();
			user1.setUsername("user1");
			user1.setPassword("123"); // sera encodé dans addUser
			userService.addUser(user1);

			AppUser user2 = new AppUser();
			user2.setUsername("user2");
			user2.setPassword("456");
			userService.addUser(user2);

			// Associer les rôles
			userService.addRoleToUser("user1", "USER");
			userService.addRoleToUser("user2", "USER");
			userService.addRoleToUser("user2", "ADMIN");

			System.out.println("Données initialisées");
		};
	}
}