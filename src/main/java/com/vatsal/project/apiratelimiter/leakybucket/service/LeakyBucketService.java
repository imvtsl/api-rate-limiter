package com.vatsal.project.apiratelimiter.leakybucket.service;

import com.vatsal.project.apiratelimiter.dto.StatusResponse;
import com.vatsal.project.apiratelimiter.leakybucket.model.BucketQueueExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A service class having business logic for a Leaky Bucket based API Rate Limiter.
 * @author imvtsl
 * @since v1,0
 */

@Service
@Slf4j
public class LeakyBucketService {
    private Map<String, BucketQueueExecutor> queueMap;

    /**
     * Assigns memory to map after this bean is initialised.
     */
    @PostConstruct
    private void instantiate() {
        queueMap = new HashMap<>();
    }

    /**
     * Processes the incoming request.
     * @param userName String
     * @return StatusResponse
     */
    public StatusResponse submitRequest(String userName) {
        long unixTimestamp = Instant.now().getEpochSecond();
        log.info("Request received from user:" + userName + " at:" + unixTimestamp);
        synchronized (this) {
            if (queueMap.containsKey(userName)) {
                log.debug("existing user");
                BucketQueueExecutor bucketQueueExecutor = queueMap.get(userName);
                updateMap(bucketQueueExecutor, unixTimestamp, userName);
            } else {
                log.debug("new user");
                handleNewUser(userName, unixTimestamp);
            }
        }
        return new StatusResponse(true);
    }

    /**
     * Removes the user from the map. If user is not found in the map, it returns failure.
     * @param userName String
     * @return StatusResponse
     */
    public StatusResponse deregisterUser(String userName) {
        synchronized (this) {
            if (queueMap.containsKey(userName)) {
                log.debug("existing user");
                BucketQueueExecutor bucketQueueExecutor = queueMap.get(userName);
                bucketQueueExecutor.stop();
                queueMap.remove(userName);
                return new StatusResponse(true);
            } else {
                return new StatusResponse(false, "user:" + userName + " not found.");
            }
        }

    }

    /**
     * Creates executor and updates map for a new user.
     * @param userName String
     * @param unixTimestamp long
     */
    private synchronized void handleNewUser(String userName, long unixTimestamp) {
        BucketQueueExecutor bucketQueueExecutor = new BucketQueueExecutor();
        updateMap(bucketQueueExecutor, unixTimestamp, userName);

        ScheduledExecutorService scheduledExecutorService = bucketQueueExecutor.getScheduledExecutorService();

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            Long pollTimeStamp =  bucketQueueExecutor.poll();
            log.info(Instant.now().getEpochSecond() + ":Processing request submitted by user:" + userName + " at:" + pollTimeStamp);
            queueMap.put(userName, bucketQueueExecutor);
        }, 15, 15, TimeUnit.SECONDS);
    }

    /**
     * Updates the map with the incoming request.
     * @param bucketQueueExecutor BucketQueueExecutor
     * @param unixTimestamp long
     * @param userName String
     */
    private synchronized void updateMap(BucketQueueExecutor bucketQueueExecutor, long unixTimestamp, String userName) {
        bucketQueueExecutor.add(unixTimestamp);
        queueMap.put(userName, bucketQueueExecutor);
    }
}
