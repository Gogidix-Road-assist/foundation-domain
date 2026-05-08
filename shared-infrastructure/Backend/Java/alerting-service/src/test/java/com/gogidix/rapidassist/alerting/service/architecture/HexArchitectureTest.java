package com.gogidix.rapidassist.alerting.service.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.gogidix.rapidassist.alerting.service",
        importOptions = {ImportOption.DoNotIncludeTests.class})
class HexArchitectureTest {

    @ArchTest
    static final ArchRule domain_layer_should_not_depend_on_other_layers = noClasses()
            .that().resideInAnyPackage("com.gogidix.rapidassist.alerting.service.domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "com.gogidix.rapidassist.alerting.service.adapters..",
                    "com.gogidix.rapidassist.alerting.service.infrastructure..",
                    "org.springframework.."
            );
}
