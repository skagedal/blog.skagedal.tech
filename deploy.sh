#!/bin/bash

if [[ "$1" == "--verbose" ]]; then
    BUNDLE_INSTALL_ARG=""
    RSYNC_ARG="--verbose"
else
    BUNDLE_INSTALL_ARG="--quiet"
    RSYNC_ARG=""
fi

echo "💁 Generating HTML using Jekyll"
echo "💁 Installing dependencies..."

cd rendered-posts
bundle install $BUNDLE_INSTALL_ARG || exit 1

echo "💁 Generating site..."
bundle exec jekyll build || exit 1

echo "💁 Uploading to blogdans..."
rsync \
    --archive \
    --compress \
    --delete \
    --info=progress2 \
    $RSYNC_ARG \
    _site/ \
    --rsh="ssh -i ${HOME}/.ssh/blogdans-key" \
    blogdans@skagedal.tech:rawposts

echo "💁 Done!"
