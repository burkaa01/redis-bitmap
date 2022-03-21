# Redis Bitmaps
###### redis-bitmap
Example project where I demonstrate two great use cases for Redis Bitmaps: determining "done" in an asynchronous system and collecting distinct activity metrics.

`Local docker images`
```
image: distinct-app
image: done-app
image: trigger-app
```
Update images in docker-compose.yml accordingly

`Docker hub images`
```
image: burkaa01/redis-bitmap:distinct-app
image: burkaa01/redis-bitmap:done-app
image: burkaa01/redis-bitmap:trigger-app
```
Update images in docker-compose.yml accordingly

`Start up example project`
```
docker-compose up
```

`Stop example project`
```
docker-compose down
```
Or use `CTRL+C` in the original terminal window

`Logs`
```
docker container logs -f distinct-app
docker container logs -f done-app
docker container logs -f trigger-app
```

`Requests`
```
curl "localhost:8098/trigger/distinct/?count=10"
curl "localhost:8098/trigger/done/?count=10"
```
First request triggers `distinct` example and second request triggers `done` example
