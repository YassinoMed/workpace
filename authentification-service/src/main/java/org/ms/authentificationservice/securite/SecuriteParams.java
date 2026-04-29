package org.ms.authentificationservice.securite;

public interface SecuriteParams {
    String SECRET = "monSecretTresSecret123";
    long EXPIRATION = 86400000; // 24 heures en millisecondes
    String HEADER_PREFIX = "Bearer ";
    String AUTH_HEADER = "Authorization";
}
