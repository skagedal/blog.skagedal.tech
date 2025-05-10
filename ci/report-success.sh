#!/usr/bin/env bash

person=$(git log -n1 --format='format:%aN' | awk '{print $1}')
commithash=$(git log -n1 --format='format:%H')
commiturl="https://github.com/skagedal/blog.skagedal.tech/commit/${commithash}"
commit="<${commiturl}|commit>"
curl -X POST -H 'Content-type: application/json' \
  --data "{\"text\":\"Deployed blogdans from ${person}'s latest ${commit}.\"}" \
  "${SLACK_WEBHOOK_URL}"
