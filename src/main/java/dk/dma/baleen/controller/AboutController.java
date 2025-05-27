package dk.dma.baleen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/about")
public class AboutController {

    private final DataSource dataSource;
    
    @Value("${info.app.version:0.1-SNAPSHOT}")
    private String version;

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String databaseUsername;

    @Value("${spring.datasource.driver-class-name:}")
    private String driverClassName;

    @Autowired
    public AboutController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("")
    public ResponseEntity<Map<String, String>> getAboutInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("backendUrl", ""); // Will be set by frontend using window.location.origin
        info.put("version", version);
        return ResponseEntity.ok(info);
    }

    @GetMapping("/database")
    public ResponseEntity<Map<String, String>> getDatabaseInfo() {
        Map<String, String> info = new HashMap<>();
        
        // Add basic configuration info
        info.put("url", sanitizeUrl(databaseUrl));
        info.put("username", databaseUsername);
        info.put("driverClassName", driverClassName);
        
        // Try to get database metadata
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            info.put("databaseProductName", metaData.getDatabaseProductName());
            info.put("databaseProductVersion", metaData.getDatabaseProductVersion());
            info.put("connectionStatus", "Connected");
        } catch (SQLException e) {
            info.put("databaseProductName", "Unknown");
            info.put("databaseProductVersion", "Unknown");
            info.put("connectionStatus", "Error: " + e.getMessage());
        }
        
        return ResponseEntity.ok(info);
    }

    @PostMapping("/database/test")
    public ResponseEntity<Map<String, Object>> testDatabaseConnection() {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            // Test the connection by executing a simple query
            connection.createStatement().execute("SELECT 1");
            
            result.put("success", true);
            result.put("message", "Database connection successful");
            
            // Add some metadata
            DatabaseMetaData metaData = connection.getMetaData();
            result.put("databaseInfo", Map.of(
                "product", metaData.getDatabaseProductName(),
                "version", metaData.getDatabaseProductVersion(),
                "driver", metaData.getDriverName() + " " + metaData.getDriverVersion()
            ));
        } catch (SQLException e) {
            result.put("success", false);
            result.put("message", "Database connection failed: " + e.getMessage());
            result.put("errorCode", e.getErrorCode());
            result.put("sqlState", e.getSQLState());
        }
        
        return ResponseEntity.ok(result);
    }
    
    private String sanitizeUrl(String url) {
        // Hide password if present in URL
        if (url != null && url.contains("password=")) {
            return url.replaceAll("password=[^&]*", "password=***");
        }
        return url;
    }
}