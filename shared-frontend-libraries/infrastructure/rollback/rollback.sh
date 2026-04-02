#!/bin/bash

# Automated rollback script for shared-frontend-libraries
# Usage: ./rollback.sh <version> <environment>

set -e

VERSION=${1:-latest}
ENVIRONMENT=${2:-production}
NAMESPACE="rapid-assist"
DEPLOYMENT_NAME="shared-frontend-libraries"

# Color output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

log() {
  echo -e "${NC}[$(date +'%Y-%m-%d %H:%M:%S')] $1"
}

error() {
  echo -e "${RED}[ERROR] $1${NC}"
  exit 1
}

warn() {
  echo -e "${YELLOW}[WARN] $1${NC}"
}

success() {
  echo -e "${GREEN}[SUCCESS] $1${NC}"
}

# Check if kubectl is available
if ! command -v kubectl &> /dev/null; then
  error "kubectl is not installed or not in PATH"
fi

# Check if we're in the correct directory
if [ ! -d ".git" ]; then
  error "Must be run from the repository root"
fi

log "Starting rollback of shared-frontend-libraries to version: $VERSION in $ENVIRONMENT"

# Pre-rollback checks
log "Checking current deployment status..."

# Get current deployment info
CURRENT_REPLICAS=$(kubectl get deployment "$DEPLOYMENT_NAME" -n "$NAMESPACE" -o jsonpath='{.spec.replicas}' 2>/dev/null || echo "unknown")
CURRENT_IMAGE=$(kubectl get deployment "$DEPLOYMENT_NAME" -n "$NAMESPACE" -o jsonpath='{.spec.template.spec.containers[0].image}' 2>/dev/null || echo "unknown")

log "Current replicas: $CURRENT_REPLICAS"
log "Current image: $CURRENT_IMAGE"

# Check if deployment exists
if kubectl get deployment "$DEPLOYMENT_NAME" -n "$NAMESPACE" &>/dev/null; then
  log "Deployment $DEPLOYMENT_NAME found in namespace $NAMESPACE"
else
  error "Deployment $DEPLOYMENT_NAME not found in namespace $NAMESPACE"
fi

# Backup current configuration
log "Backing up current deployment configuration..."
BACKUP_DIR="./backups/rollback-$(date +%Y%m%d-%H%M%S)"
mkdir -p "$BACKUP_DIR"

kubectl get deployment "$DEPLOYMENT_NAME" -n "$NAMESPACE" -o yaml > "$BACKUP_DIR/deployment-backup.yaml"
kubectl get service "$DEPLOYMENT_NAME" -n "$NAMESPACE" -o yaml > "$BACKUP_DIR/service-backup.yaml"
kubectl get ingress "$DEPLOYMENT_NAME" -n "$NAMESPACE" -o yaml > "$BACKUP_DIR/ingress-backup.yaml"
kubectl get configmap "$DEPLOYMENT_NAME" -n "$NAMESPACE" -o yaml > "$BACKUP_DIR/configmap-backup.yaml"

log "Backup saved to: $BACKUP_DIR"

# Determine rollback strategy
case "$ENVIRONMENT" in
  production)
    ROLLBACK_STRATEGY="full"
    ;;
  staging)
    ROLLBACK_STRATEGY="partial"
    ;;
  *)
    ROLLBACK_STRATEGY="full"
    ;;
esac

log "Rollback strategy: $ROLLBACK_STRATEGY"

# Perform rollback based on strategy
case "$ROLLBACK_STRATEGY" in
  full)
    log "Performing full rollback to version: $VERSION"

    # Rollback using kubectl rollout undo
    log "Running kubectl rollout undo..."
    kubectl rollout undo deployment "$DEPLOYMENT_NAME" -n "$NAMESPACE" --to-revision=$(kubectl rollout history deployment "$DEPLOYMENT_NAME" -n "$NAMESPACE" | grep "$VERSION" | awk '{print $1}')

    ;;

  partial)
    log "Performing partial rollback..."

    # Scale down non-canary replicas
    log "Scaling down green replicas..."
    kubectl scale deployment "$DEPLOYMENT_NAME-green" -n "$NAMESPACE" --replicas=0 || warn "Green deployment not found, skipping scale down"

    # Scale down canary replicas
    log "Scaling down canary replicas..."
    kubectl scale deployment "$DEPLOYMENT_NAME-canary" -n "$NAMESPACE" --replicas=0 || warn "Canary deployment not found, skipping scale down"

    ;;

  *)
    error "Unknown rollback strategy: $ROLLBACK_STRATEGY"
    ;;
esac

# Wait for rollout to complete
log "Waiting for rollout to complete..."
kubectl rollout status deployment "$DEPLOYMENT_NAME" -n "$NAMESPACE" --timeout=5m

# Verify rollback
log "Verifying rollback..."
sleep 10

READY_REPLICAS=$(kubectl get deployment "$DEPLOYMENT_NAME" -n "$NAMESPACE" -o jsonpath='{.status.readyReplicas}' 2>/dev/null || echo "unknown")
DESIRED_REPLICAS=$(kubectl get deployment "$DEPLOYMENT_NAME" -n "$NAMESPACE" -o jsonpath='{.spec.replicas}' 2>/dev/null || echo "unknown")

log "Ready replicas: $READY_REPLICAS / $DESIRED_REPLICAS"

# Check rollout status
ROLLOUT_STATUS=$(kubectl rollout status deployment "$DEPLOYMENT_NAME" -n "$NAMESPACE" -o jsonpath='{.status.conditions[?(@.type=="Available")].status}' 2>/dev/null)

if [ "$ROLLOUT_STATUS" = "True" ]; then
  success "Rollback completed successfully!"
else
  warn "Rollback completed but may not be fully available. Run: kubectl rollout status deployment/$DEPLOYMENT_NAME -n $NAMESPACE"
fi

# Run health check
log "Running health check..."
kubectl get pods -n "$NAMESPACE" -l app="$DEPLOYMENT_NAME" --field-selector=status.phase!=Running --no-headers | while read -r pod; do
  log "Pod $pod is not Running"
done

# Print next steps
log ""
log "Rollback completed!"
log "Next steps:"
log "  - Check deployment status: kubectl get deployment/$DEPLOYMENT_NAME -n $NAMESPACE"
log "  - View logs: kubectl logs -l app=$DEPLOYMENT_NAME -n $NAMESPACE"
log "  - Check pods: kubectl get pods -n $NAMESPACE -l app=$DEPLOYMENT_NAME"
