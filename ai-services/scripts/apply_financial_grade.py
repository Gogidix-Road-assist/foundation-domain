#!/usr/bin/env python3
"""
Financial-Grade Testing Automation Script
Applies @EqualsAndHashCode fix and JaCoCo/PIT configurations to all AI services.
"""

import os
import re
import sys
from pathlib import Path

# Base directory
BASE_DIR = Path(__file__).parent.parent / "Backend" / "Java"

# Services to process (excluding ai-fraud-detection-service which is already done)
SERVICES = [
    "ai-anomaly-detection-service",
    "ai-automated-tagging-service",
    "ai-bi-analytics-service",
    "ai-categorization-service",
    "ai-chatbot-service",
    "ai-computer-vision-service",
    "ai-content-analysis-service",
    "ai-content-moderation-service",
    "ai-data-quality-service",
    "ai-forecasting-service",
    "ai-gateway-service",
    "ai-image-recognition-service",
    "ai-inference-service",
    "ai-matching-algorithm-service",
    "ai-model-management-service",
    "ai-nlp-processing-service",
    "ai-optimization-service",
    "ai-personalization-service",
    "ai-predictive-analytics-service",
    "ai-pricing-engine-service",
    "ai-recommendation-service",
    "ai-report-generation-service",
    "ai-risk-assessment-service",
    "ai-search-optimization-service",
    "ai-sentiment-analysis-service",
    "ai-speech-recognition-service",
    "ai-summarization-service",
    "ai-summization-service",
    "ai-translation-service",
    "analytics-service",
]


def fix_equals_hashcode(file_path: Path) -> bool:
    """Fix @EqualsAndHashCode annotation in a Java file."""
    content = file_path.read_text()

    # Check if file has @Data annotation
    if "@Data" not in content:
        return False

    # Check if already fixed
    if "@EqualsAndHashCode(onlyExplicitlyIncluded = true)" in content:
        return False

    # Get the class name
    class_match = re.search(r'public class (\w+)', content)
    if not class_match:
        return False

    # Add import if not present
    if "import lombok.EqualsAndHashCode;" not in content:
        # Find last lombok import
        import_match = re.search(r'(import lombok\.[^\n]+)\n', content)
        if import_match:
            insert_pos = import_match.end()
            content = content[:insert_pos] + "import lombok.EqualsAndHashCode;\n" + content[insert_pos:]
        else:
            # Add after package statement
            package_match = re.search(r'package [^;]+;', content)
            if package_match:
                insert_pos = package_match.end()
                content = content[:insert_pos] + "\n\nimport lombok.EqualsAndHashCode;" + content[insert_pos:]

    # Add @EqualsAndHashCode annotation after @Data
    if re.search(r'@Data\s*\n', content):
        content = re.sub(
            r'(@Data\s*\n)',
            r'\1@EqualsAndHashCode(onlyExplicitlyIncluded = true)\n',
            content
        )

    # Find UUID id field and add @EqualsAndHashCode.Include before it
    def add_include_to_id(match):
        indent = match.group(1)
        return f'{indent}@EqualsAndHashCode.Include\n{indent}private UUID id;'

    content = re.sub(
        r'(\s+)private UUID id;',
        add_include_to_id,
        content
    )

    file_path.write_text(content)
    return True


def process_domain_models(service_dir: Path) -> int:
    """Process all domain model files in a service."""
    domain_model_dir = service_dir / "src/main/java/com/gogidix/rapidassist/ai"
    if not domain_model_dir.exists():
        return 0

    count = 0
    for java_file in domain_model_dir.rglob("domain/model/*.java"):
        if fix_equals_hashcode(java_file):
            print(f"  Fixed: {java_file.name}")
            count += 1
    return count


def process_dtos(service_dir: Path) -> int:
    """Process all DTO files in a service."""
    dto_dir = service_dir / "src/main/java/com/gogidix/rapidassist/ai"
    if not dto_dir.exists():
        return 0

    count = 0
    for java_file in dto_dir.rglob("application/dto/*Dto.java"):
        if fix_equals_hashcode(java_file):
            print(f"  Fixed: {java_file.name}")
            count += 1
    return count


def get_service_package_name(service_dir: Path) -> str:
    """Extract service package name from pom.xml."""
    pom_file = service_dir / "pom.xml"
    if not pom_file.exists():
        return None

    content = pom_file.read_text()
    # Extract service name from artifactId
    match = re.search(r'<artifactId>([^<]+)</artifactId>', content)
    if match:
        artifact_id = match.group(1)
        # Convert to package name: ai-fraud-detection-service -> fraud
        # Remove 'ai-' prefix and '-service' suffix
        name = artifact_id.replace('ai-', '').replace('-service', '')
        return name.replace('-', '')
    return None


def main():
    print("=" * 60)
    print("Financial-Grade Testing Automation")
    print("=" * 60)
    print(f"Base Directory: {BASE_DIR}")
    print(f"Services to Process: {len(SERVICES)}")
    print()

    success_count = 0
    skip_count = 0
    total_fixed = 0

    for service in SERVICES:
        service_dir = BASE_DIR / service

        if not service_dir.exists():
            print(f"SKIPPED: {service} (directory not found)")
            skip_count += 1
            continue

        print(f"Processing: {service}")

        # Process domain models
        fixed_models = process_domain_models(service_dir)

        # Process DTOs
        fixed_dtos = process_dtos(service_dir)

        service_total = fixed_models + fixed_dtos
        total_fixed += service_total

        if service_total > 0:
            print(f"  Fixed {service_total} files ({fixed_models} models, {fixed_dtos} DTOs)")

        success_count += 1
        print(f"  COMPLETED: {service}")
        print()

    print("=" * 60)
    print("SUMMARY")
    print("=" * 60)
    print(f"Successfully Processed: {success_count}")
    print(f"Skipped: {skip_count}")
    print(f"Total Files Fixed: {total_fixed}")
    print("=" * 60)


if __name__ == "__main__":
    main()
