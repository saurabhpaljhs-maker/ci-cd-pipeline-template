#!/bin/bash
# ============================================================
# health-check.sh — Post-deploy health verification
# Usage: ./scripts/health-check.sh <environment> <app-name>
# ============================================================

set -euo pipefail

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

ENVIRONMENT=${1:-"dev"}
APP_NAME=${2:-"demo-app"}
MAX_RETRIES=12
RETRY_INTERVAL=10

# Map environment to service URL
case "$ENVIRONMENT" in
    dev)     APP_URL="http://demo-app.dev.svc.cluster.local/actuator/health" ;;
    staging) APP_URL="http://demo-app.staging.svc.cluster.local/actuator/health" ;;
    prod)    APP_URL="http://demo-app.production.svc.cluster.local/actuator/health" ;;
    *)       echo -e "${RED}Unknown environment: $ENVIRONMENT${NC}"; exit 1 ;;
esac

echo -e "${YELLOW}🔍 Health check: ${APP_NAME} on ${ENVIRONMENT}${NC}"
echo "   URL: $APP_URL"
echo ""

for i in $(seq 1 $MAX_RETRIES); do
    echo -n "   Attempt $i/$MAX_RETRIES ... "

    HTTP_CODE=$(curl -s -o /tmp/health-response.json -w "%{http_code}" \
        --connect-timeout 5 --max-time 10 "$APP_URL" 2>/dev/null || echo "000")

    if [[ "$HTTP_CODE" == "200" ]]; then
        STATUS=$(cat /tmp/health-response.json | python3 -c "import sys,json; print(json.load(sys.stdin).get('status','UNKNOWN'))" 2>/dev/null || echo "UNKNOWN")
        if [[ "$STATUS" == "UP" ]]; then
            echo -e "${GREEN}✅ UP (HTTP 200)${NC}"
            echo ""
            echo -e "${GREEN}✅ Health check PASSED for ${APP_NAME} on ${ENVIRONMENT}${NC}"
            exit 0
        else
            echo -e "${YELLOW}⚠️  HTTP 200 but status=$STATUS${NC}"
        fi
    else
        echo -e "${RED}❌ HTTP $HTTP_CODE${NC}"
    fi

    if [[ $i -lt $MAX_RETRIES ]]; then
        sleep "$RETRY_INTERVAL"
    fi
done

echo ""
echo -e "${RED}❌ Health check FAILED after $MAX_RETRIES attempts${NC}"
echo "   Last response:"
cat /tmp/health-response.json 2>/dev/null || echo "   (no response)"
exit 1
