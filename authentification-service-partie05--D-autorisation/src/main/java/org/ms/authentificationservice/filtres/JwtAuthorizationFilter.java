package org.ms.authentificationservice.filtres;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    public static final String PREFIXE_JWT = "Bearer ";
    public static final String CLE_SIGNATURE = "MaClé";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return path.equals("/login")
                || path.startsWith("/h2-console")
                || path.startsWith("/actuator");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authorizationToken = request.getHeader("Authorization");

        System.out.println("=== JwtAuthorizationFilter ===");
        System.out.println("Path: " + request.getRequestURI());
        System.out.println("Authorization Header: " + authorizationToken);

        if (authorizationToken != null && authorizationToken.startsWith(PREFIXE_JWT)) {
            try {
                String jwt = authorizationToken.substring(PREFIXE_JWT.length());

                Algorithm algorithm = Algorithm.HMAC256(CLE_SIGNATURE);

                JWTVerifier jwtVerifier = JWT.require(algorithm).build();

                DecodedJWT decodedJWT = jwtVerifier.verify(jwt);

                String username = decodedJWT.getSubject();

                String[] roles = decodedJWT.getClaim("roles").asArray(String.class);

                Collection<GrantedAuthority> permissions = new ArrayList<>();

                if (roles != null) {
                    for (String role : roles) {
                        permissions.add(new SimpleGrantedAuthority(role));
                    }
                }

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        permissions);

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                System.out.println("Utilisateur authentifié : " + username);
                System.out.println("Permissions : " + permissions);

                filterChain.doFilter(request, response);

            } catch (Exception e) {
                System.out.println("Erreur de validation JWT : " + e.getMessage());

                SecurityContextHolder.clearContext();

                response.setHeader("error-message", e.getMessage());
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            }

        } else {
            System.out.println("Aucun token JWT trouvé.");
            filterChain.doFilter(request, response);
        }
    }
}