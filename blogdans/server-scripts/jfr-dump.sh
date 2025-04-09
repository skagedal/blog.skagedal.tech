#!/usr/bin/env bash

if [ "$#" -lt 1 ]; then
    echo "Usage: $0 <dump-name> [duration]"
    echo "Example: $0 my_dump 60"
    echo "Duration is optional, default is 5 seconds."
    exit 1
fi
DUMP_NAME="$1"
DURATION="${2:-5}"

source common.sh

export JCMD="${JAVA_HOME}"/bin/jcmd

PID=$(pgrep -f "${PROCESS_NAME}")

$JCMD "$PID" JFR.start name="$DUMP_NAME"
sleep "$DURATION"
$JCMD "$PID" JFR.stop name="$DUMP_NAME" filename="$DUMP_NAME.jfr"
