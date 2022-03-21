package com.github.burkaa01.trigger.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;

@RestController
public class DistinctController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DistinctController.class);
    private static final String REDIS_KEY = "distinct";

    private final JedisShardInfo redisConnectionInfo;

    @Autowired
    public DistinctController(@NonNull JedisShardInfo redisConnectionInfo) {

        this.redisConnectionInfo = redisConnectionInfo;
    }

    @GetMapping(path = "/distinct")
    public ResponseEntity<String> distinct(@RequestParam(value = "userId") int userId) {

        Jedis jedis = new Jedis(redisConnectionInfo);

        // set bit to true, which accounts for the distinct user
        jedis.setbit(REDIS_KEY, userId, true);

        // count the number of true bits aka the number of distinct users
        long count = jedis.bitcount(REDIS_KEY);
        LOGGER.info("{} distinct users", count);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/distinct/reset")
    public ResponseEntity<String> distinctReset() {

        Jedis jedis = new Jedis(redisConnectionInfo);
        jedis.del(REDIS_KEY);
        LOGGER.info("Reset distinct user count");

        return new ResponseEntity<>(HttpStatus.OK);
    }
}