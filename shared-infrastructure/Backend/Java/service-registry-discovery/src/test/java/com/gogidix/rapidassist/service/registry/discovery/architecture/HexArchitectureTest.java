package com.gogidix.rapidassist.service.registry.discovery.architecture;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class HexArchitectureTest {

    @Test
    void domainShouldNotDependOnAdaptersOrInfrastructure() {
        var classes = new ClassFileImporter().importPackages("com.gogidix.rapidassist.service.registry.discovery");

        ArchRule rule = noClasses()
                .that().resideInAnyPackage("com.gogidix.rapidassist.service.registry.discovery.domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "com.gogidix.rapidassist.service.registry.discovery.adapters..",
                        "com.gogidix.rapidassist.service.registry.discovery.infrastructure..");

        rule.check(classes);
    }
}
