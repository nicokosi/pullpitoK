# PullpitoK [![Build Status](https://travis-ci.com/nicokosi/pullpitoK.svg?branch=master)](https://travis-ci.com/nicokosi/pullpitoK)

Like [nicokosi/pullpito](https://github.com/nicokosi/pullpito/), but implemented in [Kotlin](https://kotlinlang.org/) in order to discover this language. 🎓

## Build

    ./gradlew build

## Run
For a public Github repository, run:

    ./gradlew run --args "python/peps"

For a private Github repository, add a authentication token:

    ./gradlew run --args "fakeOrg/fakePrivateRepo $GITHUB_TOKEN"

In order to show the 'usage':

    ./gradlew run --args "--help"

## Install

In order to generate a *nix executable, run:

    ./generate-executable.sh

An executable file named `pullpitoK` should be generated in the current directory. It can then be run:

    ./pullpitoK

## Upgrade dependencies

    ./gradlew useLatestVersions