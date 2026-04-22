#!/usr/bin/env bash
set -e

# ========= CONFIG =========
BASE="$HOME/DEV/Micro_Services/workspace"
EUREKA_DIR="$BASE/eureka-discovery-service"
GATEWAY_DIR="$BASE/gateway-service"
PRODUIT_DIR="$BASE/produit-service"
LOG_DIR="$HOME/ms-logs"

mkdir -p "$LOG_DIR"

echo "=== [0] Nettoyage anciens processus ==="
pkill -f 'eureka-discovery-service' || true
pkill -f 'gateway-service' || true
pkill -f 'produit-service' || true
sleep 3

echo "=== [1] Build des projets ==="
cd "$EUREKA_DIR" && mvn -q clean package -DskipTests
cd "$GATEWAY_DIR" && mvn -q clean package -DskipTests
cd "$PRODUIT_DIR" && mvn -q clean package -DskipTests

EUREKA_JAR=$(find "$EUREKA_DIR/target" -maxdepth 1 -name '*.jar' | head -n 1)
GATEWAY_JAR=$(find "$GATEWAY_DIR/target" -maxdepth 1 -name '*.jar' | head -n 1)
PRODUIT_JAR=$(find "$PRODUIT_DIR/target" -maxdepth 1 -name '*.jar' | head -n 1)

echo "=== [2] Lancer eureka-discovery-service (port 8761) ==="
nohup java -jar "$EUREKA_JAR" > "$LOG_DIR/eureka.log" 2>&1 &
sleep 15

echo "=== [3] Lancer gateway-service (port 8888) ==="
nohup java -jar "$GATEWAY_JAR" > "$LOG_DIR/gateway.log" 2>&1 &
sleep 15

echo "=== [4] Lancer produit-service sur port 9002 ==="
nohup java -jar "$PRODUIT_JAR" --server.port=9002 > "$LOG_DIR/produit-9002.log" 2>&1 &
sleep 15

echo "=== [5] Vérifier l'enregistrement Eureka du produit-service ==="
curl -s http://localhost:8761/eureka/apps/PRODUIT-SERVICE || true
echo
echo

echo "=== [6] Afficher la liste JSON des produits via gateway ==="
curl -s http://localhost:8888/PRODUIT-SERVICE/produits || true
echo
echo

echo "=== [7] Essayer de lancer une 2e instance sur le MEME port 9002 (doit échouer) ==="
set +e
timeout 20s java -jar "$PRODUIT_JAR" --server.port=9002 > "$LOG_DIR/produit-port-conflit.log" 2>&1
set -e
echo "---- Extrait du log du conflit de port ----"
tail -n 20 "$LOG_DIR/produit-port-conflit.log" || true
echo

echo "=== [8] Arrêter produit-service pour relancer plusieurs instances ==="
pkill -f "$PRODUIT_JAR" || true
sleep 5

echo "=== [9] Lancer 3 instances produit-service : 9002, 9102, 9202 ==="
nohup java -jar "$PRODUIT_JAR" --server.port=9002 > "$LOG_DIR/produit-9002.log" 2>&1 &
nohup java -jar "$PRODUIT_JAR" --server.port=9102 > "$LOG_DIR/produit-9102.log" 2>&1 &
nohup java -jar "$PRODUIT_JAR" --server.port=9202 > "$LOG_DIR/produit-9202.log" 2>&1 &
sleep 20

echo "=== [10] Vérifier les 3 instances en parallèle ==="
ss -ltnp | grep -E ':9002|:9102|:9202|:8761|:8888' || true
echo
ps -ef | grep java | grep -E 'eureka|gateway|produit' | grep -v grep || true
echo

echo "=== [11] Vérifier le triple enregistrement dans Eureka ==="
curl -s http://localhost:8761/eureka/apps/PRODUIT-SERVICE || true
echo
echo

echo "=== Test bonus : produits via gateway ==="
curl -s http://localhost:8888/PRODUIT-SERVICE/produits || true
echo
echo

echo "=== Logs disponibles dans : $LOG_DIR ==="
echo "eureka.log"
echo "gateway.log"
echo "produit-9002.log"
echo "produit-9102.log"
echo "produit-9202.log"
echo "produit-port-conflit.log"
