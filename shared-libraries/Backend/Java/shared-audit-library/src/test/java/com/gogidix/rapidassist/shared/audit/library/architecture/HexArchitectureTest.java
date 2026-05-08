package com.gogidix.rapidassist.shared.audit.library.architecture;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class HexArchitectureTest {

    @Test
    void domainShouldNotDependOnAdaptersOrInfrastructure() {
        var classes = new ClassFileImporter().importPackages("com.gogidix.rapidassist.shared.audit.library");

        ArchRule rule = noClasses()
                .that().resideInAnyPackage("com.gogidix.rapidassist.shared.audit.library.domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "com.gogidix.rapidassist.shared.audit.library.adapters..",
                        "com.gogidix.rapidassist.shared.audit.library.infrastructure..",
                        "org.springframework.."
                );

        rule.check(classes);
    }
}
