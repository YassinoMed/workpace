package org.ms.authentificationservice.web;

import org.ms.authentificationservice.dto.UserRoleData;
import org.ms.authentificationservice.entities.AppRole;
import org.ms.authentificationservice.entities.AppUser;
import org.ms.authentificationservice.services.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserServiceREST {

    private final UserService userService;

    public UserServiceREST(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<AppUser> getAllUsers() {
        return userService.getAllUsers();
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
    public void addRoleToUser(@RequestBody UserRoleData userRoleData) {
        userService.addRoleToUser(userRoleData.getUsername(), userRoleData.getRoleName());
    }
}