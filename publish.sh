#!/bin/bash

# Get git hash as version (temporary solution waiting on better captain)
export VERSION="$(git log --pretty=format:'%h' -n 1)"

# Push to repo
docker run --rm -ti --entrypoint="sbt" --net=host -u build -v $PWD:/build/ -v $HOME/.m2:/home/build/.m2/ -v $HOME/.ivy2:/home/build/.ivy2 -e "VERSION=$VERSION" woken-messages-build publish
