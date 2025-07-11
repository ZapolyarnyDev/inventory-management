plugins {
    java
    war
    id("org.springframework.boot") version "3.4.7"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "io.github.zapolyarnydev.discovery"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-server")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

extra["springCloudVersion"] = "2024.0.0"

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.test {
    useJUnitPlatform()
}