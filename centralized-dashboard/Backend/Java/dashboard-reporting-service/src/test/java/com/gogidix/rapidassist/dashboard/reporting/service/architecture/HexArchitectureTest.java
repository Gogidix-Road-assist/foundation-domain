package com.gogidix.rapidassist.dashboard.reporting.service.architecture;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class HexArchitectureTest {

    @Test
    void domainShouldNotDependOnAdaptersOrInfrastructure() {
        var classes = new ClassFileImporter().importPackages("com.gogidix.rapidassist.dashboard.reporting.service");

        ArchRule rule = noClasses()
                .that().resideInAnyPackage("com.gogidix.rapidassist.dashboard.reporting.service.domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "com.gogidix.rapidassist.dashboard.reporting.service.adapters..",
                        "com.gogidix.rapidassist.dashboard.reporting.service.infrastructure..",
                        "org.springframework.."
                );

        rule.check(classes);
    }
}
