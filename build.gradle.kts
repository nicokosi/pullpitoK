plugins {
    val kotlinVersion = "1.3.20"
    id("org.jetbrains.kotlin.jvm").version(kotlinVersion)
    id("com.adarshr.test-logger").version("1.6.0")
    id("se.patrikerdes.use-latest-versions") version "0.2.7"
    id("com.github.ben-manes.versions") version "0.20.0"
    application
}

repositories {
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
    maven { url = uri("https://kotlin.bintray.com/kotlinx") } // Required since 0.9.4 since ktor-client-gson includes ktor-client-json that depends on kotlinx-serialization
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