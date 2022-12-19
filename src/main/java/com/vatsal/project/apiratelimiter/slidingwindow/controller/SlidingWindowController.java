package com.vatsal.project.apiratelimiter.slidingwindow.controller;

import com.vatsal.project.apiratelimiter.dto.StatusResponse;
import com.vatsal.project.apiratelimiter.slidingwindow.service.SlidingWindowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Pattern;

/**
 * A REST controller for handling all operations related to a Sliding Window based Rate Limiter.
 * @author imvtsl
 * @since v1.0
 */

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/slidingwindow/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class SlidingWindowController {
    @Autowired
    private SlidingWindowService slidingWindowService;

    @Operation(summary = "Submit an incoming request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Result of the request",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StatusResponse.class))}),
            @ApiResponse(responseCode = "429", description = "Too many requests in the defined window",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StatusResponse.class))})
    })
    @GetMapping(value = "/submit/{userName}")
    public ResponseEntity<StatusResponse> submitRequest(@PathVariable @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "'${validatedValue}' is not a valid user name. Valid user name matches regex: ^[A-Za-z0-9_]+$") String userName) {
        StatusResponse statusResponse = slidingWindowService.submitRequest(userName);
        if (Boolean.TRUE.equals(statusResponse.getStatus())) {
            return ResponseEntity.ok(statusResponse);
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(statusResponse);
        }
    }

    @Operation(summary = "Deregister the given user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted the given user",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StatusResponse.class))}),
            @ApiResponse(responseCode = "404", description = "The given user not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StatusResponse.class))})
    })
    @DeleteMapping(value = "/deregister/{userName}")
    public ResponseEntity<StatusResponse> deregisterUser(@PathVariable @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "'${validatedValue}' is not a valid user name. Valid user name matches regex: ^[A-Za-z0-9_]+$") String userName) {
        StatusResponse statusResponse = slidingWindowService.deregisterUser(userName);
        if (Boolean.TRUE.equals(statusResponse.getStatus())) {
            return ResponseEntity.ok(statusResponse);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(statusResponse);
        }
    }


}
