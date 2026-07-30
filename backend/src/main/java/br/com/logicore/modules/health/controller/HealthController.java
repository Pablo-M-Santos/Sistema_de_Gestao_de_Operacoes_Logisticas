package br.com.logicore.modules.health.controller;

import br.com.logicore.modules.health.dto.HealthResponse;
import br.com.logicore.modules.health.service.HealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/health")
@RequiredArgsConstructor
public class HealthController {

    private final HealthService healthService;

    @GetMapping
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(healthService.health());
    }
}