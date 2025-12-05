plugins {
    java
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
}

fun getGitHash(): String {
    return providers.exec {
        commandLine("git", "rev-parse", "--short", "HEAD")
    }.standardOutput.asText.get().trim()
}

group = "kr.hhplus.be"
version = getGitHash()

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.0")
    }
}

dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")

    // DB
    runtimeOnly("com.mysql:mysql-connector-j")

    // Lombok
    implementation("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mysql")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
    systemProperty("user.timezone", "UTC")
}

sourceSets {
    create("testUnit") {
        java.srcDir("src/testUnit/java")
        resources.srcDir("src/testUnit/resources")
        compileClasspath = files(sourceSets["main"].output, configurations.testCompileClasspath)
        runtimeClasspath = files(output, compileClasspath, configurations.testRuntimeClasspath)
    }
}

@Suppress("UnstableApiUsage")
configurations {
    getByName("testUnitImplementation").extendsFrom(configurations.getByName("testImplementation"))
    getByName("testUnitRuntimeOnly").extendsFrom(configurations.getByName("testRuntimeOnly"))
}

tasks.register<Test>("testUnit") {
    description = "Run unit tests"
    group = "verification"
    testClassesDirs = sourceSets["testUnit"].output.classesDirs
    classpath = sourceSets["testUnit"].runtimeClasspath
}