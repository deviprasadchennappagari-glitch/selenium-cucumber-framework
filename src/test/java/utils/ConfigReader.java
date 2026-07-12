package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads framework configuration from config.properties (classpath).
 * Any property can be overridden at runtime via -D system property,
 * e.g. mvn test -Dbrowser=firefox -Dheadless=true
 */
public final class ConfigReader {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = ConfigReader.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("config.properties not found on classpath");
            }
            PROPERTIES.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    private ConfigReader() {
    }

    public static String get(String key) {
        // System property takes precedence, allowing CLI overrides
        String override = System.getProperty(key);
        if (override != null && !override.isBlank()) {
            return override;
        }
        return PROPERTIES.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        String value = get(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        return value == null ? defaultValue : Boolean.parseBoolean(value);
    }

    public static int getInt(String key, int defaultValue) {
        String value = get(key);
        try {
            return value == null ? defaultValue : Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
