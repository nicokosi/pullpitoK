#!/bin/bash

# Fail on error
set -e

echo "Building JAR file:"
./gradlew shadowJar
echo "JAR file has been built! ✅"

echo "Install GraalVM via SDKMAN!:"
curl --silent "https://get.sdkman.io" | bash || echo 'SDKMAN! already installed'
source "$HOME/.sdkman/bin/sdkman-init.sh"
GRAALVM_VERSION="19.3.0.r11-grl"
sdkman_auto_answer=true sdk install java $GRAALVM_VERSION > /dev/null || echo "GraalVM $GRAALVM_VERSION already installed."
sdk use java $GRAALVM_VERSION

echo "Build executable from JAR via GraalVM:"
gu install native-image && \
native-image \
   --enable-https \
   --no-fallback \
   --no-server \
   -jar ./build/libs/pullpitoK-all.jar \
   pullpitoK && \
   echo ' => Check the executable: ' && ./pullpitoK
echo "Executable has been built! ✅"

echo "Executable has been generated in local directory can be run with
a 'PULLPITOK_LIBSUNEC' environment variable in order to load the'libsunec' shared library (Sun
Elliptic Curve crypto) bundled in the Java Development Kit.
For instance, on Linux:
 $> PULLPITOK_LIBSUNEC=$JAVA_HOME/jre/lib/amd64 ./pullpitoK python/peps
or on macOS:
 $> PULLPITOK_LIBSUNEC=$JAVA_HOME/jre/lib ./pullpitoK python/peps"
