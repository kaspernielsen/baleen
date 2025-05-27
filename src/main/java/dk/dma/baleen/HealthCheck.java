package dk.dma.baleen;

public class HealthCheck {
    public static void main(String[] args) {
        System.out.println("Container started successfully!");
        System.out.println("Java version: " + System.getProperty("java.version"));
        System.out.println("SPRING_PROFILES_ACTIVE: " + System.getenv("SPRING_PROFILES_ACTIVE"));
        
        // Keep container alive
        try {
            Thread.sleep(300000); // 5 minutes
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}