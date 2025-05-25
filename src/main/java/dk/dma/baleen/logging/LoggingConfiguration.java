package dk.dma.baleen.logging;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class LoggingConfiguration {
    
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(LoggingConfiguration.class);
    
    @EventListener(ApplicationStartedEvent.class)
    public void onApplicationStarted() {
        // The logback.xml should have already configured our appender
        // Let's verify it's working
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        
        boolean hasInMemoryAppender = false;
        var iterator = rootLogger.iteratorForAppenders();
        while (iterator.hasNext()) {
            var appender = iterator.next();
            if (appender instanceof InMemoryLogAppender) {
                hasInMemoryAppender = true;
                log.info("InMemoryLogAppender found: {} (status: {})", 
                    appender.getName(), 
                    appender.isStarted() ? "STARTED" : "STOPPED");
            }
        }
        
        if (!hasInMemoryAppender) {
            log.warn("InMemoryLogAppender not found! Logs will not be captured in memory.");
        }
        
        log.info("=== Baleen application has started successfully ===");
        log.info("Logging system status: {} logs captured so far", InMemoryLogAppender.getLogCount());
        
        // Log some system information
        log.info("Java Version: {}", System.getProperty("java.version"));
        log.info("Available processors: {}", Runtime.getRuntime().availableProcessors());
        log.info("Max memory: {} MB", Runtime.getRuntime().maxMemory() / 1024 / 1024);
    }
}