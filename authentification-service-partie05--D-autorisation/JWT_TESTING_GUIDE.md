# Guide de Test JWT - Authentication Service

## Implémentation Complétée

Les filtres JWT ont été implémentés avec succès :

### 1. Dépendance ajoutée
- **java-jwt** (version 3.8.1) de Auth0 ajoutée au `pom.xml`

### 2. Filtres créés

#### JwtAuthenticationFilter
- **Package**: `org.ms.authentificationservice.filtres`
- **Fonction**: Créer le JWT après authentification réussie
- **Méthodes**:
  - `attemptAuthentication()`: Récupère username/password et lance l'authentification
  - `successfulAuthentication()`: Génère le JWT et l'envoie dans le header "Authorization"

#### JwtAuthorizationFilter
- **Package**: `org.ms.authentificationservice.filtres`
- **Fonction**: Vérifier le JWT avant d'accéder aux ressources
- **Méthode**:
  - `doFilterInternal()`: Vérifie le JWT, extrait les données et authentifie l'utilisateur

### 3. SecurityConfig mis à jour
- Ajout du filtre d'authentification JWT
- Ajout du filtre d'autorisation JWT (exécuté en premier)
- Bean `authenticationManager` exposé

## Configuration JWT

- **Algorithme**: HMAC256
- **Clé de signature**: "MaClé"
- **Durée de validité**: 5 minutes (300 000 ms)
- **Préfixe du token**: "Bearer "

## Tests à Effectuer

### Test 1: Authentification et Génération du JWT

**URL**: `http://localhost:8089/login`  
**Méthode**: POST  
**Headers**:
```
Content-Type: application/x-www-form-urlencoded
```

**Body** (format: application/x-www-form-urlencoded):
```
username=user1
password=123
```

**Résultat attendu**:
- Status: 200 OK
- Header de réponse "Authorization" contenant le JWT

### Test 2: Décoder le JWT

1. Copier la valeur du JWT depuis le header "Authorization"
2. Aller sur https://jwt.io
3. Coller le JWT dans le champ "Encoded"
4. Vérifier le contenu décodé:
   - **sub**: "user1"
   - **roles**: ["USER"] ou ["ADMIN"]
   - **iss**: "http://localhost:8089/login"
   - **exp**: timestamp d'expiration

### Test 3: Accès aux Ressources avec JWT

**URL**: `http://localhost:8089/users`  
**Méthode**: GET  
**Headers**:
```
Authorization: Bearer <VOTRE_JWT_ICI>
```

**Résultat attendu**:
- Status: 200 OK
- Liste des utilisateurs en JSON

### Test 4: Accès sans JWT (doit échouer)

**URL**: `http://localhost:8089/users`  
**Méthode**: GET  
**Headers**: Aucun

**Résultat attendu**:
- Status: 403 Forbidden

### Test 5: Tester avec user2

Répéter le Test 1 avec:
```
username=user2
password=123
```

Vérifier que le JWT généré contient les rôles appropriés pour user2.

### Test 6: Expiration du JWT

1. Dans `JwtAuthenticationFilter.java`, modifier la durée d'expiration:
   ```java
   .withExpiresAt(new Date(System.currentTimeMillis() + 1 * 60 * 1000))
   // 1 minute au lieu de 5
   ```
2. Redémarrer l'application
3. S'authentifier et obtenir un JWT
4. Attendre 1 minute
5. Essayer d'accéder à `/users` avec le JWT expiré

**Résultat attendu**:
- Status: 403 Forbidden
- Header "error-message" avec le message d'erreur

## Commandes Utiles

### Démarrer l'application
```bash
./mvnw spring-boot:run
```

### Compiler le projet
```bash
./mvnw clean compile
```

### Construire le JAR
```bash
./mvnw clean package
```

## Outils de Test Recommandés

1. **ARC (Advanced REST Client)** - Mentionné dans le document
2. **Postman** - Alternative populaire
3. **curl** - Ligne de commande
4. **Thunder Client** - Extension VS Code

## Exemple avec curl

### Authentification
```bash
curl -X POST http://localhost:8089/login \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=user1&password=123" \
  -i
```

### Accès aux ressources
```bash
curl -X GET http://localhost:8089/users \
  -H "Authorization: Bearer <VOTRE_JWT>"
```

## Points Importants

1. **Mode STATELESS**: Aucune session n'est créée côté serveur
2. **H2 Console**: Reste accessible sans authentification (`/h2-console/**`)
3. **Sécurité**: La clé "MaClé" devrait être externalisée dans un fichier de configuration en production
4. **Expiration**: Le JWT expire après 5 minutes par défaut
5. **Préfixe Bearer**: Le JWT doit toujours être précédé de "Bearer " dans le header Authorization

## Prochaines Étapes (Optionnelles)

1. Externaliser la clé de signature dans `application.properties`
2. Ajouter un refresh token pour renouveler le JWT
3. Implémenter la révocation des JWT
4. Ajouter des endpoints pour gérer les rôles et permissions
5. Créer des tests unitaires pour les filtres
