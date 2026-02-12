package com.gogidix.rapidassist.anti.fraud.rules.service.architecture;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class HexArchitectureTest {

    @Test
    void domainShouldNotDependOnAdaptersOrInfrastructure() {
        var classes = new ClassFileImporter().importPackages("com.gogidix.rapidassist.anti.fraud.rules.service");

        ArchRule rule = noClasses()
                .that().resideInAnyPackage("com.gogidix.rapidassist.anti.fraud.rules.service.domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "com.gogidix.rapidassist.anti.fraud.rules.service.adapters..",
                        "com.gogidix.rapidassist.anti.fraud.rules.service.infrastructure..",
                        "org.springframework.."
                );

        rule.check(classes);
    }
}
