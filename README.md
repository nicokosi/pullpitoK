# PullpitoK [![Build Status](https://github.com/nicokosi/pullpitoK/actions/workflows/ci.yml/badge.svg)](https://github.com/nicokosi/pullpitoK/actions/workflows/ci.yml)

Like [nicokosi/pullpito](https://github.com/nicokosi/pullpito/), but implemented in [Kotlin](https://kotlinlang.org/) in order to discover this language. 🎓

## Build

    ./gradlew build

## Run

For a public Github repository, run:

    ./gradlew run --args "python/peps"

For a private Github repository, add a authentication token:

    ./gradlew run --args "fakeOrg/fakePrivateRepo $GITHUB_TOKEN"

In order to show the 'usage':

    ./gradlew run --args="--help"

## Install

In order to generate a *nix executable, run:

    ./generate-executable.sh

An executable file named `pullpitoK` should be generated in the current directory. It can then be run:

    ./pullpitoK

## Dev tasks

### Format the code

    ./gradlew spotlessApply

### Inspect the code

Code inspection is configured on the `main` branch. See the [sonarcloud.io's dashboard](https://sonarcloud.io/dashboard?id=nicokosi_pullpitoK).

In order to inspect the code locally, run:

    docker run -d --name sonarqube -p 9000:9000 sonarqube
    ./gradlew sonarqube --info -Dsonar.login=admin -Dsonar.password=admin

### Upgrade the dependencies

    ./gradlew useLatestVersions

### Upgrade the build system

Check the current version in [the `Gradle` documentation](https://docs.gradle.org) and run:

    ./gradlew wrapper --gradle-version $GRADLE_CURRENT_VERSION
