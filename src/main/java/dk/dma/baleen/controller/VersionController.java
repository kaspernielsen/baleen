package dk.dma.baleen.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VersionController {

    @GetMapping("/api/hello")
    public String helloWorld() {
        return "Hello World from Baleen!1may2025";
    }
}