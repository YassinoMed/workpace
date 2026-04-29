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
public class AuthentificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthentificationServiceApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	CommandLineRunner start(UserService userService) {
		return args -> {

			AppUser user1 = new AppUser();
			user1.setUsername("user1");
			user1.setPassword("123");
			userService.addUser(user1);

			AppUser user2 = new AppUser();
			user2.setUsername("user2");
			user2.setPassword("456");
			userService.addUser(user2);

			userService.addRole(new AppRole(null, "USER"));
			userService.addRole(new AppRole(null, "ADMIN"));

			userService.addRoleToUser("user1", "USER");

			userService.addRoleToUser("user2", "USER");
			userService.addRoleToUser("user2", "ADMIN");

			AppUser yassine = new AppUser();
			yassine.setUsername("yassine");
			yassine.setPassword("yassine123");
			userService.addUser(yassine);

			userService.addRoleToUser("yassine", "USER");
			userService.addRoleToUser("yassine", "ADMIN");
		};
	}
}