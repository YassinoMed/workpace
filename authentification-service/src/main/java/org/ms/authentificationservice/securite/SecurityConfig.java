package org.ms.authentificationservice.securite;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationConfiguration authenticationConfiguration) throws Exception {

        // Désactiver CSRF
        http.csrf(AbstractHttpConfigurer::disable);

        // Mode STATELESS avec JWT
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Autoriser H2 Console
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        // Gestion des autorisations
        http.authorizeHttpRequests(auth -> auth

                // Autoriser l'accès à H2 Console
                .requestMatchers("/h2-console/**").permitAll()

                // Seul ADMIN peut ajouter un utilisateur
                .requestMatchers(HttpMethod.POST, "/users/**").hasAuthority("ADMIN")

                // Seul USER peut afficher les utilisateurs
                .requestMatchers(HttpMethod.GET, "/users/**").hasAuthority("USER")

                // Toutes les autres requêtes nécessitent une authentification
                .anyRequest().authenticated());

        // Filtre d'authentification : création du JWT
        http.addFilter(new JwtAuthenticationFilter(
                authenticationManager(authenticationConfiguration)));

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