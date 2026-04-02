package com.gogidix.rapidassist.reporting.read.model.service.architecture;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class HexArchitectureTest {

    @Test
    void domainShouldNotDependOnAdaptersOrInfrastructure() {
        var classes = new ClassFileImporter().importPackages("com.gogidix.rapidassist.reporting.read.model.service");

        ArchRule rule = noClasses()
                .that().resideInAnyPackage("com.gogidix.rapidassist.reporting.read.model.service.domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "com.gogidix.rapidassist.reporting.read.model.service.adapters..",
                        "com.gogidix.rapidassist.reporting.read.model.service.infrastructure..",
                        "org.springframework.."
                );

        rule.check(classes);
    }
}
