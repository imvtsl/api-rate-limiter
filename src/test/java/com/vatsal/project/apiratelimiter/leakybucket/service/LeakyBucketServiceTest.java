package com.vatsal.project.apiratelimiter.leakybucket.service;

import com.vatsal.project.apiratelimiter.dto.StatusResponse;
import com.vatsal.project.apiratelimiter.leakybucket.model.BucketQueueExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class LeakyBucketServiceTest {

    private static final String USER_NAME_VATSAL = "imvtsl";

    @Autowired
    LeakyBucketService leakyBucketService;

    @Test
    void testSubmitRequestNewUser() {
        StatusResponse statusResponse = leakyBucketService.submitRequest(USER_NAME_VATSAL);
        Assertions.assertEquals(true, statusResponse.getStatus());
    }

    @Test
    void testSubmitRequestExistingUser() {
        Map<String, BucketQueueExecutor> queueMap = new HashMap<>();
        queueMap.put(USER_NAME_VATSAL, new BucketQueueExecutor());
        ReflectionTestUtils.setField(leakyBucketService, "queueMap", queueMap);

        StatusResponse statusResponse = leakyBucketService.submitRequest(USER_NAME_VATSAL);

        Assertions.assertEquals(true, statusResponse.getStatus());
    }

    @Test
    void testDeregisterUser() {
        Map<String, BucketQueueExecutor> queueMap = new HashMap<>();
        queueMap.put(USER_NAME_VATSAL, new BucketQueueExecutor());
        ReflectionTestUtils.setField(leakyBucketService, "queueMap", queueMap);
        StatusResponse statusResponse = leakyBucketService.deregisterUser(USER_NAME_VATSAL);
        Assertions.assertEquals(true, statusResponse.getStatus());
    }

    @Test
    void testDeregisterUserNotFound() {
        StatusResponse statusResponse = leakyBucketService.deregisterUser(USER_NAME_VATSAL);
        Assertions.assertEquals(false, statusResponse.getStatus());
        Assertions.assertEquals("user:imvtsl not found.", statusResponse.getMessage());
    }
}
