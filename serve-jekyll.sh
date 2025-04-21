#!/usr/bin/env bash

# Add --drafts to include drafts

cd jekyll
bundle exec jekyll serve --open-url "$@"

