#!/usr/bin/env bash

unzip nextjs-latest.zip
dir_name=$(basename "$(readlink nextjs-latest.zip)" .zip)
cd "$dir_name" || exit 1
exec npm run start
