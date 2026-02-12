package com.gogidix.rapidassist.currency.converter.service.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.DisplayName;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.gogidix.rapidassist.currency.converter.service",
        importOptions = {ImportOption.DoNotIncludeTests.class})
@DisplayName("Hexagonal Architecture Tests")
class HexArchitectureTest {

    @ArchTest
    static final ArchRule domain_layer_should_not_depend_on_other_layers = noClasses()
            .that().resideInAnyPackage("com.gogidix.rapidassist.currency.converter.service.domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "com.gogidix.rapidassist.currency.converter.service.adapters..",
                    "com.gogidix.rapidassist.currency.converter.service.infrastructure..",
                    "org.springframework.."
            );
}
