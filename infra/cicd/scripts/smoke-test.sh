#!/bin/bash
# ─── Smoke Test ───────────────────────────────────────────────────────
# Polls all ShopMicro service health endpoints.
# Usage: ./smoke-test.sh <gateway-url> <timeout-seconds>
# ─────────────────────────────────────────────────────────────────────

set -euo pipefail

GATEWAY_URL="${1:-http://localhost:8080}"
TIMEOUT="${2:-120}"
INTERVAL=5
ELAPSED=0
FAILED=0

# Direct service health endpoints (bypasses gateway for internal checks)
declare -A SERVICES=(
    ["eureka-server"]="http://localhost:8761/actuator/health"
    ["api-gateway"]="http://localhost:8080/actuator/health"
    ["auth-service"]="http://localhost:8081/actuator/health"
    ["product-service"]="http://localhost:8082/actuator/health"
    ["inventory-service"]="http://localhost:8083/actuator/health"
    ["cart-service"]="http://localhost:8084/actuator/health"
    ["payment-service"]="http://localhost:8085/actuator/health"
    ["order-service"]="http://localhost:8086/actuator/health"
    ["notification-service"]="http://localhost:8087/actuator/health"
)

echo "═══════════════════════════════════════════════════"
echo "  ShopMicro Smoke Test"
echo "  Gateway: ${GATEWAY_URL}"
echo "  Timeout: ${TIMEOUT}s"
echo "═══════════════════════════════════════════════════"

# Wait for all services to be UP
for SVC in "${!SERVICES[@]}"; do
    URL="${SERVICES[$SVC]}"
    ELAPSED=0
    echo -n "  Waiting for ${SVC}..."

    while [ "$ELAPSED" -lt "$TIMEOUT" ]; do
        STATUS=$(curl -s -o /dev/null -w "%{http_code}" \
            "${URL}" --max-time 5 2>/dev/null || echo "000")

        if [ "$STATUS" = "200" ]; then
            echo " ✅ UP (${ELAPSED}s)"
            break
        fi

        sleep "$INTERVAL"
        ELAPSED=$((ELAPSED + INTERVAL))

        if [ "$ELAPSED" -ge "$TIMEOUT" ]; then
            echo " ❌ TIMEOUT after ${TIMEOUT}s (last HTTP: ${STATUS})"
            FAILED=$((FAILED + 1))
        fi
    done
done

# Check public API endpoints via gateway
echo ""
echo "  Checking public API endpoints..."

PUBLIC_ENDPOINTS=(
    "/api/products"
    "/api/categories"
)

for ENDPOINT in "${PUBLIC_ENDPOINTS[@]}"; do
    STATUS=$(curl -s -o /dev/null -w "%{http_code}" \
        "${GATEWAY_URL}${ENDPOINT}" --max-time 10 2>/dev/null || echo "000")

    if [ "$STATUS" = "200" ]; then
        echo "  ✅ GET ${ENDPOINT} → ${STATUS}"
    else
        echo "  ❌ GET ${ENDPOINT} → ${STATUS}"
        FAILED=$((FAILED + 1))
    fi
done

echo ""
echo "═══════════════════════════════════════════════════"
if [ "$FAILED" -eq 0 ]; then
    echo "  ✅ All smoke tests passed"
    exit 0
else
    echo "  ❌ ${FAILED} smoke test(s) failed"
    exit 1
fi
