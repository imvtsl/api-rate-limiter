package com.vatsal.project.apiratelimiter.slidingwindow.service;

import com.vatsal.project.apiratelimiter.dto.StatusResponse;
import com.vatsal.project.apiratelimiter.leakybucket.model.BucketQueueExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

@SpringBootTest
class SlidingWindowServiceTest {
    private static final String USER_NAME_VATSAL = "imvtsl";

    @Autowired
    SlidingWindowService slidingWindowService;

    @Test
    void testSubmitRequestNewUser() {
        slidingWindowService = new SlidingWindowService();
        Map<String, Queue<Long>> queueMap = new HashMap<>();
        ReflectionTestUtils.setField(slidingWindowService, "queueMap", queueMap);
        StatusResponse statusResponse = slidingWindowService.submitRequest(USER_NAME_VATSAL);
        Assertions.assertEquals(true, statusResponse.getStatus());
    }

    @Test
    void testSubmitRequestExistingUser() {
        Map<String, Queue<Long>> queueMap = new HashMap<>();
        queueMap.put(USER_NAME_VATSAL, new LinkedList<>());
        ReflectionTestUtils.setField(slidingWindowService, "queueMap", queueMap);

        StatusResponse statusResponse = slidingWindowService.submitRequest(USER_NAME_VATSAL);

        Assertions.assertEquals(true, statusResponse.getStatus());
    }

    @Test
    void testSubmitThrottleLimitExceeded() {
        Map<String, Queue<Long>> queueMap = new HashMap<>();
        Queue<Long> queue = new LinkedList<>();
        for(int i=1 ; i<=6 ; i++) {
            queue.add(Instant.now().getEpochSecond());
        }
        queueMap.put(USER_NAME_VATSAL, queue);
        ReflectionTestUtils.setField(slidingWindowService, "queueMap", queueMap);

        StatusResponse statusResponse = slidingWindowService.submitRequest(USER_NAME_VATSAL);

        Assertions.assertEquals(false, statusResponse.getStatus());
    }

    @Test
    void testDeregisterUser() {
        Map<String, BucketQueueExecutor> queueMap = new HashMap<>();
        queueMap.put(USER_NAME_VATSAL, new BucketQueueExecutor());
        ReflectionTestUtils.setField(slidingWindowService, "queueMap", queueMap);
        StatusResponse statusResponse = slidingWindowService.deregisterUser(USER_NAME_VATSAL);
        Assertions.assertEquals(true, statusResponse.getStatus());
    }

    @Test
    void testDeregisterUserNotFound() {
        StatusResponse statusResponse = slidingWindowService.deregisterUser(USER_NAME_VATSAL);
        Assertions.assertEquals(false, statusResponse.getStatus());
        Assertions.assertEquals("user:imvtsl not found.", statusResponse.getMessage());
    }
}
