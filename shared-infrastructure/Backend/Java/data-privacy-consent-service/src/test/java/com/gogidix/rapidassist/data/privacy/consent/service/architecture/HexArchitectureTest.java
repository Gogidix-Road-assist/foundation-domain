package com.gogidix.rapidassist.data.privacy.consent.service.architecture;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class HexArchitectureTest {

    @Test
    void domainShouldNotDependOnAdaptersOrInfrastructure() {
        var classes = new ClassFileImporter().importPackages("com.gogidix.rapidassist.data.privacy.consent.service");

        ArchRule rule = noClasses()
                .that().resideInAnyPackage("com.gogidix.rapidassist.data.privacy.consent.service.domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "com.gogidix.rapidassist.data.privacy.consent.service.adapters..",
                        "com.gogidix.rapidassist.data.privacy.consent.service.infrastructure..",
                        "org.springframework.."
                );

        rule.check(classes);
    }
}
