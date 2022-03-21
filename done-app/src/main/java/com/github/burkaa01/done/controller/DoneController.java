package com.github.burkaa01.done.controller;

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
public class DoneController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DoneController.class);

    private final JedisShardInfo redisConnectionInfo;

    @Autowired
    public DoneController(@NonNull JedisShardInfo redisConnectionInfo) {

        this.redisConnectionInfo = redisConnectionInfo;
    }

    @GetMapping(path = "/done")
    public ResponseEntity<String> done(@RequestParam(value = "taskId") String taskId,
                                       @RequestParam(value = "stepNumber") int stepNumber) {

        if (taskId == null || taskId.trim().isEmpty()) {
            LOGGER.info("Unable to process request without a taskId");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String task = taskId.trim();

        // set bit to true, which indicates that step number for the task is done
        Jedis jedis = new Jedis(redisConnectionInfo);
        jedis.setbit(task, stepNumber, true);
        LOGGER.info("Step number {} is done for task: {}", stepNumber, task);

        if (isDone(jedis, task)) {
            // clean up task in redis bitmap
            jedis.del(task);
            LOGGER.info("Deleted task: {}", task);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private static boolean isDone(Jedis jedis, String task) {
        // get the index of the smallest false bit for use in determining if the task on the whole is done
        Long leftMostZero = jedis.bitpos(task, false);

        // count the number of true bits aka the number of steps that are done
        long count = jedis.bitcount(task);

        // check if done
        boolean done = (leftMostZero == null || leftMostZero < 0 || leftMostZero == count);
        if (done) {
            LOGGER.info("Done with all {} steps for task: {}", count - 1, task);
        }
        return done;
    }
}