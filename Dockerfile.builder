# Pull base image
FROM hbpmip/scala-base-build:0.13.16-3 as scala-build-env

MAINTAINER Arnaud Jutzeler <arnaud.jutzeler@chuv.ch>

COPY build.sbt /build/
COPY project/ /build/project/
COPY src/ /build/src/
COPY .git/ /build/.git/

RUN sbt package
