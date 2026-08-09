package com.etlions.webchat.dto;

public record HealthCheckResponse(String status, String service, String timestamp) {
}
