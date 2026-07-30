package br.com.logicore.modules.health.service;

import br.com.logicore.modules.health.dto.HealthResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class HealthService {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${application.version}")
    private String applicationVersion;

    @Value("${application.environment}")
    private String environment;

    public HealthResponse health() {
        return new HealthResponse(
                "UP",
                applicationName,
                applicationVersion,
                environment,
                OffsetDateTime.now()
        );
    }
}