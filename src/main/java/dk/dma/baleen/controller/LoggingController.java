package dk.dma.baleen.controller;

import dk.dma.baleen.logging.InMemoryLogAppender;
import dk.dma.baleen.logging.InMemoryLogAppender.LogEntry;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/logs")
public class LoggingController {

    @GetMapping
    public List<LogEntry> getLogs(@RequestParam(defaultValue = "1000") int limit) {
        // Return recent logs up to the specified limit
        return InMemoryLogAppender.getRecentLogs(limit);
    }

    @DeleteMapping
    public Map<String, String> clearLogs() {
        InMemoryLogAppender.clearLogs();
        return Map.of("message", "Logs cleared successfully");
    }

    @GetMapping("/count")
    public Map<String, Object> getLogCount() {
        return Map.of("count", InMemoryLogAppender.getLogCount());
    }
}