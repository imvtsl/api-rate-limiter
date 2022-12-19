package com.vatsal.project.apiratelimiter.slidingwindow.service;

import com.vatsal.project.apiratelimiter.dto.StatusResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * A service class having business logic for a Sliding Window based API Rate Limiter.
 * @author imvtsl
 * @since v1,0
 */

@Service
@Slf4j
public class SlidingWindowService {
    @Value("${app.slidingwindow.window.length}")
    private Integer windowLength;

    @Value("${app.slidingwindow.window.time}")
    private long windowTime;

    private Map<String, Queue<Long>> queueMap;

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
                return handleExistingUser(userName, unixTimestamp);
            } else {
                log.debug("new user");
                Queue<Long> queue = new LinkedList<>();
                updateMap(queue, unixTimestamp, userName);
                return new StatusResponse(true);
            }
        }
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
                queueMap.remove(userName);
                return new StatusResponse(true);
            } else {
                return new StatusResponse(false, "user:" + userName + " not found.");
            }
        }

    }

    /**
     * Checks throttle and removes the old requests from the window for the existing user.
     * @param userName String
     * @param unixTimestamp long
     */
    private synchronized StatusResponse handleExistingUser(String userName, long unixTimestamp) {
        Queue<Long> queue = queueMap.get(userName);
        while (!queue.isEmpty() && checkToRemove(queue.peek(), unixTimestamp)) {
            log.debug("removing from queue:" + queue.peek());
            queue.poll();
        }
        if (queue.size() < windowLength) {
            updateMap(queue, unixTimestamp, userName);
            return new StatusResponse(true);
        } else {
            queueMap.put(userName, queue);
            return new StatusResponse(false, "Throttle limit exceeded");
        }
    }

    /**
     * Updates the map with the incoming request.
     * @param queue Queue<Long>
     * @param unixTimestamp long
     * @param userName String
     */
    private synchronized void updateMap(Queue<Long> queue, long unixTimestamp, String userName) {
        queue.add(unixTimestamp);
        queueMap.put(userName, queue);
    }

    /**
     * Checks if the head element in the queue is outside the window time.
     * @param peek Long
     * @param unixTimestamp long
     * @return boolean
     */
    private boolean checkToRemove(Long peek, long unixTimestamp) {
        return (unixTimestamp - windowTime) > peek;
    }
}
