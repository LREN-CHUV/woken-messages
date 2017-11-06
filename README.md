[![CHUV](https://img.shields.io/badge/CHUV-LREN-AF4C64.svg)](https://www.unil.ch/lren/en/home.html) [![License](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](https://github.com/LREN-CHUV/woken-messages/blob/master/LICENSE)

# woken-messages library

## How to build

Prerequisites: docker, captain

1. Run the build script

```sh
./build.sh
```

It will build the scala project into a docker container.

## How to push to MIP's binaries repository

1. Write a .credentials file containing:

> realm=Artifactory Realm
> host=lab01560.intranet.chuv
> user=<USER>
> password=<PASSWORD>

2. Run the publish scripts
```
  ./publish.sh
```
