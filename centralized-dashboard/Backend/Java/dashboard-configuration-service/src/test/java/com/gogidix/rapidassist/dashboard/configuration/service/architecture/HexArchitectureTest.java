package com.gogidix.rapidassist.dashboard.configuration.service.architecture;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class HexArchitectureTest {

    @Test
    void domainShouldNotDependOnAdaptersOrInfrastructure() {
        var classes = new ClassFileImporter().importPackages("com.gogidix.rapidassist.dashboard.configuration.service");

        ArchRule rule = noClasses()
                .that().resideInAnyPackage("com.gogidix.rapidassist.dashboard.configuration.service.domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "com.gogidix.rapidassist.dashboard.configuration.service.adapters..",
                        "com.gogidix.rapidassist.dashboard.configuration.service.infrastructure..",
                        "org.springframework.."
                );

        rule.check(classes);
    }
}
