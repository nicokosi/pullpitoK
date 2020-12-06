import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    id("com.adarshr.test-logger") version "2.1.1"
    id("com.diffplug.spotless") version "5.8.2"
    id("com.github.ben-manes.versions") version "0.36.0"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("org.jetbrains.kotlin.jvm") version "1.4.20"
    id("org.mikeneck.graalvm-native-image") version "v0.8.0"
    id("se.patrikerdes.use-latest-versions") version "0.2.15"
    application
}

repositories {
    jcenter()
    maven { url = uri("https://plugins.gradle.org/m2") }
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.11.3")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

application {
    mainClassName = "pullpitok.AppKt"
}

nativeImage {
    graalVmHome = System.getProperty("java.home")
    mainClass = "pullpitok.AppKt"
    executableName = "pullpitoK"
    outputDirectory = file("$buildDir/bin")
    arguments(
        " --enable-https",
        "--no-fallback",
        "--no-server",
        "-jar ./build/libs/pullpitoK-all.jar"
    )
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
