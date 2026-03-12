#!/usr/bin/env python3
"""
Update pom.xml files with Financial-Grade JaCoCo and PIT configurations.
"""

import os
import re
from pathlib import Path
from xml.etree import ElementTree as ET

# Base directory
BASE_DIR = Path(__file__).parent.parent / "Backend" / "Java"

# PIT configuration template (as XML string)
PIT_CONFIG = """            <!-- PIT Mutation Testing - Financial-Grade Testing Standard -->
            <plugin>
                <groupId>org.pitest</groupId>
                <artifactId>pitest-maven</artifactId>
                <version>1.15.3</version>
                <dependencies>
                    <dependency>
                        <groupId>org.pitest</groupId>
                        <artifactId>pitest-junit5-plugin</artifactId>
                        <version>1.2.1</version>
                    </dependency>
                </dependencies>
                <executions>
                    <execution>
                        <id>mutation-coverage</id>
                        <phase>test</phase>
                        <goals>
                            <goal>mutationCoverage</goal>
                        </goals>
                        <configuration>
                            <targetClasses>
                                <param>com.gogidix.rapidassist.ai.{package}.domain.model.*</param>
                                <param>com.gogidix.rapidassist.ai.{package}.domain.logic.*</param>
                                <param>com.gogidix.rapidassist.ai.{package}.application.service.*</param>
                                <param>com.gogidix.rapidassist.ai.{package}.infrastructure.persistence.repository.*</param>
                            </targetClasses>
                            <targetTests>
                                <param>com.gogidix.rapidassist.ai.{package}..*</param>
                            </targetTests>
                            <excludedClasses>
                                <param>.*Test.*</param>
                                <param>.*Dto</param>
                                <param>.*Entity</param>
                                <param>.*Config</param>
                                <param>.*Mapper</param>
                                <param>.*Application$</param>
                                <param>.*Enum</param>
                            </excludedClasses>
                            <excludedMethods>
                                <param>hashCode</param>
                                <param>equals</param>
                                <param>toString</param>
                            </excludedMethods>
                            <mutationThreshold>60</mutationThreshold>
                            <coverageThreshold>85</coverageThreshold>
                            <outputFormats>
                                <param>HTML</param>
                                <param>XML</param>
                            </outputFormats>
                            <timestampedReports>true</timestampedReports>
                            <threads>4</threads>
                            <detectInlinedCode>true</detectInlinedCode>
                            <withHistory>true</withHistory>
                        </configuration>
                    </execution>
                </executions>
            </plugin>"""

# Enhanced JaCoCo configuration
JACOCO_RULES = """
                                <!-- Overall Project Coverage - 85% Minimum -->
                                <rule>
                                    <element>BUNDLE</element>
                                    <limits>
                                        <limit>
                                            <counter>LINE</counter>
                                            <value>COVEREDRATIO</value>
                                            <minimum>0.850</minimum>
                                        </limit>
                                        <limit>
                                            <counter>BRANCH</counter>
                                            <value>COVEREDRATIO</value>
                                            <minimum>0.750</minimum>
                                        </limit>
                                    </limits>
                                </rule>

                                <!-- Domain Package - 90% Line, 85% Branch -->
                                <rule>
                                    <element>PACKAGE</element>
                                    <include>com.gogidix.rapidassist.ai.{package}.domain</include>
                                    <limits>
                                        <limit>
                                            <counter>LINE</counter>
                                            <value>COVEREDRATIO</value>
                                            <minimum>0.900</minimum>
                                        </limit>
                                        <limit>
                                            <counter>BRANCH</counter>
                                            <value>COVEREDRATIO</value>
                                            <minimum>0.850</minimum>
                                        </limit>
                                    </limits>
                                </rule>

                                <!-- Domain Model - 95% Line, 90% Branch (Critical Business Logic) -->
                                <rule>
                                    <element>PACKAGE</element>
                                    <include>com.gogidix.rapidassist.ai.{package}.domain.model</include>
                                    <limits>
                                        <limit>
                                            <counter>LINE</counter>
                                            <value>COVEREDRATIO</value>
                                            <minimum>0.950</minimum>
                                        </limit>
                                        <limit>
                                            <counter>BRANCH</counter>
                                            <value>COVEREDRATIO</value>
                                            <minimum>0.900</minimum>
                                        </limit>
                                    </limits>
                                </rule>

                                <!-- Application Service Package - 90% Line, 85% Branch -->
                                <rule>
                                    <element>PACKAGE</element>
                                    <include>com.gogidix.rapidassist.ai.{package}.application</include>
                                    <limits>
                                        <limit>
                                            <counter>LINE</counter>
                                            <value>COVEREDRATIO</value>
                                            <minimum>0.900</minimum>
                                        </limit>
                                        <limit>
                                            <counter>BRANCH</counter>
                                            <value>COVEREDRATIO</value>
                                            <minimum>0.850</minimum>
                                        </limit>
                                    </limits>
                                </rule>

                                <!-- Infrastructure Package - 75% Line, 70% Branch -->
                                <rule>
                                    <element>PACKAGE</element>
                                    <include>com.gogidix.rapidassist.ai.{package}.infrastructure</include>
                                    <limits>
                                        <limit>
                                            <counter>LINE</counter>
                                            <value>COVEREDRATIO</value>
                                            <minimum>0.750</minimum>
                                        </limit>
                                        <limit>
                                            <counter>BRANCH</counter>
                                            <value>COVEREDRATIO</value>
                                            <minimum>0.700</minimum>
                                        </limit>
                                    </limits>
                                </rule>

                                <!-- Interfaces Package - 75% Line, 70% Branch -->
                                <rule>
                                    <element>PACKAGE</element>
                                    <include>com.gogidix.rapidassist.ai.{package}.interfaces</include>
                                    <limits>
                                        <limit>
                                            <counter>LINE</counter>
                                            <value>COVEREDRATIO</value>
                                            <minimum>0.750</minimum>
                                        </limit>
                                        <limit>
                                            <counter>BRANCH</counter>
                                            <value>COVEREDRATIO</value>
                                            <minimum>0.700</minimum>
                                        </limit>
                                    </limits>
                                </rule>"""


def get_package_name(service_name: str) -> str:
    """Extract package name from service name."""
    # ai-fraud-detection-service -> fraud
    # ai-anomaly-detection-service -> anomaly
    name = service_name.replace('ai-', '').replace('-service', '')
    return name.replace('-', '')


def update_pom_file(pom_path: Path, package_name: str) -> bool:
    """Update a pom.xml file with Financial-Grade configurations."""
    content = pom_path.read_text()

    # Check if already updated (has PIT plugin)
    if '<artifactId>pitest-maven</artifactId>' in content:
        return False

    # Update JaCoCo configuration with Financial-Grade rules
    # Find the JaCoCo configuration and replace/add rules
    old_jacoco = r'(<execution>\s*<id>jacoco-check</id>\s*<goals>\s*<goal>check</goal>\s*</goals>\s*<configuration>\s*<rules>)(.*?)(</rules>)'
    new_jacoco = r'\1' + JACOCO_RULES.format(package=package_name) + r'\3'

    # Only update if the simple rule exists
    if re.search(r'<element>PACKAGE</element>\s*<limits>\s*<limit>\s*<counter>LINE</counter>', content):
        content = re.sub(old_jacoco, new_jacoco, content, flags=re.DOTALL)

    # Add PIT plugin after JaCoCo plugin (before </plugins>)
    pit_config = PIT_CONFIG.format(package=package_name)
    content = re.sub(
        r'(</plugin>\s*)(</plugins>)',
        r'\1' + pit_config + r'\n\2',
        content
    )

    # Update surefire plugin to use ${argLine}
    content = re.sub(
        r'<argLine>-Xmx(\d+)m</argLine>',
        r'<argLine>${argLine} -Xmx\1m</argLine>',
        content
    )

    pom_path.write_text(content)
    return True


def main():
    print("=" * 60)
    print("Updating pom.xml files with Financial-Grade Configurations")
    print("=" * 60)
    print()

    services = [
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

    updated_count = 0
    skipped_count = 0

    for service in services:
        pom_path = BASE_DIR / service / "pom.xml"

        if not pom_path.exists():
            print(f"SKIPPED: {service} (pom.xml not found)")
            skipped_count += 1
            continue

        package_name = get_package_name(service)

        if update_pom_file(pom_path, package_name):
            print(f"UPDATED: {service} (package: {package_name})")
            updated_count += 1
        else:
            print(f"ALREADY UPDATED: {service}")
            skipped_count += 1

    print()
    print("=" * 60)
    print("SUMMARY")
    print("=" * 60)
    print(f"Updated: {updated_count}")
    print(f"Skipped: {skipped_count}")
    print("=" * 60)


if __name__ == "__main__":
    main()
