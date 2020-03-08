plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.70"
    id("se.patrikerdes.use-latest-versions") version "0.2.13"
    id("com.github.ben-manes.versions") version "0.27.0"
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("com.adarshr.test-logger") version "2.0.0"
    application
}

repositories {
    jcenter()
    maven { url = uri("https://plugins.gradle.org/m2") }
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.10.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

application {
    mainClassName = "pullpitok.AppKt"
}