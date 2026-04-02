package com.gogidix.rapidassist.shared.idempotency.library.architecture;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class HexArchitectureTest {

    @Test
    void domainShouldNotDependOnAdaptersOrInfrastructure() {
        var classes = new ClassFileImporter().importPackages("com.gogidix.rapidassist.shared.idempotency.library");

        ArchRule rule = noClasses()
                .that().resideInAnyPackage("com.gogidix.rapidassist.shared.idempotency.library.domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "com.gogidix.rapidassist.shared.idempotency.library.adapters..",
                        "com.gogidix.rapidassist.shared.idempotency.library.infrastructure..",
                        "org.springframework.."
                );

        rule.check(classes);
    }
}
