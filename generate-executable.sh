#!/bin/bash

# Fail on error
set -e

echo "Building JAR file:"
./gradlew uberJar
echo "JAR file has been built! ✅"

echo "Install GraalVM via SDKMAN!:"
curl --silent "https://get.sdkman.io?rcupdate=false" | bash || echo 'SDKMAN! already installed'
source "$HOME/.sdkman/bin/sdkman-init.sh"
GRAALVM_VERSION="25.0.1-graalce"
sdkman_auto_answer=true sdk install java $GRAALVM_VERSION > /dev/null || echo "GraalVM $GRAALVM_VERSION already installed."
sdk use java $GRAALVM_VERSION

echo "Build executable from JAR via GraalVM:"
gu install native-image && \
native-image \
   --enable-https \
   --no-fallback \
   -jar ./build/libs/pullpitoK-all.jar \
   pullpitoK && \
   echo ' => Check the executable: ' && ./pullpitoK
echo "Executable has been built! ✅"

echo "Executable has been generated in local directory.

For instance, run the following command for python/peps (https://github.com/python/peps):

 $> ./pullpitoK python/peps"
