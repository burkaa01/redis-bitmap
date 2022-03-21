package com.github.burkaa01.trigger.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;

import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.springframework.http.HttpMethod.GET;

@RestController
public class TriggerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TriggerController.class);

    private final String distinctApiHost;
    private final String doneApiHost;
    private final JedisShardInfo redisConnectionInfo;

    @Autowired
    public TriggerController(@NonNull @Qualifier("distinctApiHost") String distinctApiHost,
                             @NonNull @Qualifier("doneApiHost") String doneApiHost,
                             @NonNull JedisShardInfo redisConnectionInfo) {

        this.distinctApiHost = distinctApiHost;
        this.doneApiHost = doneApiHost;
        this.redisConnectionInfo = redisConnectionInfo;
    }

    @GetMapping(path = "/trigger/distinct")
    public ResponseEntity<String> triggerDistinct(@RequestParam(value = "count") int count) {
        LOGGER.info("Received trigger/distinct request with count {}", count);

        if (count == 0) {
            LOGGER.info("Nothing to process");
            return new ResponseEntity<>(HttpStatus.OK);
        }

        int userId = randomUserId();
        int currentRequest = 1;
        int numberOfRequests = randomNumber(1, 4);
        LOGGER.info("Sending {} requests for user: {}", numberOfRequests, userId);

        int i = 0;
        while (i < count) {
            // make one request
            distinctRequest(userId);
            i++;
            if (i >= count) {
                break;
            }
            // new distinct request cycle
            if (currentRequest >= numberOfRequests) {
                userId = randomUserId();
                currentRequest = 1;
                numberOfRequests = randomNumber(1, 4);
                LOGGER.info("Sending {} requests for user: {}", numberOfRequests, userId);
            } else {
                currentRequest++;
            }
        }
        LOGGER.info("Finished sending distinct requests");
        distinctResetRequest();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/trigger/done")
    public ResponseEntity<String> triggerDone(@RequestParam(value = "count") int count) {
        LOGGER.info("Received trigger/done request with count {}", count);

        if (count == 0) {
            LOGGER.info("Nothing to process");
            return new ResponseEntity<>(HttpStatus.OK);
        }

        String taskId = UUID.randomUUID().toString().trim();
        LOGGER.info("Sending {} done requests for task: {}", count, taskId);

        // set "last" bit to true, initializing an end of steps marker for this task
        // before this task is considered done, all bits before this "last" bit must also be set to true
        Jedis jedis = new Jedis(redisConnectionInfo);
        jedis.setbit(taskId, count, true);

        // make each done request in parallel
        IntStream.range(0, count).parallel().forEach(stepNumber -> {
            doneRequest(taskId, stepNumber);
        });

        LOGGER.info("Finished sending done requests");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void distinctRequest(int userId) {
        restRequest(distinctApiHost + "/distinct/?userId=" + userId);
    }

    private void distinctResetRequest() {
        restRequest(distinctApiHost + "/distinct/reset");
    }

    private void doneRequest(String taskId, int stepNumber) {
        restRequest(doneApiHost + "/done/?taskId=" + taskId + "&stepNumber=" + stepNumber);
    }

    private void restRequest(String url) {
        new RestTemplate().exchange(url, GET, null, String.class);
    }

    private static int randomUserId() {
        return randomNumber(100000, 999999);
    }

    private static int randomNumber(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }
}