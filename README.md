# woken-messages library

## How to build

Prerequisites: docker, captain

1. Run the build script
```
./build.sh
```
It will build the scala project into a docker container.

## How to push to MIP's binaries repository

1. Write a .credentials file containing:

> realm=Artifactory Realm
> host=lab01560.intranet.chuv
> user=<USER>
> password=<PASSWORD>

2. Run the push scripts
```
./push.sh
```