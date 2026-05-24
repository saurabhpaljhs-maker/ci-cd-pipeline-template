#!/bin/bash
# ============================================================
# Emergency Manual Rollback Script
# Usage: ./scripts/rollback.sh <image-tag> <namespace>
# Example: ./scripts/rollback.sh 42-abc1234 production
# ============================================================
set -euo pipefail

IMAGE_TAG="${1:?Error: Provide image tag. Usage: ./rollback.sh <tag> <namespace>}"
NAMESPACE="${2:-production}"
APP_NAME="demo-app"
DOCKER_REGISTRY="yourdockerhubusername"
IMAGE="${DOCKER_REGISTRY}/${APP_NAME}:${IMAGE_TAG}"

echo "🔄 Rolling back ${APP_NAME} to image: ${IMAGE}"
echo "   Namespace: ${NAMESPACE}"
echo ""

# Confirm before proceeding
read -rp "Are you sure? (yes/no): " confirm
[[ "$confirm" != "yes" ]] && { echo "Aborted."; exit 0; }

# Check kubectl context
CURRENT_CONTEXT=$(kubectl config current-context)
echo "⚡ Kubectl context: ${CURRENT_CONTEXT}"
echo ""

# Set specific image on the deployment
kubectl set image deployment/${APP_NAME} \
  ${APP_NAME}=${IMAGE} \
  -n ${NAMESPACE}

# Wait for rollout
echo "⏳ Waiting for rollout..."
kubectl rollout status deployment/${APP_NAME} \
  -n ${NAMESPACE} \
  --timeout=120s

# Verify pods are running
echo ""
echo "📊 Pod status after rollback:"
kubectl get pods -n ${NAMESPACE} -l app=${APP_NAME}

echo ""
echo "✅ Rollback complete! Running image: ${IMAGE}"
