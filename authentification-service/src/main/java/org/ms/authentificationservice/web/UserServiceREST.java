package org.ms.authentificationservice.web;

import lombok.RequiredArgsConstructor;
import org.ms.authentificationservice.entities.AppRole;
import org.ms.authentificationservice.entities.AppUser;
import org.ms.authentificationservice.services.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserServiceREST {

    private final UserService userService;

    @GetMapping("/users")
    public List<AppUser> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{username}")
    public AppUser getUserByUsername(@PathVariable String username) {
        return userService.getUserByName(username);
    }

    @PostMapping("/users")
    public AppUser addUser(@RequestBody AppUser appUser) {
        return userService.addUser(appUser);
    }

    @PostMapping("/roles")
    public AppRole addRole(@RequestBody AppRole appRole) {
        return userService.addRole(appRole);
    }

    @PostMapping("/addRoleToUser")
    public void addRoleToUser(@RequestParam String username,
                              @RequestParam String roleName) {
        userService.addRoleToUser(username, roleName);
    }
}
