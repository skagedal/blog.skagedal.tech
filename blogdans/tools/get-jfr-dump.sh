#!/usr/bin/env bash

set -e

APP=blogdans
USER="$APP@skagedal.tech"
KEYFILE="~/.ssh/${APP}-key"

DUMPNAME="d$(openssl rand -hex 8)"

ssh -i "$KEYFILE" "$USER" ./jfr-dump.sh "$DUMPNAME" 10
scp -i "$KEYFILE" "$USER":"$DUMPNAME".jfr .
ssh -i "$KEYFILE" "$USER" rm "$DUMPNAME".jfr
echo "JFR dump saved as $DUMPNAME.jfr"

echo "Opening Azul..."
open "$DUMPNAME".jfr -a "Azul Mission Control"
