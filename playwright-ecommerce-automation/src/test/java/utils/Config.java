package utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Reads configuration from src/test/resources/config.properties.
 * Any value can be overridden with a JVM system property, e.g. -Dbrowser=firefox.
 */
public final class Config {

    private static final Properties PROPERTIES = load();

    private Config() {
    }

    private static Properties load() {
        Properties props = new Properties();
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not read config.properties", e);
        }
        return props;
    }

    private static String get(String key) {
        String override = System.getProperty(key);
        if (override != null && !override.isBlank()) {
            return override.trim();
        }
        String value = PROPERTIES.getProperty(key);
        if (value == null) {
            throw new IllegalStateException("Missing configuration key: " + key);
        }
        return value.trim();
    }

    public static String baseUrl() {
        return get("baseUrl");
    }

    public static String browser() {
        return get("browser").toLowerCase();
    }

    public static boolean headless() {
        return Boolean.parseBoolean(get("headless"));
    }

    public static double slowMo() {
        return Double.parseDouble(get("slowMo"));
    }

    /** Folder where failure screenshots and traces are written. */
    public static Path artifactsDir() {
        return Path.of("target", "artifacts");
    }
}
