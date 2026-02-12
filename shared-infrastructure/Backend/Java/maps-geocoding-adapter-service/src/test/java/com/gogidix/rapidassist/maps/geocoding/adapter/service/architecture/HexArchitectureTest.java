package com.gogidix.rapidassist.maps.geocoding.adapter.service.architecture;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class HexArchitectureTest {

    @Test
    void domainShouldNotDependOnAdaptersOrInfrastructure() {
        var classes = new ClassFileImporter().importPackages("com.gogidix.rapidassist.maps.geocoding.adapter.service");

        ArchRule rule = noClasses()
                .that().resideInAnyPackage("com.gogidix.rapidassist.maps.geocoding.adapter.service.domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "com.gogidix.rapidassist.maps.geocoding.adapter.service.adapters..",
                        "com.gogidix.rapidassist.maps.geocoding.adapter.service.infrastructure..",
                        "org.springframework.."
                );

        rule.check(classes);
    }
}
