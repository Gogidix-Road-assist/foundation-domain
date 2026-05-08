package com.gogidix.rapidassist.idempotency.service.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

@AnalyzeClasses(packages = "com.gogidix.rapidassist.idempotency.service",
        importOptions = {ImportOption.DoNotIncludeTests.class})
class HexArchitectureTest {

    @ArchTest
    static final ArchRule domain_layer_should_not_depend_on_other_layers = noClasses()
            .that().resideInAnyPackage("com.gogidix.rapidassist.idempotency.service.domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                    "com.gogidix.rapidassist.idempotency.service.adapters..",
                    "com.gogidix.rapidassist.idempotency.service.infrastructure..");
}
