[![CHUV](https://img.shields.io/badge/CHUV-LREN-AF4C64.svg)](https://www.unil.ch/lren/en/home.html) [![License](https://img.shields.io/badge/license-AGPL--3.0-blue.svg)](https://github.com/LREN-CHUV/woken-messages/blob/master/LICENSE) [![Download](https://api.bintray.com/packages/hbpmedical/maven/woken-messages/images/download.svg)](https://bintray.com/hbpmedical/maven/woken-messages/_latestVersion)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/50b557498f404feb86f4d118eb6e143f)](https://www.codacy.com/app/hbp-mip/woken-messages?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=HBPMedical/woken-messages&amp;utm_campaign=Badge_Grade) [![CircleCI](https://circleci.com/gh/HBPMedical/woken-messages.svg?style=svg)](https://circleci.com/gh/HBPMedical/woken-messages)

# woken-messages library

This library contains the messages and API used in Woken ML applications.

## Usage

The library is hosted on [https://bintray.com/hbpmedical/maven/woken-messages](Bintray.com), follow the instructions from Bintray to configure Maven or sbt.

Maven dependency:

```xml
  <dependency>
     <groupId>ch.chuv.lren.woken</groupId>
     <artifactId>woken-messages_2.11</artifactId>
     <version>2.6.3</version>
  </dependency>
```
sbt dependency:

```
  "ch.chuv.lren.woken" %% "woken-messages" % 2.6.3
```

Woken-messages is provided for Scala 2.11 and Scala 2.12

## How to build

Prerequisites: docker, captain

1. Run the build script

```sh
./build.sh
```

It will build the scala project into a docker container.

## How to publish to MIP's binaries repository

1. Define the environment variables to log into BinTray

> BINTRAY_USER=<USER>
> BINTRAY_PASS=<PASSWORD>

2. Run the publish script
```
  ./publish.sh
```

# Acknowledgements

This work has been funded by the European Union Seventh Framework Program (FP7/2007­2013) under grant agreement no. 604102 (HBP)

This work is part of SP8 of the Human Brain Project (SGA1).
