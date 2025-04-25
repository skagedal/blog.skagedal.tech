#!/usr/bin/env bash

source common.sh

export JAVA="${JAVA_HOME}"/bin/java
export ENVIRONMENT=production

exec $JAVA \
  -Dlogback.configurationFile=logback-prod.xml \
  -jar blogdans-1.0-SNAPSHOT-jar-with-dependencies.jar serve
  