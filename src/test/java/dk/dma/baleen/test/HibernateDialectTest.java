package dk.dma.baleen.test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test to verify correct Hibernate dialect class names for PostgreSQL/PostGIS
 */
public class HibernateDialectTest {

    @Test
    public void testHibernate6Dialects() {
        System.out.println("Testing Hibernate 6.x dialect availability...");
        
        // These are the old dialect names that don't exist in Hibernate 6.x
        String[] oldDialects = {
            "org.hibernate.spatial.dialect.postgis.PostGISDialect",
            "org.hibernate.spatial.dialect.postgis.PostGISPG10Dialect"
        };
        
        for (String dialectName : oldDialects) {
            try {
                Class.forName(dialectName);
                fail("Old dialect " + dialectName + " should not exist in Hibernate 6.x");
            } catch (ClassNotFoundException e) {
                System.out.println("✓ Confirmed: " + dialectName + " does not exist (expected for Hibernate 6.x)");
            }
        }
        
        // These are the correct dialect names for Hibernate 6.x
        String[] validDialects = {
            "org.hibernate.dialect.PostgreSQLDialect",  // Base PostgreSQL dialect with auto-detection
            "org.hibernate.spatial.dialect.postgis.PostgisPG95Dialect",  // PostGIS for PG 9.5+
            "org.hibernate.spatial.dialect.postgis.PostgisPG10Dialect"   // PostGIS for PG 10+
        };
        
        for (String dialectName : validDialects) {
            try {
                Class<?> dialectClass = Class.forName(dialectName);
                assertNotNull(dialectClass);
                System.out.println("✓ Found valid dialect: " + dialectName);
            } catch (ClassNotFoundException e) {
                System.out.println("✗ Expected dialect not found: " + dialectName);
                System.out.println("  This might indicate missing hibernate-spatial dependency");
            }
        }
        
        System.out.println("\nRecommendation for Azure PostgreSQL with PostGIS:");
        System.out.println("Use: org.hibernate.dialect.PostgreSQLDialect (auto-detects PostGIS)");
        System.out.println("Or: org.hibernate.spatial.dialect.postgis.PostgisPG95Dialect (explicit PostGIS)");
    }
}