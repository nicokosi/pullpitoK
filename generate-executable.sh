#!/bin/bash

# Fail on error
set -e

echo "Building JAR file:"
./gradlew shadowJar
echo "JAR file has been built! ✅"

echo "Install GraalVM via SDKMAN!:"
curl --silent "https://get.sdkman.io" | bash || echo 'SDKMAN! already installed'
source "$HOME/.sdkman/bin/sdkman-init.sh"
GRAALVM_VERSION=1.0.0-rc-15-grl
sdkman_auto_answer=true sdk install java $GRAALVM_VERSION > /dev/null
sdk use java $GRAALVM_VERSION

echo "Copying 'libsunec' shared library (Sun Elliptic Curve crypto):"
set +e
cp $JAVA_HOME/jre/lib/*/libsunec* . 2>/dev/null # Linux
linux_libsunec=$?
cp $JAVA_HOME/jre/lib/libsunec* . 2>/dev/null #macOS
macos_libsunec=$?
set -e
if [ $linux_libsunec -ne 0 ] && [ $macos_libsunec -ne 0 ]; then
	echo "Cannot copy 'libsunec' shared library"
	exit 1
fi
echo "'libsunec' shared library has been copied! ✅"

echo "Build executable from JAR via GraalVM:"
native-image \
   --no-server \
   --enable-https \
   -jar ./build/libs/pullpitoK-all.jar \
   pullpitoK && \
   echo ' => Check the executable: ' && ./pullpitoK
echo "Executable has been built! ✅"

echo "\nExecutable has been generated in local directory, try it copy/pasting this command:
 $> ./pullpitoK python/peps

Executable can also be run from an other directory via a 'PULLPITOK_LIBSUNEC'
environment variable in order to load the 'libsunec' shared library (Sun
Elliptic Curve crypto) bundled in the Java Development Kit.
For instance, on Linux:
 $> PULLPITOK_LIBSUNEC=$JAVA_HOME/jre/lib/amd64 ./pullpitoK python/peps
or on macOS:
 $> PULLPITOK_LIBSUNEC=$JAVA_HOME/jre/lib ./pullpitoK python/peps"