#!/usr/bin/env bash

cd /build

ls -al

count=$(git status --porcelain | wc -l)
if test $count -gt 0; then
    git status
    echo "Not all files have been copied to Docker. Build aborted"
    exit 1
fi