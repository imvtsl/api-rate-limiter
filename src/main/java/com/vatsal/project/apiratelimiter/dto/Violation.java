package com.vatsal.project.apiratelimiter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

/**
 * DTO class for returning errors and exceptions details to client.
 * @author imvtsl
 * @since v1.0
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "fieldName",
        "errorMessage"
})

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Violation {
    private String fieldName;
    private String errorMessage;

    public Violation(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

