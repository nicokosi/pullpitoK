import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    id("org.jetbrains.kotlin.jvm") version "2.0.20"
    id("se.patrikerdes.use-latest-versions") version "0.2.18"
    id("com.github.ben-manes.versions") version "0.51.0"
    id("com.adarshr.test-logger") version "4.0.0"
    id("com.diffplug.spotless") version "6.25.0"
    id("org.sonarqube") version "5.0.0.4638"
    application
}

repositories {
    mavenCentral()
    maven { url = uri("https://plugins.gradle.org/m2") }
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation("com.github.tomakehurst:wiremock-jre8:3.0.1")
}

application {
    mainClass.set("pullpitok.AppKt")
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(21))
    }
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}
tasks.withType<DependencyUpdatesTask> {
    resolutionStrategy {
        componentSelection {
            all {
                if (isNonStable(candidate.version) && !isNonStable(currentVersion)) {
                    reject("Release candidate")
                }
            }
        }
    }
}

spotless {
    kotlin {
        ktlint()
    }
    kotlinGradle {
        // same as kotlin, but for .gradle.kts files (defaults to '*.gradle.kts')
        target("*.gradle.kts")
        ktlint()
    }
}

tasks.register<Jar>("uberJar") {
    manifest {
        attributes["Implementation-Title"] = "PullpitoK"
        attributes["Implementation-Version"] = archiveVersion
        attributes["Main-Class"] = "pullpitok.AppKt"
    }

    archiveClassifier.set("all")
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}
