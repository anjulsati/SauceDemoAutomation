package com.saucedemo.pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.saucedemo.config.ConfigReader;

/**
 * Shared behaviour for every page object: explicit waits and small,
 * readable helpers around the raw WebDriver API.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(
                        ConfigReader.getInt("timeout.seconds", 15)));
    }

    protected WebElement visible(By locator) {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement clickable(By locator) {
        return wait.until(
                ExpectedConditions.elementToBeClickable(locator));
    }

    protected void click(By locator) {
        clickable(locator).click();
    }

    /**
     * Clicks through JavaScript. Useful for animated overlay controls that
     * the browser reports as covered even though they are visible.
     */
    protected void jsClick(By locator) {
        WebElement element = clickable(locator);
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", element);
    }

    protected void type(By locator, String text) {
        WebElement element = visible(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected String textOf(By locator) {
        return visible(locator).getText().trim();
    }

    protected String attributeOf(By locator, String attribute) {
        return visible(locator).getAttribute(attribute);
    }

    /** Waits for an element and returns whether it is displayed. */
    protected boolean isVisible(By locator) {
        try {
            return visible(locator).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Checks visibility without waiting. Use this for negative assertions so
     * a missing element does not cost a full timeout.
     */
    protected boolean isDisplayedImmediately(By locator) {
        List<WebElement> elements = driver.findElements(locator);
        return !elements.isEmpty() && elements.get(0).isDisplayed();
    }

    protected void waitForUrlContains(String fragment) {
        wait.until(ExpectedConditions.urlContains(fragment));
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    /** Parses values such as {@code "Item total: $29.99"} into {@code 29.99}. */
    protected static double parseMoney(String value) {
        return Double.parseDouble(value.replaceAll("[^0-9.]", ""));
    }
}
