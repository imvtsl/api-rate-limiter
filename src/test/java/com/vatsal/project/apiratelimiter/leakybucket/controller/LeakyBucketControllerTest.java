package com.vatsal.project.apiratelimiter.leakybucket.controller;

import com.vatsal.project.apiratelimiter.dto.StatusResponse;
import com.vatsal.project.apiratelimiter.leakybucket.service.LeakyBucketService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LeakyBucketController.class)
class LeakyBucketControllerTest {

    private static final String BASE_URL = "/leakybucket/v1";
    private static final String SUBMIT_REQUEST_URL = "/submit/";
    private static final String DEREGISTER_REQUEST_URL = "/deregister/";
    private static final String USER_NAME_VATSAL = "imvtsl";
    private static final String USER_NAME_INVALID = "/-)";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    LeakyBucketService leakyBucketService;

    @Test
    void testSubmitRequest() throws Exception {
        StatusResponse statusResponse = new StatusResponse(true);
        Mockito.when(leakyBucketService.submitRequest(Mockito.anyString())).thenReturn(statusResponse);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + SUBMIT_REQUEST_URL + USER_NAME_VATSAL)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONAssert.assertEquals(
                "{\"status\":true}", response, JSONCompareMode.LENIENT);
    }

    @Test
    void testSubmitRequestInvalidUserName() throws Exception {
        StatusResponse statusResponse = new StatusResponse(true);
        Mockito.when(leakyBucketService.submitRequest(Mockito.anyString())).thenReturn(statusResponse);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + SUBMIT_REQUEST_URL + USER_NAME_INVALID)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONAssert.assertEquals(
                "{\"requestViolations\":[{\"fieldName\":\"-)\",\"errorMessage\":\"'-)' is not a valid user name. Valid user name matches regex: ^[A-Za-z0-9_]+$\"}]}", response, JSONCompareMode.LENIENT);
    }

    @Test
    void testDeregisterUser() throws Exception {
        StatusResponse statusResponse = new StatusResponse(true);
        Mockito.when(leakyBucketService.deregisterUser(Mockito.anyString())).thenReturn(statusResponse);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + DEREGISTER_REQUEST_URL + USER_NAME_VATSAL)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONAssert.assertEquals(
                "{\"status\":true}", response, JSONCompareMode.LENIENT);
    }

    @Test
    void testDeregisterUserNotFound() throws Exception {
        StatusResponse statusResponse = new StatusResponse(false, "user:" + USER_NAME_VATSAL + " not found.");
        Mockito.when(leakyBucketService.deregisterUser(Mockito.anyString())).thenReturn(statusResponse);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + DEREGISTER_REQUEST_URL + USER_NAME_VATSAL)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONAssert.assertEquals(
                "{\"status\":false,\"message\":\"user:imvtsl not found.\"}", response, JSONCompareMode.LENIENT);
    }
}
