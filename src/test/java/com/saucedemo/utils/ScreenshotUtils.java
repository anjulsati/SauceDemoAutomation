package com.saucedemo.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * Captures screenshots and stores them under {@code target/screenshots}.
 */
public final class ScreenshotUtils {

    private static final DateTimeFormatter TIMESTAMP =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private ScreenshotUtils() {
        // Utility class
    }

    /** @return the screenshot encoded as Base64, ready for report embedding. */
    public static String captureBase64(WebDriver driver) {

        return ((TakesScreenshot) driver)
                .getScreenshotAs(OutputType.BASE64);
    }

    /**
     * Persists a Base64 screenshot on disk.
     *
     * @return the absolute file path, or {@code null} if saving failed
     */
    public static Path save(String base64, String testName) {

        try {
            Path directory = Paths.get("target", "screenshots");
            Files.createDirectories(directory);

            String safeName = testName
                    .replaceAll("[^a-zA-Z0-9-_]", "_");

            Path destination = directory.resolve(
                    safeName + "_"
                            + LocalDateTime.now().format(TIMESTAMP)
                            + ".png");

            Files.write(
                    destination,
                    Base64.getDecoder().decode(base64));

            return destination.toAbsolutePath();

        } catch (IOException | IllegalArgumentException e) {
            System.err.println(
                    "Unable to save failure screenshot: " + e.getMessage());
            return null;
        }
    }
}
