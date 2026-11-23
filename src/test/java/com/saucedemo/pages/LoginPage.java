package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.saucedemo.config.ConfigReader;

/**
 * Page object for the SauceDemo login screen.
 */
public class LoginPage extends BasePage {

    private static final By USERNAME = By.id("user-name");
    private static final By PASSWORD = By.id("password");
    private static final By LOGIN_BUTTON = By.id("login-button");
    private static final By LOGO = By.className("login_logo");
    private static final By ERROR_MESSAGE = By.cssSelector("[data-test='error']");
    private static final By ERROR_CLOSE_BUTTON = By.className("error-button");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        driver.get(ConfigReader.get("base.url"));
        return waitUntilLoaded();
    }

    public LoginPage waitUntilLoaded() {
        visible(USERNAME);
        visible(LOGIN_BUTTON);
        return this;
    }

    public boolean isLoaded() {
        return isDisplayedImmediately(USERNAME)
                && isDisplayedImmediately(LOGIN_BUTTON);
    }

    public InventoryPage loginAs(String username, String password) {

        type(USERNAME, username);
        type(PASSWORD, password);
        click(LOGIN_BUTTON);

        return new InventoryPage(driver).waitUntilLoaded();
    }

    public LoginPage loginExpectingFailure(
            String username, String password) {

        type(USERNAME, username);
        type(PASSWORD, password);
        click(LOGIN_BUTTON);

        visible(ERROR_MESSAGE);
        return this;
    }

    public boolean isErrorDisplayed() {
        return isDisplayedImmediately(ERROR_MESSAGE);
    }

    public String getErrorMessage() {
        return textOf(ERROR_MESSAGE);
    }

    public LoginPage dismissError() {

        click(ERROR_CLOSE_BUTTON);
        wait.until(ExpectedConditions
                .invisibilityOfElementLocated(ERROR_MESSAGE));

        return this;
    }

    public String getLogoText() {
        return textOf(LOGO);
    }

    public String getUsernameFieldType() {
        return attributeOf(USERNAME, "type");
    }

    public String getPasswordFieldType() {
        return attributeOf(PASSWORD, "type");
    }

    public String getLoginButtonValue() {
        return attributeOf(LOGIN_BUTTON, "value");
    }

    public boolean isLoginButtonDisplayed() {
        return isDisplayedImmediately(LOGIN_BUTTON);
    }
}
