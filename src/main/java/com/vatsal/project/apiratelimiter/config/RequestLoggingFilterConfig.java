package com.vatsal.project.apiratelimiter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

/**
 * Filter for logging incoming REST server request and response details.
 * @author imvtsl
 * @since v1.0
 */

@Configuration
public class RequestLoggingFilterConfig {

    /**
     * Sets parameters to the filter.
     * @return CommonsRequestLoggingFilter
     */
    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        var filter
                = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(true);
        filter.setAfterMessagePrefix("REQUEST DATA : ");
        return filter;
    }
}
