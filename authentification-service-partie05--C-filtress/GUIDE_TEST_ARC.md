# 🎯 Guide de Test avec ARC - Étape par Étape

## ⚠️ PROBLÈME IDENTIFIÉ

Dans votre capture d'écran, le header Authorization est :
```
Bearer eyJ0eXAiOiJKV1Qi...
```

**C'EST CORRECT !** Mais vous obtenez 403, ce qui signifie probablement que :
1. Le token a expiré (5 minutes)
2. Le token n'est pas valide
3. Il y a un problème avec le format

## 📋 ÉTAPES DÉTAILLÉES

### Étape 1 : S'authentifier et obtenir un JWT

1. **Créer une nouvelle requête dans ARC**
   - Méthode : `POST`
   - URL : `http://localhost:8089/login`

2. **Onglet HEADERS**
   - Cliquer sur "ADD"
   - Name : `Content-Type`
   - Value : `application/x-www-form-urlencoded`

3. **Onglet BODY**
   - Sélectionner le format : `application/x-www-form-urlencoded`
   - Ajouter les paramètres :
     - Name : `username` | Value : `user1`
     - Name : `password` | Value : `123`

4. **Envoyer la requête**
   - Cliquer sur le bouton bleu "Send" (flèche)

5. **Vérifier la réponse**
   - Status : Doit être `200 OK`
   - Cliquer sur l'onglet "Response" puis "Headers"
   - Chercher le header `Authorization`
   - **COPIER IMMÉDIATEMENT LA VALEUR COMPLÈTE DU JWT**

### Étape 2 : Accéder à la ressource protégée

1. **Créer une NOUVELLE requête dans ARC**
   - Méthode : `GET`
   - URL : `http://localhost:8089/users`

2. **Onglet HEADERS**
   - Cliquer sur "ADD"
   - Name : `Authorization`
   - Value : `Bearer ` + VOTRE_JWT
   
   **ATTENTION : Format exact**
   ```
   Bearer eyJ0eXAiOiJKV1QiLCJhbGc...
   ```
   - Le mot "Bearer"
   - UN ESPACE
   - Le token JWT complet

3. **Envoyer la requête**
   - Cliquer sur "Send"

4. **Vérifier la réponse**
   - Status : Doit être `200 OK`
   - Body : Liste des utilisateurs en JSON

## 🔍 DIAGNOSTIC DE VOTRE PROBLÈME ACTUEL

Vous avez une erreur **403 Forbidden** sur `/users`. Voici les causes possibles :

### Cause 1 : Token Expiré ⏰
Le JWT expire après **5 minutes**. Si vous avez obtenu le token il y a plus de 5 minutes, il est expiré.

**Solution :**
- Refaire l'Étape 1 pour obtenir un nouveau token
- Utiliser immédiatement le nouveau token

### Cause 2 : Format du Header Incorrect ❌
Le header doit être exactement :
```
Authorization: Bearer eyJ0eXAiOiJKV1Qi...
```

**Vérifier :**
- Il y a bien un espace après "Bearer"
- Le token est complet (pas coupé)
- Pas d'espaces supplémentaires avant ou après

### Cause 3 : Token Incomplet 📋
Le JWT est très long. Assurez-vous de copier le token COMPLET.

**Vérifier :**
- Le token se termine généralement par des caractères aléatoires
- Il contient 3 parties séparées par des points : `xxxxx.yyyyy.zzzzz`

## 🧪 TEST RAPIDE AVEC CURL

Pour vérifier que tout fonctionne, testez avec curl :

### 1. Login
```bash
curl -X POST http://localhost:8089/login \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=user1&password=123" \
  -i
```

**Résultat attendu :**
```
HTTP/1.1 200
Authorization: eyJ0eXAiOiJKV1QiLCJhbGc...
```

### 2. Copier le token et tester l'accès
```bash
# Remplacer TOKEN par votre JWT complet
curl -X GET http://localhost:8089/users \
  -H "Authorization: Bearer TOKEN"
```

**Résultat attendu :**
```json
[
  {
    "id": 1,
    "username": "user1",
    ...
  }
]
```

## 🔧 SOLUTION IMMÉDIATE

Suivez ces étapes EXACTEMENT dans cet ordre :

1. **Fermer tous les onglets dans ARC**

2. **Nouvel onglet - LOGIN**
   ```
   POST http://localhost:8089/login
   
   HEADERS:
   Content-Type: application/x-www-form-urlencoded
   
   BODY (x-www-form-urlencoded):
   username=user1
   password=123
   ```
   
3. **Cliquer sur Send**

4. **Dans la réponse, onglet Headers, copier TOUT le contenu du header Authorization**
   - Sélectionner tout le texte
   - Ctrl+C pour copier

5. **Nouvel onglet - GET USERS**
   ```
   GET http://localhost:8089/users
   
   HEADERS:
   Authorization: Bearer [Ctrl+V pour coller le token]
   ```
   
   **IMPORTANT :** Après "Bearer", tapez UN ESPACE, puis collez le token

6. **Cliquer sur Send**

## 📊 VÉRIFICATION DU TOKEN

Pour vérifier que votre token est valide :

1. Aller sur https://jwt.io
2. Coller votre token dans "Encoded"
3. Vérifier dans "Decoded" :
   - **sub** : doit être "user1"
   - **roles** : doit contenir ["USER"]
   - **exp** : vérifier la date d'expiration
   - **iss** : doit être "http://localhost:8089/login"

## 🎬 VIDÉO DE DÉMONSTRATION (Texte)

```
┌─────────────────────────────────────────┐
│ ÉTAPE 1 : LOGIN                         │
├─────────────────────────────────────────┤
│ POST http://localhost:8089/login        │
│                                         │
│ Headers:                                │
│   Content-Type: application/x-www-...  │
│                                         │
│ Body:                                   │
│   username=user1                        │
│   password=123                          │
│                                         │
│ [SEND] ──────────────────────────────> │
│                                         │
│ Response: 200 OK                        │
│ Headers:                                │
│   Authorization: eyJ0eXAi...           │
│                  ↑                      │
│                  └─ COPIER CE TOKEN    │
└─────────────────────────────────────────┘

┌─────────────────────────────────────────┐
│ ÉTAPE 2 : GET USERS                     │
├─────────────────────────────────────────┤
│ GET http://localhost:8089/users         │
│                                         │
│ Headers:                                │
│   Authorization: Bearer eyJ0eXAi...    │
│                         ↑               │
│                         └─ ESPACE ICI   │
│                                         │
│ [SEND] ──────────────────────────────> │
│                                         │
│ Response: 200 OK                        │
│ Body: [{"id":1,"username":"user1"...}] │
└─────────────────────────────────────────┘
```

## ⚡ CHECKLIST RAPIDE

Avant de tester, vérifier :

- [ ] Le serveur est démarré (port 8089)
- [ ] Vous utilisez un token FRAIS (moins de 5 minutes)
- [ ] Le header Authorization contient "Bearer " (avec espace)
- [ ] Le token est complet (pas coupé)
- [ ] Vous testez avec user1/123 ou user2/456

## 🆘 SI ÇA NE MARCHE TOUJOURS PAS

1. **Redémarrer le serveur**
   ```bash
   # Arrêter le serveur (Ctrl+C)
   ./mvnw spring-boot:run
   ```

2. **Vérifier les logs du serveur**
   - Chercher "attemptAuthentication" lors du login
   - Chercher "successfulAuthentication" si login réussi
   - Chercher les erreurs lors de l'accès à /users

3. **Utiliser le fichier test-jwt.html**
   - Ouvrir test-jwt.html dans votre navigateur
   - Cliquer sur "Login"
   - Cliquer sur "Get Users List"
   - Si ça marche dans le HTML mais pas dans ARC, c'est un problème de configuration ARC

## 📞 INFORMATIONS DE DEBUG

Si vous avez toujours des problèmes, vérifiez dans les logs du serveur :

**Lors du login :**
```
attemptAuthentication
successfulAuthentication
```

**Lors de l'accès à /users :**
- Si le token est valide : pas d'erreur, requête passe
- Si le token est invalide : erreur dans les logs

## 🎯 RÉSUMÉ

**Le problème le plus courant : TOKEN EXPIRÉ**

Solution : Obtenir un nouveau token et l'utiliser immédiatement.

**Format correct du header :**
```
Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMSIsInJvbGVzIjpbIlVTRVIiXSwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDg5L2xvZ2luIiwiZXhwIjoxNzE1MDg2NDA5fQ.xxxxx
```

Notez bien l'espace après "Bearer" !
