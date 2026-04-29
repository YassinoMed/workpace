package org.ms.authentificationservice.web;

import lombok.RequiredArgsConstructor;
import org.ms.authentificationservice.entities.AppUser;
import org.ms.authentificationservice.services.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserServiceREST {

    private final UserService userService;

    @GetMapping("/users")
    public List<AppUser> users() {
        return userService.getAllUsers();
    }

    @GetMapping("/profile")
    public String profile(java.security.Principal principal) {
        return "Bienvenue " + principal.getName();
    }
}