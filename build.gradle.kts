import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.5.30"
    id("se.patrikerdes.use-latest-versions") version "0.2.17"
    id("com.github.ben-manes.versions") version "0.39.0"
    id("com.adarshr.test-logger") version "3.0.0"
    id("com.diffplug.spotless") version "5.14.3"
    application
}

repositories {
    mavenCentral()
    maven { url = uri("https://plugins.gradle.org/m2") }
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

application {
    mainClass.set("pullpitok.AppKt")
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
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
