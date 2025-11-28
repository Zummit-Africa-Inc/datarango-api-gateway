package com.datarango.gateway.dto;

import lombok.Data;
import java.time.Instant;

@Data
public abstract class BaseRequest {
    private String requestId;
    private Instant timestamp = Instant.now();
}