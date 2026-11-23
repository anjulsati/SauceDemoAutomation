package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page object for checkout step one: customer information.
 */
public class CheckoutStepOnePage extends BasePage {

    public static final String URL_FRAGMENT = "checkout-step-one.html";

    private static final By FIRST_NAME = By.id("first-name");
    private static final By LAST_NAME = By.id("last-name");
    private static final By POSTAL_CODE = By.id("postal-code");
    private static final By CONTINUE_BUTTON = By.id("continue");
    private static final By CANCEL_BUTTON = By.id("cancel");
    private static final By ERROR_MESSAGE = By.cssSelector("[data-test='error']");
    private static final By PAGE_TITLE = By.className("title");

    public CheckoutStepOnePage(WebDriver driver) {
        super(driver);
    }

    public CheckoutStepOnePage waitUntilLoaded() {
        waitForUrlContains(URL_FRAGMENT);
        visible(FIRST_NAME);
        return this;
    }

    public boolean isLoaded() {
        return getCurrentUrl().contains(URL_FRAGMENT)
                && isDisplayedImmediately(FIRST_NAME);
    }

    public String getHeading() {
        return textOf(PAGE_TITLE);
    }

    public CheckoutStepOnePage fillCustomerInformation(
            String firstName, String lastName, String postalCode) {

        type(FIRST_NAME, firstName);
        type(LAST_NAME, lastName);
        type(POSTAL_CODE, postalCode);

        return this;
    }

    public CheckoutStepOnePage clickContinue() {
        click(CONTINUE_BUTTON);
        return this;
    }

    public CheckoutStepOnePage waitForError() {
        visible(ERROR_MESSAGE);
        return this;
    }

    public CheckoutStepTwoPage continueToOverview() {
        click(CONTINUE_BUTTON);
        return new CheckoutStepTwoPage(driver).waitUntilLoaded();
    }

    public CartPage cancel() {
        click(CANCEL_BUTTON);
        return new CartPage(driver).waitUntilLoaded();
    }

    public boolean isErrorDisplayed() {
        return isDisplayedImmediately(ERROR_MESSAGE);
    }

    public String getErrorMessage() {
        return textOf(ERROR_MESSAGE);
    }

    public boolean isContinueButtonDisplayed() {
        return isDisplayedImmediately(CONTINUE_BUTTON);
    }

    public boolean isCancelButtonDisplayed() {
        return isDisplayedImmediately(CANCEL_BUTTON);
    }
}
