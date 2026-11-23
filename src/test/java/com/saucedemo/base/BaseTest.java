package com.saucedemo.base;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.saucedemo.config.ConfigReader;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.utils.DriverFactory;

/**
 * Base class for every UI test.
 *
 * <p>Guarantees full isolation: each test receives a fresh browser and a
 * fresh session, then the browser is always closed, even when a test fails.</p>
 */
public abstract class BaseTest {

    protected static final String BASE_URL =
            ConfigReader.get("base.url");

    protected static final String VALID_USER =
            ConfigReader.get("standard.user", "standard_user");

    protected static final String PASSWORD =
            ConfigReader.get("password", "secret_sauce");

    protected static final int TIMEOUT_SECONDS =
            ConfigReader.getInt("timeout.seconds", 15);

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {

        driver = DriverFactory.createDriver();

        wait = new WebDriverWait(
                driver, Duration.ofSeconds(TIMEOUT_SECONDS));

        driver.get(BASE_URL);
    }

    /** Logs in with the standard user and returns the inventory page. */
    protected InventoryPage loginAsStandardUser() {

        return new LoginPage(driver)
                .open()
                .loginAs(VALID_USER, PASSWORD);
    }

    public WebDriver getDriver() {
        return driver;
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {

        try {
            if (driver != null) {
                driver.quit();
            }
        } finally {
            driver = null;
        }
    }
}
