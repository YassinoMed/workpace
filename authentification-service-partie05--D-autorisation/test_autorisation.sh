#!/bin/bash

BASE_URL="http://localhost:8089"

echo "======================================"
echo " TESTS AUTORISATION JWT - SPRING BOOT "
echo "======================================"
echo ""

get_token() {
  local username=$1
  local password=$2

  RESPONSE=$(curl -si -X POST "$BASE_URL/login" \
    -d "username=$username&password=$password")

  echo "$RESPONSE" > "login_${username}_response.txt"

  TOKEN=$(echo "$RESPONSE" \
    | grep -i "^Authorization:" \
    | sed 's/Authorization: //I' \
    | tr -d '\r')

  echo "$TOKEN"
}

test_request() {
  local title=$1
  local method=$2
  local url=$3
  local token=$4
  local data=$5

  echo "--------------------------------------"
  echo "$title"
  echo "--------------------------------------"

  if [ -z "$token" ]; then
    curl -i -X "$method" "$url"
  else
    if [ -z "$data" ]; then
      curl -i -X "$method" "$url" \
        -H "Authorization: $token"
    else
      curl -i -X "$method" "$url" \
        -H "Authorization: $token" \
        -H "Content-Type: application/json" \
        -d "$data"
    fi
  fi

  echo ""
  echo ""
}

echo "1) Test sans JWT"
test_request "GET /users sans token — attendu : 401 ou 403" \
  "GET" "$BASE_URL/users" "" ""

echo "2) Login user1"
TOKEN_USER1=$(get_token "user1" "123")

if [ -z "$TOKEN_USER1" ]; then
  echo "ERREUR : Token user1 non récupéré."
  echo "Voici la réponse complète de /login :"
  cat login_user1_response.txt
  exit 1
fi

echo "Token user1 récupéré : OK"
echo ""

echo "3) Tests avec user1"
test_request "GET /users avec user1 — attendu : 200" \
  "GET" "$BASE_URL/users" "$TOKEN_USER1" ""

test_request "POST /users avec user1 — attendu : 403" \
  "POST" "$BASE_URL/users" "$TOKEN_USER1" \
  '{"username":"testUserByUser1","password":"123"}'

echo "4) Login user2"
TOKEN_USER2=$(get_token "user2" "456")

if [ -z "$TOKEN_USER2" ]; then
  echo "ERREUR : Token user2 non récupéré."
  echo "Voici la réponse complète de /login :"
  cat login_user2_response.txt
  exit 1
fi

echo "Token user2 récupéré : OK"
echo ""

echo "5) Tests avec user2"
test_request "GET /users avec user2 — attendu : 200" \
  "GET" "$BASE_URL/users" "$TOKEN_USER2" ""

test_request "POST /users avec user2 — attendu : 200 ou 201" \
  "POST" "$BASE_URL/users" "$TOKEN_USER2" \
  '{"username":"testUserByAdmin","password":"123"}'

echo "======================================"
echo " Résumé attendu"
echo "======================================"
echo "Sans token       : GET /users  => 403 ou 401"
echo "user1 USER       : GET /users  => 200"
echo "user1 USER       : POST /users => 403"
echo "user2 USER+ADMIN : GET /users  => 200"
echo "user2 ADMIN      : POST /users => 200 ou 201"
echo "======================================"
