#!/bin/bash
echo "=== UPDATING FOUNDATION-DOMAIN LIBRARY VERSIONS TO 1.0.0 ==="

# Find all pom.xml files and update version from 0.0.1-SNAPSHOT to 1.0.0
find . -name "pom.xml" -type f | while read pom; do
    if grep -q "0.0.1-SNAPSHOT" "$pom"; then
        echo "Updating: $pom"
        sed -i 's/0\.0\.1-SNAPSHOT/1.0.0/g' "$pom"
        echo "  ✅ Updated"
    fi
done

echo ""
echo "=== VERSION UPDATE COMPLETE ==="
echo "All versions changed from 0.0.1-SNAPSHOT to 1.0.0"
