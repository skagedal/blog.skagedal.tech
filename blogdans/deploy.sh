#!/usr/bin/env bash

set -e

APP=blogdans
USER="$APP@skagedal.tech"
KEYFILE="~/.ssh/${APP}-key"

echo "👋 Building JAR with Java 24..."
JAVA_HOME=$(/usr/libexec/java_home -v 24)
export JAVA_HOME
mvn clean package assembly:single

echo
echo "👋 Uploading JAR to skagedal.tech..."
scp -i "$KEYFILE" "target/$APP-1.0-SNAPSHOT-jar-with-dependencies.jar" "$USER:"
scp -i "$KEYFILE" server-scripts/run.sh "$USER:"

echo "👋 Sending TERM signal to running service..."
ssh -i "$KEYFILE" "$USER" pkill -TERM -f "$APP-1.0-SNAPSHOT"
