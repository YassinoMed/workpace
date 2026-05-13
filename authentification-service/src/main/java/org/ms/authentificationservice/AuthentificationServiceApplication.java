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

import java.util.ArrayList;

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
            userService.addRole(new AppRole(null, "USER"));
            userService.addRole(new AppRole(null, "ADMIN"));

            userService.addUser(new AppUser(null, "user1", "123", new ArrayList<>()));
            userService.addUser(new AppUser(null, "user2", "456", new ArrayList<>()));

            userService.addRoleToUser("user1", "USER");

            userService.addRoleToUser("user2", "USER");
            userService.addRoleToUser("user2", "ADMIN");
        };
    }
}
