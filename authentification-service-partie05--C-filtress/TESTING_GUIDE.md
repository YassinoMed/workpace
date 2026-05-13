# Guide de Test - Partie 05-A : Session Security

## Résumé des modifications

### 1. Nouveau package DTO
- **Fichier créé** : `src/main/java/org/ms/authentificationservice/dto/UserRoleData.java`
- Contient deux attributs : `username` et `roleName`

### 2. Nouveaux endpoints REST
**Fichier modifié** : `UserServiceREST.java`

Trois nouveaux endpoints ajoutés :
- `POST /users` - Ajouter un utilisateur
- `POST /roles` - Ajouter un rôle
- `POST /addRoleToUser` - Affecter un rôle à un utilisateur

### 3. Configuration de sécurité
**Fichier modifié** : `SecurityConfig.java`

- Authentification obligatoire pour toutes les URLs (sauf H2 console)
- Formulaire de login Spring Security activé
- Authentification basée sur les données de la base de données (tables AppUser et AppRole)
- Les rôles sont chargés comme permissions Spring Security

## Comment tester

### Étape 1 : Démarrer l'application
```bash
./mvnw spring-boot:run
```

### Étape 2 : Accéder à une URL protégée
Ouvrir le navigateur : `http://localhost:8089/users`

**Résultat attendu** : Redirection vers le formulaire de login Spring Security

### Étape 3 : S'authentifier
Utiliser les credentials des utilisateurs créés au démarrage :
- **user1** / **123** (rôle: USER)
- **user2** / **456** (rôles: USER, ADMIN)

**Résultat attendu** : Après authentification, redirection vers `/users` avec la liste des utilisateurs en JSON

### Étape 4 : Tester les nouveaux endpoints avec ARC ou Postman

#### A. Ajouter un utilisateur
```
POST http://localhost:8089/users
Content-Type: application/json

{
  "username": "user3",
  "password": "789"
}
```

#### B. Ajouter un rôle
```
POST http://localhost:8089/roles
Content-Type: application/json

{
  "roleName": "MANAGER"
}
```

#### C. Affecter un rôle à un utilisateur
```
POST http://localhost:8089/addRoleToUser
Content-Type: application/json

{
  "username": "user3",
  "roleName": "MANAGER"
}
```

### Étape 5 : Se déconnecter
Accéder à : `http://localhost:8089/logout`

### Étape 6 : Vérifier la base de données H2
- URL : `http://localhost:8089/h2-console`
- JDBC URL : `jdbc:h2:mem:users-db`
- Username : `sa`
- Password : (laisser vide)

**Note** : H2 console reste accessible sans authentification

## Points importants

1. **CSRF désactivé** : Permet les requêtes POST sans token CSRF (pour faciliter les tests)
2. **Mode SESSION** : L'authentification utilise les sessions HTTP (cookies JSESSIONID)
3. **Encodage des mots de passe** : BCryptPasswordEncoder utilisé automatiquement
4. **Chargement des rôles** : Les rôles sont chargés depuis la base de données et convertis en `GrantedAuthority`

## Prochaine étape
La partie suivante (Partie 05-B) implémentera JWT pour remplacer l'authentification par session.
