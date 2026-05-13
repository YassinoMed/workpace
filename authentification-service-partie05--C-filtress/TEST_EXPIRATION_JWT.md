# 🕐 Test d'Expiration du JWT

## Objectif
Tester le comportement de Spring Security lorsque le JWT expire.

## Configuration Actuelle
- **Durée de validité par défaut** : 5 minutes (300 000 ms)
- **Algorithme** : HMAC256
- **Clé** : "MaClé"

## 📝 Étape 1 : Modifier la Durée d'Expiration

### Dans le fichier `JwtAuthenticationFilter.java`

Localiser cette ligne (environ ligne 67) :
```java
.withExpiresAt(new Date(System.currentTimeMillis() + 5 * 60 * 1000))
// date d'expiration après 5 minutes
```

Modifier en :
```java
.withExpiresAt(new Date(System.currentTimeMillis() + 1 * 60 * 1000))
// date d'expiration après 1 minute
```

### Différentes durées pour tester

```java
// 30 secondes (pour test rapide)
.withExpiresAt(new Date(System.currentTimeMillis() + 30 * 1000))

// 1 minute (comme demandé)
.withExpiresAt(new Date(System.currentTimeMillis() + 1 * 60 * 1000))

// 5 minutes (par défaut)
.withExpiresAt(new Date(System.currentTimeMillis() + 5 * 60 * 1000))

// 1 heure
.withExpiresAt(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
```

## 🔄 Étape 2 : Redémarrer l'Application

```bash
# Arrêter l'application (Ctrl+C dans le terminal)
# Puis redémarrer
./mvnw spring-boot:run
```

## 🧪 Étape 3 : Tester l'Expiration

### Test A : Token Valide (Immédiatement après login)

1. **S'authentifier**
   ```
   POST http://localhost:8089/login
   Content-Type: application/x-www-form-urlencoded
   
   username=user1
   password=123
   ```

2. **Copier le JWT** du header Authorization

3. **Accéder immédiatement à /users**
   ```
   GET http://localhost:8089/users
   Authorization: Bearer <VOTRE_JWT>
   ```

4. **Résultat attendu** : ✅ 200 OK avec liste des utilisateurs

### Test B : Token Expiré (Après 1 minute)

1. **Attendre 1 minute et 10 secondes** (pour être sûr)

2. **Réutiliser le même JWT**
   ```
   GET http://localhost:8089/users
   Authorization: Bearer <MÊME_JWT_QU'AVANT>
   ```

3. **Résultat attendu** : ❌ 403 Forbidden

4. **Vérifier les logs du serveur** :
   ```
   === JwtAuthorizationFilter ===
   Path: /users
   Authorization Header: Bearer eyJ0eXAi...
   JWT extrait: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...
   Erreur de validation JWT: The Token has expired on ...
   ```

### Test C : Obtenir un Nouveau Token

1. **Se ré-authentifier**
   ```
   POST http://localhost:8089/login
   Content-Type: application/x-www-form-urlencoded
   
   username=user1
   password=123
   ```

2. **Copier le NOUVEAU JWT**

3. **Accéder à /users avec le nouveau token**
   ```
   GET http://localhost:8089/users
   Authorization: Bearer <NOUVEAU_JWT>
   ```

4. **Résultat attendu** : ✅ 200 OK

## 📊 Tableau de Test

| Test | Token | Temps écoulé | Résultat Attendu | Status |
|------|-------|--------------|------------------|--------|
| 1 | Nouveau | 0 sec | 200 OK | ✅ |
| 2 | Même | 30 sec | 200 OK | ✅ |
| 3 | Même | 70 sec | 403 Forbidden | ❌ |
| 4 | Nouveau | 0 sec | 200 OK | ✅ |

## 🔍 Vérification de l'Expiration sur jwt.io

1. Aller sur https://jwt.io
2. Coller votre JWT dans "Encoded"
3. Dans "Decoded" > "Payload", regarder le champ **"exp"**
4. Convertir le timestamp en date :
   ```javascript
   // Dans la console du navigateur
   new Date(1715086409 * 1000)
   // Résultat : Date d'expiration
   ```

5. Comparer avec l'heure actuelle :
   ```javascript
   new Date()
   // Résultat : Heure actuelle
   ```

## 🎯 Script de Test Automatique (curl)

### test-expiration.sh (Linux/Mac)

```bash
#!/bin/bash

echo "=== Test d'Expiration JWT ==="
echo ""

# 1. Login et récupération du token
echo "1. Authentification..."
RESPONSE=$(curl -s -i -X POST http://localhost:8089/login \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=user1&password=123")

TOKEN=$(echo "$RESPONSE" | grep -i "Authorization:" | cut -d' ' -f2- | tr -d '\r')

if [ -z "$TOKEN" ]; then
    echo "❌ Erreur : Impossible d'obtenir le token"
    exit 1
fi

echo "✅ Token obtenu : ${TOKEN:0:50}..."
echo ""

# 2. Test immédiat
echo "2. Test immédiat (token valide)..."
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" \
  -H "Authorization: Bearer $TOKEN" \
  http://localhost:8089/users)

if [ "$HTTP_CODE" = "200" ]; then
    echo "✅ Accès autorisé (200 OK)"
else
    echo "❌ Accès refusé ($HTTP_CODE)"
fi
echo ""

# 3. Attendre 70 secondes
echo "3. Attente de 70 secondes pour l'expiration..."
for i in {70..1}; do
    echo -ne "   Temps restant : $i secondes\r"
    sleep 1
done
echo ""
echo ""

# 4. Test après expiration
echo "4. Test après expiration (token expiré)..."
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" \
  -H "Authorization: Bearer $TOKEN" \
  http://localhost:8089/users)

if [ "$HTTP_CODE" = "403" ]; then
    echo "✅ Accès refusé comme attendu (403 Forbidden)"
else
    echo "❌ Résultat inattendu ($HTTP_CODE)"
fi
echo ""

# 5. Nouveau token
echo "5. Obtention d'un nouveau token..."
RESPONSE=$(curl -s -i -X POST http://localhost:8089/login \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=user1&password=123")

NEW_TOKEN=$(echo "$RESPONSE" | grep -i "Authorization:" | cut -d' ' -f2- | tr -d '\r')

echo "✅ Nouveau token obtenu : ${NEW_TOKEN:0:50}..."
echo ""

# 6. Test avec nouveau token
echo "6. Test avec le nouveau token..."
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" \
  -H "Authorization: Bearer $NEW_TOKEN" \
  http://localhost:8089/users)

if [ "$HTTP_CODE" = "200" ]; then
    echo "✅ Accès autorisé avec le nouveau token (200 OK)"
else
    echo "❌ Accès refusé ($HTTP_CODE)"
fi

echo ""
echo "=== Test terminé ==="
```

### test-expiration.ps1 (Windows PowerShell)

```powershell
Write-Host "=== Test d'Expiration JWT ===" -ForegroundColor Cyan
Write-Host ""

# 1. Login
Write-Host "1. Authentification..." -ForegroundColor Yellow
$body = @{
    username = "user1"
    password = "123"
}
$response = Invoke-WebRequest -Uri "http://localhost:8089/login" `
    -Method POST `
    -ContentType "application/x-www-form-urlencoded" `
    -Body $body

$token = $response.Headers["Authorization"]

if (-not $token) {
    Write-Host "❌ Erreur : Impossible d'obtenir le token" -ForegroundColor Red
    exit 1
}

Write-Host "✅ Token obtenu : $($token.Substring(0, 50))..." -ForegroundColor Green
Write-Host ""

# 2. Test immédiat
Write-Host "2. Test immédiat (token valide)..." -ForegroundColor Yellow
try {
    $headers = @{ Authorization = "Bearer $token" }
    $response = Invoke-WebRequest -Uri "http://localhost:8089/users" `
        -Method GET `
        -Headers $headers
    Write-Host "✅ Accès autorisé (200 OK)" -ForegroundColor Green
} catch {
    Write-Host "❌ Accès refusé ($($_.Exception.Response.StatusCode))" -ForegroundColor Red
}
Write-Host ""

# 3. Attendre 70 secondes
Write-Host "3. Attente de 70 secondes pour l'expiration..." -ForegroundColor Yellow
for ($i = 70; $i -gt 0; $i--) {
    Write-Host "`r   Temps restant : $i secondes" -NoNewline
    Start-Sleep -Seconds 1
}
Write-Host ""
Write-Host ""

# 4. Test après expiration
Write-Host "4. Test après expiration (token expiré)..." -ForegroundColor Yellow
try {
    $headers = @{ Authorization = "Bearer $token" }
    $response = Invoke-WebRequest -Uri "http://localhost:8089/users" `
        -Method GET `
        -Headers $headers
    Write-Host "❌ Résultat inattendu (200 OK)" -ForegroundColor Red
} catch {
    if ($_.Exception.Response.StatusCode -eq 403) {
        Write-Host "✅ Accès refusé comme attendu (403 Forbidden)" -ForegroundColor Green
    } else {
        Write-Host "❌ Résultat inattendu ($($_.Exception.Response.StatusCode))" -ForegroundColor Red
    }
}
Write-Host ""

Write-Host "=== Test terminé ===" -ForegroundColor Cyan
```

## 📋 Checklist de Test

- [ ] Modifier la durée d'expiration à 1 minute
- [ ] Redémarrer l'application
- [ ] S'authentifier et obtenir un JWT
- [ ] Tester l'accès immédiat (doit fonctionner)
- [ ] Attendre 70 secondes
- [ ] Tester l'accès avec le token expiré (doit échouer avec 403)
- [ ] Vérifier les logs du serveur pour voir l'erreur d'expiration
- [ ] Obtenir un nouveau token
- [ ] Tester l'accès avec le nouveau token (doit fonctionner)
- [ ] Remettre la durée à 5 minutes pour l'utilisation normale

## 🔧 Messages d'Erreur Attendus

### Dans les logs du serveur (token expiré) :
```
=== JwtAuthorizationFilter ===
Path: /users
Authorization Header: Bearer eyJ0eXAi...
JWT extrait: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...
Erreur de validation JWT: The Token has expired on Tue May 06 12:30:00 CET 2026.
```

### Dans la réponse HTTP :
```
HTTP/1.1 403 Forbidden
error-message: The Token has expired on Tue May 06 12:30:00 CET 2026.
```

## 💡 Conseils

1. **Pour des tests rapides** : Utilisez 30 secondes au lieu de 1 minute
2. **Logs détaillés** : Les logs ont été ajoutés pour voir exactement ce qui se passe
3. **Vérification** : Utilisez jwt.io pour vérifier la date d'expiration
4. **Production** : En production, utilisez une durée plus longue (15-30 minutes)

## 🎓 Comprendre l'Expiration

Le JWT contient un champ `exp` (expiration) qui est un timestamp Unix :
```json
{
  "sub": "user1",
  "roles": ["USER"],
  "iss": "http://localhost:8089/login",
  "exp": 1715086409  ← Timestamp d'expiration
}
```

Le serveur vérifie :
```java
if (currentTime > exp) {
    throw new TokenExpiredException("The Token has expired");
}
```

## ✅ Résultat Attendu

Après ce test, vous devriez comprendre :
1. Comment le JWT expire après la durée configurée
2. Comment Spring Security rejette les tokens expirés
3. Comment obtenir un nouveau token après expiration
4. L'importance de gérer le refresh token en production
