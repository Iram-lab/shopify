#!/bin/bash
# ─── Wait for Eureka Service Registration ────────────────────────────
# Polls Eureka until all expected services are registered.
# Usage: ./wait-for-eureka.sh <eureka-url> <comma-separated-services> <timeout>
# Example: ./wait-for-eureka.sh http://localhost:8761 "auth-service,product-service" 120
# ─────────────────────────────────────────────────────────────────────

set -euo pipefail

EUREKA_URL="${1:-http://localhost:8761}"
EXPECTED_SERVICES="${2:-api-gateway,auth-service,product-service,inventory-service,cart-service,order-service,payment-service,notification-service}"
TIMEOUT="${3:-120}"
INTERVAL=5
ELAPSED=0

echo "Waiting for services to register with Eureka at ${EUREKA_URL}..."
echo "Expected: ${EXPECTED_SERVICES}"

IFS=',' read -ra SERVICES <<< "$EXPECTED_SERVICES"

while [ "$ELAPSED" -lt "$TIMEOUT" ]; do
    # Get registered apps from Eureka
    REGISTERED=$(curl -s -H "Accept: application/json" \
        "${EUREKA_URL}/eureka/apps" 2>/dev/null \
        | grep -o '"name":"[^"]*"' \
        | sed 's/"name":"//;s/"//' \
        | tr '[:upper:]' '[:lower:]' || echo "")

    ALL_UP=true
    for SVC in "${SERVICES[@]}"; do
        SVC_LOWER=$(echo "$SVC" | tr '[:upper:]' '[:lower:]')
        if echo "$REGISTERED" | grep -q "$SVC_LOWER"; then
            echo "  ✅ ${SVC} registered"
        else
            echo "  ⏳ ${SVC} not yet registered..."
            ALL_UP=false
        fi
    done

    if [ "$ALL_UP" = "true" ]; then
        echo "✅ All services registered with Eureka!"
        exit 0
    fi

    sleep "$INTERVAL"
    ELAPSED=$((ELAPSED + INTERVAL))
    echo "  Waiting... (${ELAPSED}/${TIMEOUT}s)"
done

echo "❌ Timeout: Not all services registered within ${TIMEOUT}s"
exit 1
