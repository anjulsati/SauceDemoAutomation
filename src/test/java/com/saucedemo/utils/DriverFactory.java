package com.saucedemo.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import com.saucedemo.config.ConfigReader;

/**
 * Creates the browser instance requested in {@code config.properties}.
 *
 * <p>Selenium Manager (built into Selenium 4) resolves and downloads the
 * matching driver automatically, so no WebDriverManager dependency is
 * required.</p>
 */
public final class DriverFactory {

    static {
        /*
         * Chrome can be newer than the CDP version bundled with Selenium
         * (for example Chrome 157 vs the CDP 156 bundled with Selenium
         * 4.52). Selenium then logs a harmless warning for every browser
         * launch. Suppress only that logger until a matching Selenium
         * release is available; all other warnings still appear.
         */
        Logger.getLogger(
                        "org.openqa.selenium.devtools.CdpVersionFinder")
                .setLevel(Level.OFF);
    }

    private DriverFactory() {
        // Utility class
    }

    public static WebDriver createDriver() {

        String browser = ConfigReader
                .get("browser", "chrome")
                .toLowerCase();

        boolean headless = ConfigReader
                .getBoolean("headless", false);

        return switch (browser) {
            case "firefox" -> new FirefoxDriver(
                    firefoxOptions(headless));
            case "edge" -> new EdgeDriver(
                    edgeOptions(headless));
            default -> new ChromeDriver(
                    chromeOptions(headless));
        };
    }

    private static ChromeOptions chromeOptions(boolean headless) {

        ChromeOptions options = new ChromeOptions();

        options.addArguments(
                "--incognito",
                "--window-size=1920,1080",
                "--disable-notifications",
                "--disable-save-password-bubble");

        Map<String, Object> preferences = new HashMap<>();
        preferences.put("credentials_enable_service", false);
        preferences.put("profile.password_manager_enabled", false);
        preferences.put("profile.password_manager_leak_detection", false);

        options.setExperimentalOption("prefs", preferences);

        if (headless) {
            options.addArguments(
                    "--headless=new",
                    "--no-sandbox",
                    "--disable-dev-shm-usage",
                    "--disable-gpu");
        }

        return options;
    }

    private static FirefoxOptions firefoxOptions(boolean headless) {

        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--width=1920", "--height=1080");

        if (headless) {
            options.addArguments("-headless");
        }

        return options;
    }

    private static EdgeOptions edgeOptions(boolean headless) {

        EdgeOptions options = new EdgeOptions();
        options.addArguments("--window-size=1920,1080");

        if (headless) {
            options.addArguments(
                    "--headless=new",
                    "--no-sandbox",
                    "--disable-dev-shm-usage");
        }

        return options;
    }
}
