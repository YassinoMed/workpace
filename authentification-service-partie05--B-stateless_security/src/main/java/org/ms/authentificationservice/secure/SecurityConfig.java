package org.ms.authentificationservice.secure;

import org.ms.authentificationservice.entities.AppUser;
import org.ms.authentificationservice.services.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
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
import org.springframework.beans.factory.annotation.Autowired;

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

    /**
     * Configure Spring Security to load users from the database.
     * Called automatically by Spring Security to set up the AuthenticationManager.
     */
    @Autowired
    public void globalConfig(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(new UserDetailsService() {
            @Override
            // Cette méthode est appelée suite à la validation du formulaire d'authentification
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                // Récupérer l'utilisateur depuis la BD
                AppUser appUser = userService.getUserByName(username);
                if (appUser == null) {
                    throw new UsernameNotFoundException("Utilisateur introuvable : " + username);
                }
                // Construire la collection des permissions selon le format de Spring Security
                Collection<GrantedAuthority> permissions = new ArrayList<>();
                appUser.getRoles().forEach(r -> {
                    permissions.add(new SimpleGrantedAuthority(r.getRoleName()));
                });
                // Retourner un objet User de Spring Security
                return new User(appUser.getUsername(), appUser.getPassword(), permissions);
            }
        }).passwordEncoder(passwordEncoder);
    }

    /**
     * Définir les règles d'accès aux ressources en mode STATELESS.
     * - Désactiver CSRF pour utiliser les tokens
     * - Configurer la session en mode STATELESS (pas de SESSION_ID)
     * - H2 console reste accessible sans authentification
     * - Toutes les autres URLs nécessitent une authentification
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Désactiver la protection CSRF pour utiliser désormais les tokens
        http.csrf(csrf -> csrf.disable());

        // Configurer la sécurité au mode STATELESS
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // Permettre l'affichage des frames (pour H2 console)
        http.headers(headers -> headers
                .frameOptions(frame -> frame.disable())
        );

        // Désactiver l'affichage du formulaire d'authentification
        // http.formLogin();

        // Laisser uniquement l'accès à la console de la base de données
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated()
        );

        return http.build();
    }
}
