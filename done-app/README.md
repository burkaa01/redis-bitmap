# Done
###### done-app
Example app where I demonstrate the Redis Bitmaps use case for determining "done" in an asynchronous system.

`Build java app`
```
./gradlew build
```
Artifact will live at `build/libs/done-app-0.0.1-SNAPSHOT.jar`

`Build docker image`
```
docker build -t done-app .
```
Image will be visible when you run command `docker image ls -a`

