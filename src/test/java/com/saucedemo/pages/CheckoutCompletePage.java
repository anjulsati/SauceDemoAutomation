package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page object for the order confirmation screen.
 */
public class CheckoutCompletePage extends BasePage {

    public static final String URL_FRAGMENT = "checkout-complete.html";

    private static final By HEADER = By.className("complete-header");
    private static final By MESSAGE = By.className("complete-text");
    private static final By BACK_HOME_BUTTON = By.id("back-to-products");

    public CheckoutCompletePage(WebDriver driver) {
        super(driver);
    }

    public CheckoutCompletePage waitUntilLoaded() {
        waitForUrlContains(URL_FRAGMENT);
        visible(HEADER);
        return this;
    }

    public boolean isLoaded() {
        return getCurrentUrl().contains(URL_FRAGMENT)
                && isDisplayedImmediately(HEADER);
    }

    public String getConfirmationHeader() {
        return textOf(HEADER);
    }

    public String getConfirmationMessage() {
        return textOf(MESSAGE);
    }

    public InventoryPage backToHome() {
        click(BACK_HOME_BUTTON);
        return new InventoryPage(driver).waitUntilLoaded();
    }
}
