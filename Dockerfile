# Pull base image
FROM hbpmip/scala-base-build:0.13.16-5 as scala-build-env

MAINTAINER Arnaud Jutzeler <arnaud.jutzeler@chuv.ch>

ARG BINTRAY_USER
ARG BINTRAY_PASS

ENV BINTRAY_USER=$BINTRAY_USER \
    BINTRAY_PASS=$BINTRAY_PASS

COPY build.sbt /build/
COPY project/ /build/project/

# Run sbt on an empty project and force it to download most of its dependencies to fill the cache
RUN sbt compile

COPY src/ /build/src/
COPY .git/ /build/.git/
COPY .circleci/ /build/.circleci/
COPY .*.cfg .*ignore .*.yaml .*.conf *.md *.builder *.sh *.yml *.json LICENSE /build/

RUN /check-sources.sh

RUN sbt package
