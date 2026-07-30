package br.com.logicore.modules.health.dto;

import java.time.OffsetDateTime;

public record HealthResponse(
        String status,
        String application,
        String version,
        String environment,
        OffsetDateTime timestamp
) {
}