package com.localdevops.hello_world_java;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public Map<String, Object> hello() {
        return Map.of(
                "message", "Hello from Java CI/CD pipeline",
                "application", "hello-world-java",
                "deployedBy", "Jenkins + Docker + Nexus",
                "timestamp", Instant.now().toString()
        );
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "Healthy",
                "application", "hello-world-java",
                "timestamp", Instant.now().toString()
        );
    }
}