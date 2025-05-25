package dk.dma.baleen.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.List;
import java.util.ArrayList;

public class InMemoryLogAppender extends AppenderBase<ILoggingEvent> {
    
    private static final int MAX_LOGS = 10000; // Keep last 10000 log entries
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    // Use a thread-safe deque to store logs
    private static final ConcurrentLinkedDeque<LogEntry> logDeque = new ConcurrentLinkedDeque<>();
    
    @Override
    protected void append(ILoggingEvent event) {
        if (!isStarted()) {
            return;
        }
        
        LogEntry logEntry = new LogEntry(
            Instant.ofEpochMilli(event.getTimeStamp()).atZone(ZoneId.systemDefault()).format(DATE_FORMATTER),
            event.getLevel().toString(),
            event.getLoggerName(),
            event.getFormattedMessage(),
            event.getThreadName()
        );
        
        logDeque.addLast(logEntry);
        
        // Remove oldest entries if we exceed max size
        while (logDeque.size() > MAX_LOGS) {
            logDeque.removeFirst();
        }
    }
    
    public static List<LogEntry> getLogs() {
        return new ArrayList<>(logDeque);
    }
    
    public static List<LogEntry> getRecentLogs(int count) {
        List<LogEntry> result = new ArrayList<>();
        int skip = Math.max(0, logDeque.size() - count);
        int index = 0;
        
        for (LogEntry entry : logDeque) {
            if (index++ >= skip) {
                result.add(entry);
            }
        }
        
        return result;
    }
    
    public static void clearLogs() {
        logDeque.clear();
    }
    
    public static int getLogCount() {
        return logDeque.size();
    }
    
    public static class LogEntry {
        private final String timestamp;
        private final String level;
        private final String logger;
        private final String message;
        private final String thread;
        
        public LogEntry(String timestamp, String level, String logger, String message, String thread) {
            this.timestamp = timestamp;
            this.level = level;
            this.logger = logger;
            this.message = message;
            this.thread = thread;
        }
        
        // Getters
        public String getTimestamp() { return timestamp; }
        public String getLevel() { return level; }
        public String getLogger() { return logger; }
        public String getMessage() { return message; }
        public String getThread() { return thread; }
    }
}