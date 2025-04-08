#!/bin/bash

if [[ "$1" == "--verbose" ]]; then
    BUNDLE_INSTALL_ARG=""
    RSYNC_ARG="--verbose"
else
    BUNDLE_INSTALL_ARG="--quiet"
    RSYNC_ARG=""
fi

cd jekyll

echo "💁 Installing Jekyll dependencies..."
bundle install $BUNDLE_INSTALL_ARG || exit 1

echo "💁 Generating site with Jekyll..."
bundle exec jekyll build || exit 1

echo "💁 Uploading..."
rsync \
    --archive \
    --compress \
    --delete \
    --info=progress2 \
    $RSYNC_ARG \
    _site/ \
    simon@skagedal.tech:blog

echo "💁 Uploading to blogdans..."
rsync \
    --archive \
    --compress \
    --delete \
    --info=progress2 \
    $RSYNC_ARG \
    ./ \
    --rsh="ssh -i ${HOME}/.ssh/blogdans-key" \
    blogdans@skagedal.tech:content

echo "💁 Done!"
