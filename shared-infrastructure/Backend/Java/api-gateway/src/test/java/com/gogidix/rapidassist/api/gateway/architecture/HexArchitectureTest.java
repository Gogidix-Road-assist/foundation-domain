package com.gogidix.rapidassist.api.gateway.architecture;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class HexArchitectureTest {

    @Test
    void domainShouldNotDependOnAdaptersOrInfrastructure() {
        var classes = new ClassFileImporter().importPackages("com.gogidix.rapidassist.api.gateway");

        ArchRule rule = noClasses()
                .that().resideInAnyPackage("com.gogidix.rapidassist.api.gateway.domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "com.gogidix.rapidassist.api.gateway.adapters..",
                        "com.gogidix.rapidassist.api.gateway.infrastructure..");

        rule.check(classes);
    }
}
