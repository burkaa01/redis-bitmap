#Trigger
######trigger-app

Example app used to trigger activity in both Redis Bitmaps demonstrations.

`Build app`
```
./gradlew build
```
Artifact will live at `build/libs/trigger-app-0.0.1-SNAPSHOT.jar`

`Build docker image`
```
docker build -t trigger-app .
```
Image will be visible when you run command `docker image ls -a`