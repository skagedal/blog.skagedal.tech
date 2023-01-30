#!/bin/bash

bundle install || exit 1
bundle exec jekyll build || exit 1

rsync \
    --archive \
    --compress \
    --progress \
    --delete \
    --verbose \
    _site/ \
    simon@skagedal.tech:blog

