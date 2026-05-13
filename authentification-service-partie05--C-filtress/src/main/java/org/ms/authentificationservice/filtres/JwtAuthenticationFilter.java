package org.ms.authentificationservice.filtres;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Date;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;

    // injecter par constructeur un gestionnaire d'authentication
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    //appelée suite à l'appel du path "/login"
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        System.out.println("attemptAuthentication");
        //récupérer les paramètres "username" et "password" du path "/login"
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        //Construire un objet "Authentification"
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        //lancer la procédure d'authentification
        // (appeler la méthode "loadUserByUsername" de "UserDetailsService"
        return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
    }

    //appelée en cas de l'existence de l'utilisateur dans la BD pour créer le JWT
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response, FilterChain chain, Authentication authResult) throws
            IOException, ServletException {
        System.out.println("successfulAuthentication");

        //retourner l'utilisateur authentifié
        User user = (User) authResult.getPrincipal();
        //récupérer la liste des rôles
        String[] roles = new String[user.getAuthorities().size()];
        int index = 0;
        for (GrantedAuthority gi : user.getAuthorities()) {
            roles[index] = gi.toString();
            index++;
        }

        //Choisir un algorithme de cryptage
        Algorithm algo = Algorithm.HMAC256("MaClé");
        //Construire le JWT
        String jwtAccessToken = JWT.create()
                .withSubject(user.getUsername()) // stocker le nom de l'utilisateur
                .withExpiresAt(new Date(System.currentTimeMillis() + 5 * 60 * 1000))
                // date d'expiration après 5 minutes
                // Pour tester l'expiration, changer en : 1 * 60 * 1000 (1 minute)
                .withIssuer(request.getRequestURL().toString())
                //url de la reuête d'origine
                //placer la liste des rôles associés à l'utilisateur courant
                .withArrayClaim("roles", roles)
                .sign(algo); //signer le JWT avec l'algorithme choisi
        //Envoyer le JWT dans l'entête de la réponse
        response.setHeader("Authorization", jwtAccessToken);
        
        System.out.println("JWT Token généré pour l'utilisateur: " + user.getUsername());
        System.out.println("Token: " + jwtAccessToken);
    }
}
