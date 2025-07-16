plugins {
	java
	id("org.springframework.boot") version "3.4.7"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "io.github.zapolyarnydev"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

configure(listOf(project(":order-service"), project(":inventory-service"))) {
	tasks.register<Test>("unitTest") {
		description = "Runs unit tests"
		group = "verification"

		testClassesDirs = sourceSets.test.get().output.classesDirs
		classpath = sourceSets.test.get().runtimeClasspath

		useJUnitPlatform {
			includeTags("unit")
		}
	}

	tasks.register<Test>("integrationTest") {
		description = "Runs integration tests"
		group = "verification"

		testClassesDirs = sourceSets.test.get().output.classesDirs
		classpath = sourceSets.test.get().runtimeClasspath

		useJUnitPlatform {
			includeTags("integration")
		}
	}
}

tasks.register("unitTestAll") {
	group = "verification"
	description = "Runs unit tests in all subprojects"
	dependsOn(
		listOf(project(":order-service"), project(":inventory-service"))
			.map { it.tasks.named("unitTest") }
	)
}

tasks.register("integrationTestAll") {
	group = "verification"
	description = "Runs integration tests in all subprojects"
	dependsOn(
		listOf(project(":order-service"), project(":inventory-service"))
			.map { it.tasks.named("integrationTest") }
	)
}

tasks.withType<Test> {
	useJUnitPlatform()
}
