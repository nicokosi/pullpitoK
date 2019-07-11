plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.31"
    id("com.adarshr.test-logger") version "1.6.0"
    id("se.patrikerdes.use-latest-versions") version "0.2.9"
    id("com.github.ben-manes.versions") version "0.21.0"
    id("com.github.johnrengelman.shadow") version "5.0.0"
    application
}

repositories {
    jcenter()
    maven { url = uri("https://plugins.gradle.org/m2") }
}

dependencies {
    compile("com.fasterxml.jackson.core:jackson-databind:2.9.8")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

application {
    mainClassName = "pullpitok.AppKt"
}