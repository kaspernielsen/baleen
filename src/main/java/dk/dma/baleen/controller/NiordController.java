package dk.dma.baleen.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class NiordController {

    @Value("${niord.endpoint:}")
    private String niordEndpoint;

    @GetMapping("/api/niord/config")
    public Map<String, String> getNiordConfig() {
        if (niordEndpoint == null || niordEndpoint.trim().isEmpty()) {
            return Map.of();
        }
        
        return Map.of("endpoint", niordEndpoint);
    }
}