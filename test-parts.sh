#!/usr/bin/env bash

# ==============================================================================
# Script de Test Automatisé - Partie 05 (A, B, C)
# ==============================================================================

# Couleurs pour l'affichage
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # Pas de couleur

# Ports des instances actives
PORT_A=8089
PORT_B=8090
PORT_C=8096

# Fichier temporaire pour stocker les cookies et les tokens
COOKIE_FILE="/tmp/session_cookie.txt"
TOKEN_FILE="/tmp/jwt_token.txt"

# Nettoyage des fichiers temporaires au début
rm -f "$COOKIE_FILE" "$TOKEN_FILE"

print_header() {
    echo -e "\n${BLUE}======================================================================${NC}"
    echo -e "${BLUE}>>> $1${NC}"
    echo -e "${BLUE}======================================================================${NC}"
}

print_step() {
    echo -e "\n${CYAN}[Étape $1] $2${NC}"
}

check_service_up() {
    local port=$1
    if ! nc -z localhost "$port" 2>/dev/null; then
        echo -e "${RED}❌ Le service sur le port $port n'est pas actif ! Veillez à ce qu'il soit démarré.${NC}"
        return 1
    fi
    return 0
}

# ==============================================================================
# TEST PARTIE A : Session-based Security (Port 8089)
# ==============================================================================
print_header "TEST PARTIE 05 - A : SESSION SECURITY (Port $PORT_A)"

if check_service_up $PORT_A; then

    print_step "1" "Requête anonyme vers /users (Attendu : Redirection 302 vers /login)"
    RESPONSE=$(curl -s -o /dev/null -w "%{http_code} -> Location: %{redirect_url}" http://localhost:$PORT_A/users)
    echo -e "Réponse reçue : ${YELLOW}$RESPONSE${NC}"

    print_step "2" "Authentification de 'user1' (Attendu : Récupération du Cookie de session JSESSIONID)"
    # Envoi de la requête POST de login et enregistrement du cookie dans un fichier
    HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X POST \
        -d "username=user1&password=123" \
        -c "$COOKIE_FILE" \
        http://localhost:$PORT_A/login)
    
    if [ "$HTTP_STATUS" == "302" ] || [ "$HTTP_STATUS" == "200" ]; then
        echo -e "Status HTTP : ${GREEN}$HTTP_STATUS OK${NC}"
        echo -e "Cookie enregistré dans ${YELLOW}$COOKIE_FILE${NC} :"
        cat "$COOKIE_FILE" | grep "JSESSIONID" || echo "Aucun cookie JSESSIONID reçu."
    else
        echo -e "${RED}❌ Échec d'authentification (Status: $HTTP_STATUS)${NC}"
    fi

    print_step "3" "Accès à /users AVEC le Cookie de Session (Attendu : Liste JSON des utilisateurs)"
    if [ -f "$COOKIE_FILE" ]; then
        curl -s -b "$COOKIE_FILE" http://localhost:$PORT_A/users | jq . 2>/dev/null || curl -s -b "$COOKIE_FILE" http://localhost:$PORT_A/users
        echo ""
    else
        echo -e "${RED}Impossible de tester sans cookie de session.${NC}"
    fi

fi

# ==============================================================================
# TEST PARTIE B : Stateless Security (Port 8090)
# ==============================================================================
print_header "TEST PARTIE 05 - B : STATELESS SECURITY (Port $PORT_B)"

if check_service_up $PORT_B; then

    print_step "1" "Requête vers /users sans authentification (Attendu : 403 Forbidden)"
    HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:$PORT_B/users)
    echo -e "Status HTTP : ${GREEN}$HTTP_STATUS${NC} (Pas de redirection car le mode est STATELESS)"

    print_step "2" "Tentative de connexion par formulaire (Attendu : 403 ou 404 car formLogin() est désactivé)"
    HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X POST -d "username=user1&password=123" http://localhost:$PORT_B/login)
    echo -e "Status HTTP sur /login : ${YELLOW}$HTTP_STATUS${NC}"

fi

# ==============================================================================
# TEST PARTIE C : Filters / JWT (Port 8096)
# ==============================================================================
print_header "TEST PARTIE 05 - C : FILTERS / JWT (Port $PORT_C)"

if check_service_up $PORT_C; then

    print_step "1" "Requête vers /users sans Token JWT (Attendu : 403 Forbidden)"
    HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:$PORT_C/users)
    echo -e "Status HTTP : ${GREEN}$HTTP_STATUS Forbidden${NC}"

    print_step "2" "Authentification via JwtAuthenticationFilter (Attendu : Récupération du Token JWT)"
    # Récupérer les en-têtes HTTP de la réponse de login
    LOGIN_HEADERS=$(curl -s -D - -o /dev/null -X POST -d "username=user2&password=456" http://localhost:$PORT_C/login)
    
    # Extraire la ligne Authorization
    JWT_HEADER=$(echo "$LOGIN_HEADERS" | grep -Fi "Authorization:")
    
    if [ ! -z "$JWT_HEADER" ]; then
        # Extraire la valeur du token (supprimer 'Bearer ' et les retours à la ligne)
        TOKEN=$(echo "$JWT_HEADER" | awk '{print $2}' | tr -d '\r\n')
        echo "$TOKEN" > "$TOKEN_FILE"
        echo -e "${GREEN}✅ Token JWT généré avec succès !${NC}"
        echo -e "Extrait du Token : ${YELLOW}${TOKEN:0:50}...${NC}"
    else
        echo -e "${RED}❌ Impossible de récupérer le token JWT.${NC}"
        echo -e "En-têtes reçus :\n$LOGIN_HEADERS"
    fi

    print_step "3" "Accès à /users AVEC le Token JWT (Attendu : Liste JSON des utilisateurs)"
    if [ -f "$TOKEN_FILE" ]; then
        ACTIVE_TOKEN=$(cat "$TOKEN_FILE")
        curl -s -H "Authorization: Bearer $ACTIVE_TOKEN" http://localhost:$PORT_C/users | jq . 2>/dev/null || curl -s -H "Authorization: Bearer $ACTIVE_TOKEN" http://localhost:$PORT_C/users
        echo ""
    else
        echo -e "${RED}Impossible de tester sans token JWT.${NC}"
    fi

fi

# Nettoyage final
rm -f "$COOKIE_FILE" "$TOKEN_FILE"
echo -e "\n${GREEN}=== Fin des Tests ===${NC}\n"
