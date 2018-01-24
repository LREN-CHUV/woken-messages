# Pull base image
FROM hbpmip/scala-base-build:1.1.0-0 as scala-build-env

MAINTAINER Ludovic Claude <ludovic.claude@chuv.ch>

ARG BINTRAY_USER
ARG BINTRAY_PASS

ENV BINTRAY_USER=$BINTRAY_USER \
    BINTRAY_PASS=$BINTRAY_PASS

# First caching layer: build.sbt and sbt configuration
COPY build.sbt /build/
RUN  mkdir -p /build/project/
COPY project/build.properties project/plugins.sbt project/.gitignore /build/project/

# Run sbt on an empty project and force it to download most of its dependencies to fill the cache
RUN sbt compile

# Second caching layer: project sources
COPY src/ /build/src/
COPY .git/ /build/.git/
COPY .circleci/ /build/.circleci/
COPY .*.cfg .*ignore .*.yaml .*.conf *.md *.sh *.yml *.json Dockerfile LICENSE /build/

RUN /check-sources.sh

RUN sbt +test package

# Check again the sources to detect code that needs reformatting
RUN /check-sources.sh
