package org.ms.authentificationservice.secure;

import org.ms.authentificationservice.entities.AppUser;
import org.ms.authentificationservice.filtres.JwtAuthenticationFilter;
import org.ms.authentificationservice.filtres.JwtAuthorizationFilter;
import org.ms.authentificationservice.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.Collection;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void globalConfig(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(new UserDetailsService() {

            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

                AppUser appUser = userService.getUserByName(username);

                if (appUser == null) {
                    throw new UsernameNotFoundException("Utilisateur introuvable : " + username);
                }

                Collection<GrantedAuthority> permissions = new ArrayList<>();

                appUser.getRoles().forEach(role -> {
                    permissions.add(new SimpleGrantedAuthority(role.getRoleName()));
                });

                return new User(
                        appUser.getUsername(),
                        appUser.getPassword(),
                        permissions);
            }
        }).passwordEncoder(passwordEncoder);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Désactiver CSRF
        http.csrf(csrf -> csrf.disable());

        // Mode STATELESS avec JWT
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Autoriser les frames pour H2 Console
        http.headers(headers -> headers
                .frameOptions(frame -> frame.disable()));

        // Règles d'autorisation
        http.authorizeHttpRequests(auth -> auth

                // H2 Console accessible sans authentification
                .requestMatchers("/h2-console/**").permitAll()

                // Login accessible pour créer le JWT
                .requestMatchers(HttpMethod.POST, "/login").permitAll()

                // Seul ADMIN peut ajouter un utilisateur
                .requestMatchers(HttpMethod.POST, "/users", "/users/**")
                .hasAuthority("ADMIN")

                // Seul USER peut afficher les utilisateurs
                .requestMatchers(HttpMethod.GET, "/users", "/users/**")
                .hasAuthority("USER")

                // Toute autre requête nécessite une authentification
                .anyRequest().authenticated());

        // Filtre d'authentification : création du JWT
        http.addFilter(new JwtAuthenticationFilter(
                authenticationManager(http.getSharedObject(AuthenticationConfiguration.class))));

        // Filtre d'autorisation : vérification du JWT
        http.addFilterBefore(
                new JwtAuthorizationFilter(),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}