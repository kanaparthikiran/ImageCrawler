#!/usr/bin/env bash
export PATH=${JAVA_HOME}/bin:${PATH}
BUILD_DIR=`pwd`
BUILD_CLASSPATH=
for i in `ls ${BUILD_DIR}/lib/*.jar`
do
BUILD_CLASSPATH=${BUILD_CLASSPATH}:${i}
done
#echo "Set the Build Classpath as $BUILD_CLASSPATH"
echo "Current Directory for Build and Install is $BUILD_DIR"
if [ -d "build" ]; then
echo "directory build already exists so removing that"
  rm -R build
fi
echo "Creating directory for build"
mkdir build
echo "created build directory for compilation"
javac  -classpath  "${BUILD_CLASSPATH}"  src/com/resto/image/*.java  src/com/resto/image/util/*.java -d build
echo "All the classes required for Build Installation are compiled"
java -Dlog4j.configuration=src/log4j.properties  -classpath "${BUILD_CLASSPATH}:${CLASSPATH}:build:build/log4j.properties" com.resto.image.ImageTestDFSThreads

echo "Completed Crawling the WebSite and Generating Report"
