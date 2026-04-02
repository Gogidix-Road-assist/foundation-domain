package com.gogidix.rapidassist.release.rollout.config.service.architecture;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class HexArchitectureTest {

    @Test
    void domainShouldNotDependOnAdaptersOrInfrastructure() {
        var classes = new ClassFileImporter().importPackages("com.gogidix.rapidassist.release.rollout.config.service");

        ArchRule rule = noClasses()
                .that().resideInAnyPackage("com.gogidix.rapidassist.release.rollout.config.service.domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "com.gogidix.rapidassist.release.rollout.config.service.adapters..",
                        "com.gogidix.rapidassist.release.rollout.config.service.infrastructure..",
                        "org.springframework.."
                );

        rule.check(classes);
    }
}
