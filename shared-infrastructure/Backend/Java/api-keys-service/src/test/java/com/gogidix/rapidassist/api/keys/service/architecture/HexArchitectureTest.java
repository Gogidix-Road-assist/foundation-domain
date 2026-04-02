package com.gogidix.rapidassist.api.keys.service.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.DisplayName;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * Architecture Test: HexArchitectureTest
 *
 * Verifies the hexagonal architecture constraints are enforced.
 * This test ensures layer boundaries are respected.
 */
@AnalyzeClasses(packages = "com.gogidix.rapidassist.api.keys.service",
        importOptions = {ImportOption.DoNotIncludeTests.class})
@DisplayName("Hexagonal Architecture Tests")
class HexArchitectureTest {

    @ArchTest
    static final ArchRule domain_layer_should_not_depend_on_other_layers =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..application..", "..infrastructure..", "..interfaces..", "..adapters..")
                    .because("Domain layer should have zero dependencies on other layers");

    @ArchTest
    static final ArchRule application_layer_should_not_depend_on_interfaces =
            noClasses()
                    .that().resideInAPackage("..application..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..interfaces..", "..adapters..")
                    .because("Application layer should not depend on interfaces");

    @ArchTest
    static final ArchRule infrastructure_should_not_depend_on_domain =
            classes()
                    .that().resideInAnyPackage("..infrastructure..", "..adapters..")
                    .should().onlyDependOnClassesThat()
                    .resideInAnyPackage("..infrastructure..", "..adapters..", "..domain..", "..application..",
                            "java..", "org.springframework..", "com.gogidix.rapidassist.shared..")
                    .because("Infrastructure should only depend on domain, shared, and frameworks");

    @ArchTest
    static final ArchRule controllers_should_be_named_correctly =
            classes()
                    .that().areAnnotatedWith(RestController.class)
                    .or().areAnnotatedWith(Controller.class)
                    .should().haveSimpleNameEndingWith("Controller")
                    .because("Controllers should have Controller suffix");

    @ArchTest
    static final ArchRule repositories_should_be_named_correctly =
            classes()
                    .that().areAnnotatedWith(Repository.class)
                    .should().haveSimpleNameEndingWith("Repository")
                    .because("Repositories should have Repository suffix");

    @ArchTest
    static final ArchRule handlers_should_be_named_correctly =
            classes()
                    .that().resideInAPackage("..application..")
                    .and().areAnnotatedWith(Component.class)
                    .should().haveSimpleNameEndingWith("Handler")
                    .because("Application handlers should have Handler suffix");

    @ArchTest
    static final ArchRule enforce_package_layering =
            slices()
                    .matching("com.gogidix.rapidassist.api.keys.service.(*)..")
                    .should().beFreeOfCycles();

    @ArchTest
    static final ArchRule domain_classes_should_not_use_spring_annotations =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().beAnnotatedWith(Component.class)
                    .orShould().beAnnotatedWith(Repository.class)
                    .orShould().beAnnotatedWith(Service.class)
                    .because("Domain classes should be framework-agnostic");

    @ArchTest
    static final ArchRule all_controllers_must_reside_in_interfaces =
            classes()
                    .that().areAnnotatedWith(RestController.class)
                    .should().resideInAnyPackage("..interfaces..", "..adapters..");

    @ArchTest
    static final ArchRule all_repositories_must_reside_in_infrastructure =
            classes()
                    .that().areAnnotatedWith(Repository.class)
                    .should().resideInAnyPackage("..infrastructure..", "..adapters..");
}
