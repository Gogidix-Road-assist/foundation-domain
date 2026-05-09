#!/bin/bash
# Foundation-Domain Kubernetes Deployment Script
# Usage: ./deploy.sh [environment]
# Environments: dev, staging, production

set -e

# Configuration
ENVIRONMENT=${1:-dev}
NAMESPACE="foundation"
REGION="us-east-1"
CLUSTER_NAME="foundation-cluster"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Functions
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

check_kubectl() {
    if ! command -v kubectl &> /dev/null; then
        log_error "kubectl not found. Please install kubectl."
        exit 1
    fi
    log_info "kubectl found: $(kubectl version --client --short)"
}

check_cluster() {
    if ! kubectl cluster-info &> /dev/null; then
        log_error "Cannot connect to Kubernetes cluster. Please check your kubeconfig."
        exit 1
    fi
    log_info "Connected to cluster: $(kubectl config current-context)"
}

create_namespace() {
    log_info "Creating namespace: $NAMESPACE"
    kubectl apply -f k8s/namespace.yaml
}

deploy_infrastructure() {
    log_info "Deploying infrastructure (MongoDB, PostgreSQL, Redis)..."
    kubectl apply -f k8s/infrastructure.yaml

    log_info "Waiting for databases to be ready..."
    kubectl wait --for=condition=ready pod -l app=mongodb -n $NAMESPACE --timeout=300s || true
    kubectl wait --for=condition=ready pod -l app=postgres -n $NAMESPACE --timeout=300s || true
    kubectl wait --for=condition=ready pod -l app=redis -n $NAMESPACE --timeout=300s || true
}

deploy_core_services() {
    log_info "Deploying core services..."
    kubectl apply -f k8s/core-services.yaml

    log_info "Waiting for core services to be ready..."
    kubectl wait --for=condition=available deployment/api-gateway -n $NAMESPACE --timeout=300s || true
    kubectl wait --for=condition=available deployment/identity-service -n $NAMESPACE --timeout=300s || true
    kubectl wait --for=condition=available deployment/notification-service -n $NAMESPACE --timeout=300s || true
    kubectl wait --for=condition=available deployment/service-registry -n $NAMESPACE --timeout=300s || true
}

deploy_ai_services() {
    log_info "Deploying AI services..."
    kubectl apply -f k8s/ai-services.yaml

    log_info "Waiting for AI services to be ready..."
    kubectl wait --for=condition=available deployment/ai-gateway -n $NAMESPACE --timeout=300s || true
    kubectl wait --for=condition=available deployment/ai-inference -n $NAMESPACE --timeout=300s || true
    kubectl wait --for=condition=available deployment/ai-model-management -n $NAMESPACE --timeout=300s || true
}

deploy_monitoring() {
    log_info "Deploying monitoring stack..."
    kubectl apply -f k8s/monitoring.yaml

    log_info "Waiting for monitoring services to be ready..."
    kubectl wait --for=condition=available deployment/prometheus -n $NAMESPACE --timeout=300s || true
    kubectl wait --for=condition=available deployment/grafana -n $NAMESPACE --timeout=300s || true
    kubectl wait --for=condition=available deployment/alertmanager -n $NAMESPACE --timeout=300s || true
}

deploy_network_policies() {
    log_info "Deploying network policies..."
    kubectl apply -f k8s/network-policy.yaml
}

deploy_autoscaler() {
    log_info "Deploying HorizontalPodAutoscaler..."
    kubectl apply -f k8s/autoscaler.yaml
}

deploy_ingress() {
    log_info "Deploying ingress..."
    kubectl apply -f k8s/ingress.yaml
}

show_status() {
    log_info "Deployment status:"
    echo ""
    kubectl get all -n $NAMESPACE
    echo ""
    log_info "Service endpoints:"
    kubectl get svc -n $NAMESPACE
    echo ""
    log_info "Grafana URL: http://grafana.gogidix.com (admin/foundation123)"
    log_info "Prometheus URL: http://prometheus.gogidix.com"
    log_info "API Gateway URL: https://api.gogidix.com"
}

main() {
    log_info "Starting Foundation-Domain deployment to $ENVIRONMENT..."
    echo ""

    check_kubectl
    check_cluster
    create_namespace
    deploy_infrastructure
    deploy_core_services
    deploy_ai_services
    deploy_monitoring
    deploy_network_policies
    deploy_autoscaler
    deploy_ingress
    show_status

    log_info "Deployment completed successfully!"
}

# Run main function
main "$@"
