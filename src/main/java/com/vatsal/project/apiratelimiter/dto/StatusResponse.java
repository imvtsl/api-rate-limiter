package com.vatsal.project.apiratelimiter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "status",
        "message"
})

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StatusResponse {
    @JsonProperty("status")
    private Boolean status;

    @JsonProperty("message")
    private String message;

    public StatusResponse(Boolean status) {
        this.status = status;
    }
}
