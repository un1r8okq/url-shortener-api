package land.ver.url.shortener.unit

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.library.Architectures
import org.junit.jupiter.api.Test
import org.springframework.web.bind.annotation.RestController

class ArchitectureTest {
    private val rootPackageName = "land.ver.url.shortener"
    private val packages = ClassFileImporter().importPackages(rootPackageName)

    @Test
    fun `layered architecture`() {
        Architectures.layeredArchitecture()
            .consideringOnlyDependenciesInAnyPackage(rootPackageName)
            .layer("Controller").definedBy("..controllers..")
            .layer("Service").definedBy("..services..")
            .layer("Repository").definedBy("..repositories..")
            .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
            .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
            .whereLayer("Repository").mayOnlyBeAccessedByLayers("Controller", "Service")
            .check(packages)
    }

    @Test
    fun `controllers should be in the correct package`() {
        ArchRuleDefinition.classes()
            .that().areAnnotatedWith(RestController::class.java)
            .should().resideInAPackage("$rootPackageName.controllers")
            .check(packages)
    }

    @Test
    fun `controllers' names end in Controller`() {
        ArchRuleDefinition.classes()
            .that().areAnnotatedWith(RestController::class.java)
            .should().haveNameMatching(".+Controller$")
            .check(packages)
    }

    @Test
    fun `services should be in the correct package`() {
        ArchRuleDefinition.classes()
            .that().areAnnotatedWith(RestController::class.java)
            .should().resideInAPackage("$rootPackageName.controllers")
            .check(packages)
    }
}
