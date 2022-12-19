package com.vatsal.project.apiratelimiter.leakybucket.model;

import lombok.Getter;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Model class for storing request timestamps and executor to poll requests at fixed intervals for a user.
 * @author imvtsl
 * @since v1.0
 */

@Getter
public class BucketQueueExecutor {
    /**
     * A queue to store incoming requests.
     */
    private Queue<Long> queue;

    /**
     * Executor that polls requests from queue at fixed intervals.
     */
    private ScheduledExecutorService scheduledExecutorService;

    public BucketQueueExecutor() {
        this.queue = new LinkedList<>();
        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
    }

    /**
     * Adds request timestamp to the queue.
     * @param unixTimestamp long
     */
    public void add(long unixTimestamp) {
        queue.add(unixTimestamp);
    }

    /**
     * Polls the head element of the queue.
     * @return Long
     */
    public Long poll() {
        return queue.poll();
    }

    /**
     * Stops the thread pool.
     */
    public void stop() {
        scheduledExecutorService.shutdownNow();
    }
}
