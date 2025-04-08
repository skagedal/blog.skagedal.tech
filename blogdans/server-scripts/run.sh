#!/usr/bin/env bash
export JAVA_HOME=/usr/lib/jvm/temurin-24-jdk-amd64
export JAVA="${JAVA_HOME}"/bin/java
export ENVIRONMENT=production
java -jar blogdans-1.0-SNAPSHOT-jar-with-dependencies.jar serve
