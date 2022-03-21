#Distinct
######distinct-app
Example app where I demonstrate the Redis Bitmaps use case for collecting distinct activity metrics.

`Build app`
```
./gradlew build
```
Artifact will live at `build/libs/distinct-app-0.0.1-SNAPSHOT.jar`

`Build docker image`
```
docker build -t distinct-app .
```
Image will be visible when you run command `docker image ls -a`