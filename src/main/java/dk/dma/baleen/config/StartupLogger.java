package dk.dma.baleen.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StartupLogger {
    
    private static final Logger log = LoggerFactory.getLogger(StartupLogger.class);
    
    @Value("${spring.datasource.url}")
    private String databaseUrl;
    
    @Value("${spring.profiles.active:default}")
    private String activeProfile;
    
    @Bean
    public CommandLineRunner logStartup() {
        return args -> {
            log.info("===========================================");
            log.info("Baleen Application Starting");
            log.info("Active Profile: {}", activeProfile);
            log.info("Database URL: {}", databaseUrl);
            log.info("Environment DATABASE_PASSWORD is set: {}", 
                System.getenv("DATABASE_PASSWORD") != null ? "YES" : "NO");
            log.info("===========================================");
        };
    }
}