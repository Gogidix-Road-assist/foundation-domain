#!/bin/bash

###############################################################################
# PR Risk Scoring Script - Financial-Grade Testing Standard
#
# Calculates deployment risk score based on:
# - Files changed in critical packages
# - Coverage delta
# - Mutation score delta
# - Historical failure patterns
#
# Usage: ./pr-risk-score.sh [base_branch] [target_branch]
# Output:
#   RISK_SCORE={0-100}
#   RISK_LEVEL={SAFE|GUARDED|HIGH_RISK|CRITICAL}
#   BLOCK_DEPLOY={true|false}
###############################################################################

set -euo pipefail

# Default values
BASE_BRANCH="${1:-main}"
TARGET_BRANCH="${2:-HEAD}"
SERVICE_NAME="ai-fraud-detection-service"

# Risk thresholds
CRITICAL_THRESHOLD=85
HIGH_RISK_THRESHOLD=65
GUARDED_THRESHOLD=40

# Coverage thresholds
MIN_LINE_COVERAGE=85
MIN_BRANCH_COVERAGE=75
MIN_MUTATION_SCORE=60

# Critical package patterns
CRITICAL_PACKAGES=(
    "domain/model"
    "domain/repository"
    "domain/service"
    "application/service"
    "domain/tenant"
)

# Output colors
RED='\033[0;31m'
YELLOW='\033[1;33m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color

###############################################################################
# Helper Functions
###############################################################################

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Get list of changed files
get_changed_files() {
    git diff --name-only "${BASE_BRANCH}...${TARGET_BRANCH}" 2>/dev/null || echo ""
}

# Check if file is in critical package
is_critical_file() {
    local file="$1"
    for pattern in "${CRITICAL_PACKAGES[@]}"; do
        if echo "$file" | grep -q "$pattern"; then
            return 0
        fi
    done
    return 1
}

# Count files by type
count_files_by_type() {
    local files="$1"
    local type="$2"

    echo "$files" | grep -c "\\.$type$" 2>/dev/null || echo "0"
}

# Get test coverage from JaCoCo report
get_coverage() {
    local report_file="$1"

    if [[ ! -f "$report_file" ]]; then
        echo "0"
        return
    fi

    # Extract line coverage from XML report
    local coverage=$(xmlstarlet sel -t -v "//counter[@type='LINE']/@coveredratio" "$report_file" 2>/dev/null || echo "0")

    # Convert to percentage
    echo "$(awk "BEGIN {printf \"%.0f\", $coverage * 100}")"
}

# Get mutation score from PIT report
get_mutation_score() {
    local report_file="$1"

    if [[ ! -f "$report_file" ]]; then
        echo "0"
        return
    fi

    # Extract mutation score from XML report
    local score=$(xmlstarlet sel -t -v "//mutations/@mutationScore" "$report_file" 2>/dev/null || echo "0")

    echo "$score"
}

# Calculate risk score component
calculate_component_score() {
    local name="$1"
    local weight="$2"
    local value="$3"
    local max_value="${4:-100}"

    if [[ $max_value -eq 0 ]]; then
        echo "0"
        return
    fi

    local ratio=$(awk "BEGIN {printf \"%.2f\", ($value / $max_value) * 100}")
    local weighted=$(awk "BEGIN {printf \"%.0f\", $ratio * $weight / 100}")

    log_info "  - $name: $value/$max_value = $ratio% (weight: $weight%) = $weighted points"

    echo "$weighted"
}

###############################################################################
# Main Scoring Logic
###############################################################################

calculate_pr_risk_score() {
    log_info "Calculating PR risk score for ${SERVICE_NAME}"
    log_info "Comparing ${BASE_BRANCH}...${TARGET_BRANCH}"
    echo ""

    # Get changed files
    local changed_files=$(get_changed_files)

    if [[ -z "$changed_files" ]]; then
        log_warning "No changed files detected"
        echo "RISK_SCORE=0"
        echo "RISK_LEVEL=SAFE"
        echo "BLOCK_DEPLOY=false"
        return
    fi

    local total_files=$(echo "$changed_files" | wc -l)
    log_info "Total files changed: $total_files"
    echo ""

    # Initialize scores
    local total_score=0

    # 1. Critical Package Impact (30% weight)
    log_info "Evaluating Critical Package Impact (30% weight):"
    local critical_files=0
    local test_files=0

    while IFS= read -r file; do
        if is_critical_file "$file"; then
            ((critical_files++))
            log_warning "  - Critical file changed: $file"
        elif echo "$file" | grep -q "test"; then
            ((test_files++))
        fi
    done <<< "$changed_files"

    local critical_score=$(calculate_component_score "Critical Files" 30 $critical_files $total_files)
    total_score=$((total_score + critical_score))
    echo ""

    # 2. Production vs Test File Ratio (20% weight)
    log_info "Evaluating Production vs Test File Ratio (20% weight):"
    local prod_files=$((total_files - test_files))
    local test_ratio=0
    if [[ $prod_files -gt 0 ]]; then
        test_ratio=$(awk "BEGIN {printf \"%.0f\", ($test_files / $prod_files) * 100}")
    fi

    # Lower risk if tests are added/modified
    local ratio_score=$(awk "BEGIN {printf \"%.0f\", (100 - $test_ratio) * 20 / 100}")
    log_info "  - Test ratio: $test_files/$prod_files = $test_ratio%"
    log_info "  - Ratio score: $ratio_score points"
    total_score=$((total_score + ratio_score))
    echo ""

    # 3. Coverage Delta (25% weight)
    log_info "Evaluating Coverage Delta (25% weight):"

    # Check for existing coverage reports
    local base_report="target/site/jacoco/jacoco.xml"
    local current_coverage=$(get_coverage "$base_report")
    local base_coverage="${BASE_COVERAGE:-0}"

    local coverage_delta=$((current_coverage - base_coverage))
    local coverage_impact=0

    if [[ $current_coverage -lt $MIN_LINE_COVERAGE ]]; then
        coverage_impact=25
        log_error "  - Current coverage ($current_coverage%) below minimum ($MIN_LINE_COVERAGE%)"
    elif [[ $coverage_delta -lt -5 ]]; then
        coverage_impact=20
        log_error "  - Coverage decreased by ${coverage_delta}%"
    elif [[ $coverage_delta -lt 0 ]]; then
        coverage_impact=10
        log_warning "  - Coverage decreased by ${coverage_delta}%"
    else
        coverage_impact=0
        log_info "  - Coverage: $current_coverage% (delta: +${coverage_delta}%)"
    fi

    total_score=$((total_score + coverage_impact))
    echo ""

    # 4. File Type Risk (15% weight)
    log_info "Evaluating File Type Risk (15% weight):"
    local java_files=$(count_files_by_type "$changed_files" "java")
    local xml_files=$(count_files_by_type "$changed_files" "xml")
    local json_files=$(count_files_by_type "$changed_files" "json")
    local yaml_files=$(count_files_by_type "$changed_files" "yml") + $(count_files_by_type "$changed_files" "yaml")

    # Java and XML files are higher risk (business logic and config)
    local file_type_score=$(( (java_files + xml_files) * 15 / (total_files + 1) ))
    log_info "  - Java files: $java_files"
    log_info "  - XML files: $xml_files"
    log_info "  - Config files: $((yaml_files))"
    log_info "  - File type score: $file_type_score points"

    total_score=$((total_score + file_type_score))
    echo ""

    # 5. Historical Failure Pattern (10% weight)
    log_info "Evaluating Historical Failure Pattern (10% weight):"
    local failure_score=0

    # Check for recent CI failures (mock logic - in real implementation, query CI API)
    if git log --oneline "${BASE_BRANCH}...${TARGET_BRANCH}" | grep -q "fix.*test"; then
        failure_score=10
        log_warning "  - Test fixes detected in PR"
    else
        failure_score=0
        log_info "  - No recent test failure patterns"
    fi

    total_score=$((total_score + failure_score))
    echo ""

    # Final risk score
    log_info "========================================="
    log_info "FINAL RISK SCORE: $total_score / 100"
    log_info "========================================="
    echo ""

    # Determine risk level
    local risk_level="SAFE"
    local block_deploy="false"

    if [[ $total_score -ge $CRITICAL_THRESHOLD ]]; then
        risk_level="CRITICAL"
        block_deploy="true"
        log_error "RISK LEVEL: CRITICAL - Deployment BLOCKED"
    elif [[ $total_score -ge $HIGH_RISK_THRESHOLD ]]; then
        risk_level="HIGH_RISK"
        block_deploy="true"
        log_error "RISK LEVEL: HIGH_RISK - Deployment BLOCKED"
    elif [[ $total_score -ge $GUARDED_THRESHOLD ]]; then
        risk_level="GUARDED"
        block_deploy="false"
        log_warning "RISK LEVEL: GUARDED - Manual review recommended"
    else
        risk_level="SAFE"
        block_deploy="false"
        log_info "RISK LEVEL: SAFE - Deployment can proceed"
    fi

    # Output for CI/CD consumption
    echo ""
    echo "RISK_SCORE=$total_score"
    echo "RISK_LEVEL=$risk_level"
    echo "BLOCK_DEPLOY=$block_deploy"

    # Return exit code based on block_deploy
    if [[ "$block_deploy" == "true" ]]; then
        return 1
    fi

    return 0
}

###############################################################################
# Script Entry Point
###############################################################################

main() {
    log_info "PR Risk Scoring - ${SERVICE_NAME}"
    log_info "========================================"
    echo ""

    # Check if we're in a git repository
    if ! git rev-parse --git-dir > /dev/null 2>&1; then
        log_error "Not a git repository"
        exit 1
    fi

    # Check required tools
    for tool in git awk; do
        if ! command -v "$tool" &> /dev/null; then
            log_error "Required tool not found: $tool"
            exit 1
        fi
    done

    # Optional: xmlstarlet for coverage parsing
    if ! command -v xmlstarlet &> /dev/null; then
        log_warning "xmlstarlet not found - coverage parsing limited"
    fi

    # Run the calculation
    calculate_pr_risk_score
}

# Run main function
main "$@"
