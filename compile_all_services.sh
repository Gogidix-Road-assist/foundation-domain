#!/bin/bash

# Output file for results
RESULTS="/tmp/foundation_compilation_results.txt"

# Initialize counters
TOTAL=0
COMPILE_PASS=0
COMPILE_FAIL=0

echo "=== Foundation-Domain Compilation Check ===" > "$RESULTS"
echo "Started: $(date)" >> "$RESULTS"
echo "" >> "$RESULTS"

# Function to compile a service
compile_service() {
    local service_path="$1"
    local service_name=$(basename "$service_path")
    TOTAL=$((TOTAL + 1))
    
    echo "[$TOTAL] Compiling: $service_name" | tee -a "$RESULTS"
    
    cd "$service_path" 2>/dev/null || {
        echo "  ❌ FAILED: Cannot access directory" | tee -a "$RESULTS"
        COMPILE_FAIL=$((COMPILE_FAIL + 1))
        echo "FAIL|$service_name" >> /tmp/compilation_summary.txt
        return
    }
    
    if mvn clean compile -q -DskipTests 2>&1 | tail -5; then
        echo "  ✅ PASS" | tee -a "$RESULTS"
        COMPILE_PASS=$((COMPILE_PASS + 1))
        echo "PASS|$service_name" >> /tmp/compilation_summary.txt
    else
        echo "  ❌ FAIL" | tee -a "$RESULTS"
        COMPILE_FAIL=$((COMPILE_FAIL + 1))
        echo "FAIL|$service_name" >> /tmp/compilation_summary.txt
    fi
    
    echo "" >> "$RESULTS"
}

# Find all services
echo "" > /tmp/compilation_summary.txt

# ai-services
for dir in ai-services/Backend/Java/*-service ai-services/Backend/Java/*config-service; do
    if [ -d "$dir" ]; then
        compile_service "$(pwd)/$dir"
    fi
done

# central-configuration
for dir in central-configuration/Backend/Java/*-service central-configuration/Backend/Java/*config-service; do
    if [ -d "$dir" ]; then
        compile_service "$(pwd)/$dir"
    fi
done

# centralized-dashboard
for dir in centralized-dashboard/Backend/Java/*-service; do
    if [ -d "$dir" ]; then
        compile_service "$(pwd)/$dir"
    fi
done

# orchestration-services
for dir in orchestration-services/Backend/Java/*-service; do
    if [ -d "$dir" ]; then
        compile_service "$(pwd)/$dir"
    fi
done

# shared-infrastructure
for dir in shared-infrastructure/Backend/Java/*-service; do
    if [ -d "$dir" ]; then
        compile_service "$(pwd)/$dir"
    fi
done

# Summary
echo "" >> "$RESULTS"
echo "=== SUMMARY ===" >> "$RESULTS"
echo "Total Services: $TOTAL" >> "$RESULTS"
echo "Passed: $COMPILE_PASS" >> "$RESULTS"
echo "Failed: $COMPILE_FAIL" >> "$RESULTS"
echo "Success Rate: $(echo "scale=1; $COMPILE_PASS * 100 / $TOTAL" | bc)%" >> "$RESULTS"
echo "Ended: $(date)" >> "$RESULTS"

echo ""
echo "=== FINAL SUMMARY ==="
echo "Total: $TOTAL | Passed: $COMPILE_PASS | Failed: $COMPILE_FAIL"
echo ""
echo "Failed services:"
grep "^FAIL" /tmp/compilation_summary.txt | cut -d'|' -f2
