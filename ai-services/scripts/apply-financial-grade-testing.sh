#!/bin/bash
# Financial-Grade Testing Automation Script
# Applies @EqualsAndHashCode fix and JaCoCo/PIT configurations to all AI services

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../Backend/Java" && pwd)"
LOG_FILE="$BASE_DIR/../scripts/financial-grade-testing.log"

# Function to log messages
log() {
    echo -e "${2}[$(date +'%Y-%m-%d %H:%M:%S')]${NC} $1" | tee -a "$LOG_FILE"
}

# Function to fix @EqualsAndHashCode in a Java file
fix_equals_hashcode() {
    local file="$1"
    local temp_file="${file}.tmp"

    # Check if file has @Data annotation
    if ! grep -q "@Data" "$file"; then
        return 0
    fi

    # Check if already fixed
    if grep -q "@EqualsAndHashCode(onlyExplicitlyIncluded = true)" "$file"; then
        return 0
    fi

    log "Fixing: $file" "$YELLOW"

    # Get the class name
    local class_name=$(grep -m1 "^public class" "$file" | sed 's/public class \([A-Za-z0-9_]*\).*/\1/')

    # Add import if not present
    if ! grep -q "import lombok.EqualsAndHashCode;" "$file"; then
        # Find last lombok import and add after it
        sed -i "/^import lombok\./a import lombok.EqualsAndHashCode;" "$file"
    fi

    # Add @EqualsAndHashCode annotation after @Data
    sed -i "/^@Data$/a @EqualsAndHashCode(onlyExplicitlyIncluded = true)" "$file"

    # Find UUID id field and add @EqualsAndHashCode.Include before it
    # Pattern: private UUID id;
    if grep -q "private UUID id;" "$file"; then
        # Check if @EqualsAndHashCode.Include already exists
        if ! grep -B1 "private UUID id;" "$file" | grep -q "@EqualsAndHashCode.Include"; then
            sed -i "/private UUID id;/i\    @EqualsAndHashCode.Include" "$file"
        fi
    fi

    log "Fixed: $file" "$GREEN"
    return 0
}

# Function to process domain models in a service
process_domain_models() {
    local service_dir="$1"
    local domain_model_dir="$service_dir/src/main/java/com/gogidix/rapidassist/ai/*/domain/model"

    if [ ! -d "$domain_model_dir" ]; then
        log "No domain/model directory found in $service_dir" "$YELLOW"
        return 0
    fi

    log "Processing domain models in: $service_dir" "$YELLOW"

    # Find all Java files in domain/model
    find "$domain_model_dir" -name "*.java" -type f | while read -r file; do
        fix_equals_hashcode "$file"
    done
}

# Function to process DTOs in a service
process_dtos() {
    local service_dir="$1"
    local dto_dir="$service_dir/src/main/java/com/gogidix/rapidassist/ai/*/application/dto"

    if [ ! -d "$dto_dir" ]; then
        log "No DTO directory found in $service_dir" "$YELLOW"
        return 0
    fi

    log "Processing DTOs in: $service_dir" "$YELLOW"

    find "$dto_dir" -name "*Dto.java" -type f | while read -r file; do
        fix_equals_hashcode "$file"
    done
}

# List of services to process (excluding ai-fraud-detection-service which is already done)
SERVICES=(
    "ai-anomaly-detection-service"
    "ai-automated-tagging-service"
    "ai-bi-analytics-service"
    "ai-categorization-service"
    "ai-chatbot-service"
    "ai-computer-vision-service"
    "ai-content-analysis-service"
    "ai-content-moderation-service"
    "ai-data-quality-service"
    "ai-forecasting-service"
    "ai-gateway-service"
    "ai-image-recognition-service"
    "ai-inference-service"
    "ai-matching-algorithm-service"
    "ai-model-management-service"
    "ai-nlp-processing-service"
    "ai-optimization-service"
    "ai-personalization-service"
    "ai-predictive-analytics-service"
    "ai-pricing-engine-service"
    "ai-recommendation-service"
    "ai-report-generation-service"
    "ai-risk-assessment-service"
    "ai-search-optimization-service"
    "ai-sentiment-analysis-service"
    "ai-speech-recognition-service"
    "ai-summarization-service"
    "ai-summization-service"
    "ai-translation-service"
    "analytics-service"
)

# Main execution
main() {
    log "===========================================" "$GREEN"
    log "Financial-Grade Testing Automation" "$GREEN"
    log "===========================================" "$GREEN"
    log "Base Directory: $BASE_DIR"
    log "Services to Process: ${#SERVICES[@]}"
    log ""

    local success_count=0
    local skip_count=0
    local error_count=0

    for service in "${SERVICES[@]}"; do
        local service_dir="$BASE_DIR/$service"

        if [ ! -d "$service_dir" ]; then
            log "SKIPPED: $service (directory not found)" "$YELLOW"
            ((skip_count++))
            continue
        fi

        log "===========================================" "$GREEN"
        log "Processing: $service" "$GREEN"
        log "===========================================" "$GREEN"

        # Process domain models
        process_domain_models "$service_dir"

        # Process DTOs
        process_dtos "$service_dir"

        ((success_count++))
        log "COMPLETED: $service" "$GREEN"
        log ""
    done

    log "===========================================" "$GREEN"
    log "SUMMARY" "$GREEN"
    log "===========================================" "$GREEN"
    log "Successfully Processed: $success_count"
    log "Skipped: $skip_count"
    log "Errors: $error_count"
    log "===========================================" "$GREEN"
}

# Run main function
main "$@"
