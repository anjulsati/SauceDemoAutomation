package com.saucedemo.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Central access point for the framework configuration.
 *
 * <p>Values are read from {@code config.properties} on the test classpath.
 * Any key can be overridden with a JVM system property, which makes the
 * same suite runnable locally, in CI, or in headless mode:</p>
 *
 * <pre>mvn test -Dheadless=true -Dbrowser=firefox</pre>
 */
public final class ConfigReader {

    private static final String CONFIG_FILE = "config.properties";
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = ConfigReader.class
                .getClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {

            if (input == null) {
                throw new IllegalStateException(
                        "Unable to find " + CONFIG_FILE + " on the test classpath");
            }

            PROPERTIES.load(input);

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to load " + CONFIG_FILE, e);
        }
    }

    private ConfigReader() {
        // Utility class
    }

    public static String get(String key) {
        String value = get(key, null);

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Missing configuration value for key: " + key);
        }

        return value;
    }

    public static String get(String key, String defaultValue) {
        String systemValue = System.getProperty(key);

        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue.trim();
        }

        String fileValue = PROPERTIES.getProperty(key);

        return fileValue == null ? defaultValue : fileValue.trim();
    }

    public static int getInt(String key, int defaultValue) {
        String value = get(key, String.valueOf(defaultValue));

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Configuration value for " + key
                            + " is not a number: " + value, e);
        }
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(
                get(key, String.valueOf(defaultValue)));
    }
}
