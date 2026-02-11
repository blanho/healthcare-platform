package com.healthcare;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "com.healthcare", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule layer_dependencies_are_respected = layeredArchitecture()
        .consideringAllDependencies()
        .layer("API").definedBy("..api..")
        .layer("Domain").definedBy("..domain..")
        .layer("Service").definedBy("..service..")
        .layer("Repository").definedBy("..repository..")
        .layer("Config").definedBy("..config..")

        .whereLayer("API").mayNotBeAccessedByAnyLayer()
        .whereLayer("Service").mayOnlyBeAccessedByLayers("API")
        .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")
        .whereLayer("Domain").mayOnlyBeAccessedByLayers("API", "Service", "Repository", "Config")
        .allowEmptyShould(true);

    @ArchTest
    static final ArchRule modules_should_not_have_cyclic_dependencies = slices()
        .matching("com.healthcare.(*)..")
        .should().beFreeOfCycles()
        .allowEmptyShould(true);

    @ArchTest
    static final ArchRule patient_module_should_not_depend_on_other_modules =
        noClasses()
            .that().resideInAPackage("..patient..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                "..appointment..",
                "..billing..",
                "..medicalrecord..",
                "..notification.."
            )
            .allowEmptyShould(true)
            .because("Patient module should be independent - use events for cross-module communication");

    @ArchTest
    static final ArchRule appointment_module_can_only_depend_on_allowed_modules =
        classes()
            .that().resideInAPackage("..appointment..")
            .should().onlyDependOnClassesThat()
            .resideInAnyPackage(
                "java..",
                "jakarta..",
                "org.springframework..",
                "org.slf4j..",
                "lombok..",
                "com.healthcare.common..",
                "com.healthcare.appointment.."
            )
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule controllers_should_be_suffixed =
        classes()
            .that().resideInAPackage("..api..")
            .and().areAnnotatedWith(org.springframework.web.bind.annotation.RestController.class)
            .should().haveSimpleNameEndingWith("Controller")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule services_should_be_suffixed =
        classes()
            .that().resideInAPackage("..service..")
            .and().areAnnotatedWith(org.springframework.stereotype.Service.class)
            .should().haveSimpleNameEndingWith("Service")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule repositories_should_be_suffixed =
        classes()
            .that().resideInAPackage("..repository..")
            .should().haveSimpleNameEndingWith("Repository")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule entities_should_not_be_public_outside_module =
        classes()
            .that().resideInAPackage("..domain..")
            .and().haveSimpleNameNotEndingWith("DTO")
            .and().haveSimpleNameNotEndingWith("Event")
            .should().notBePublic()
            .orShould().beAnnotatedWith(jakarta.persistence.Entity.class)
            .allowEmptyShould(true)
            .because("Domain entities should be accessed through service layer");

    @ArchTest
    static final ArchRule no_field_injection =
        noClasses()
            .should().beAnnotatedWith(org.springframework.beans.factory.annotation.Autowired.class)
            .because("Use constructor injection instead of field injection");

    @ArchTest
    static final ArchRule controllers_should_only_call_services =
        classes()
            .that().resideInAPackage("..api..")
            .should().onlyDependOnClassesThat()
            .resideInAnyPackage(
                "java..",
                "jakarta..",
                "org.springframework..",
                "org.slf4j..",
                "lombok..",
                "..api..",
                "..service..",
                "..domain..",
                "..common.."
            )
            .allowEmptyShould(true)
            .because("Controllers should not directly access repositories");

    @ArchTest
    static final ArchRule services_should_not_depend_on_controllers =
        noClasses()
            .that().resideInAPackage("..service..")
            .should().dependOnClassesThat()
            .resideInAPackage("..api..")
            .allowEmptyShould(true)
            .because("Services should not depend on API layer");
}
