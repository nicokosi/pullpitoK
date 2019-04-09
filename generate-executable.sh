#!/bin/bash

# Fail on error
set -e

echo "Build nicokosi/pullpitoK JAR file:"
./gradlew shadowJar

echo "Install GraalVM via SDKMAN!:"
curl -s "https://get.sdkman.io" | bash || echo 'SDKMAN! already installed'
source "$HOME/.sdkman/bin/sdkman-init.sh"
GRAALVM_VERSION=1.0.0-rc-15-grl
sdk install java $GRAALVM_VERSION
sdk use java $GRAALVM_VERSION

echo "Copy 'libsunec' shared library (Sun Elliptic Curve crypto):"
cp $JAVA_HOME/jre/lib/libsunec* .

echo "Build nicokosi/pullpitoK's executable from JAR via GraalVM:"
native-image \
   --no-server \
   --verbose \
   --enable-https \
   -jar ./build/libs/pullpitoK-all.jar \
   pullpitoK && \
   echo ' => Check the executable: ' && ./pullpitoK

echo "Executable has been generated, try it copy/pasting this command:"
echo " GRAALVM_HOME=$GRAALVM_HOME ./pullpitoK python/peps"