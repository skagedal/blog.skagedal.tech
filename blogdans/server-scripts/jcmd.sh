#!/usr/bin/env bash

source common.sh

export JCMD="${JAVA_HOME}"/bin/jcmd

PID=$(pgrep -f "${PROCESS_NAME}")
$JCMD