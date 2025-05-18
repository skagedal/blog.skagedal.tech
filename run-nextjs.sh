#!/usr/bin/env bash

current_sha=$(git rev-parse HEAD)
unzip -o -d "nextjs-$current_sha" "nextjs-comments.zip"
cd "nextjs-$current_sha" || exit 1
exec node .next/standalone/server.js
