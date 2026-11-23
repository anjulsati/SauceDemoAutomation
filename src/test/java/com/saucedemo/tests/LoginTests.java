package com.saucedemo.tests;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;

/**
 * Authentication coverage: valid login, negative credentials, locked and
 * unknown users, validation messages, session handling and logout.
 */
public class LoginTests extends BaseTest {

    @Test(groups = {"smoke", "login"},
            description = "Valid credentials open the inventory page")
    public void validLoginNavigatesToInventory() {

        InventoryPage inventory = loginAsStandardUser();

        Assert.assertTrue(inventory.isLoaded(),
                "Inventory page should load after valid login");
        Assert.assertEquals(inventory.getHeading(), "Products");
        Assert.assertEquals(inventory.getProductCount(), 6);
    }

    @Test(groups = {"login", "regression"},
            description = "Wrong password is rejected with a clear error")
    public void invalidPasswordShowsError() {

        LoginPage login = new LoginPage(driver)
                .open()
                .loginExpectingFailure(VALID_USER, "wrong_password");

        Assert.assertTrue(login.isErrorDisplayed());
        Assert.assertTrue(
                login.getErrorMessage().contains(
                        "Username and password do not match"),
                "Unexpected error: " + login.getErrorMessage());
        Assert.assertFalse(driver.getCurrentUrl().contains("inventory.html"));
    }

    @Test(groups = {"login", "regression"},
            description = "Unknown username is rejected")
    public void unknownUsernameShowsError() {

        LoginPage login = new LoginPage(driver)
                .open()
                .loginExpectingFailure("unknown_user", PASSWORD);

        Assert.assertTrue(login.isErrorDisplayed());
        Assert.assertTrue(
                login.getErrorMessage().contains(
                        "Username and password do not match"));
    }

    @Test(groups = {"login", "regression"},
            description = "Locked-out user cannot sign in")
    public void lockedOutUserShowsError() {

        LoginPage login = new LoginPage(driver)
                .open()
                .loginExpectingFailure("locked_out_user", PASSWORD);

        Assert.assertTrue(login.isErrorDisplayed());
        Assert.assertTrue(
                login.getErrorMessage().contains("locked out"),
                "Unexpected error: " + login.getErrorMessage());
        Assert.assertFalse(driver.getCurrentUrl().contains("inventory.html"));
    }

    @Test(groups = {"login", "validation"},
            description = "Empty username triggers the required-field error")
    public void emptyUsernameShowsValidationError() {

        LoginPage login = new LoginPage(driver)
                .open()
                .loginExpectingFailure("", PASSWORD);

        Assert.assertTrue(
                login.getErrorMessage().contains("Username is required"));
    }

    @Test(groups = {"login", "validation"},
            description = "Empty password triggers the required-field error")
    public void emptyPasswordShowsValidationError() {

        LoginPage login = new LoginPage(driver)
                .open()
                .loginExpectingFailure(VALID_USER, "");

        Assert.assertTrue(
                login.getErrorMessage().contains("Password is required"));
    }

    @Test(groups = {"login", "validation"},
            description = "Submitting the empty form reports the first missing field")
    public void emptyFormShowsValidationError() {

        LoginPage login = new LoginPage(driver)
                .open()
                .loginExpectingFailure("", "");

        Assert.assertTrue(login.isErrorDisplayed());
        Assert.assertTrue(
                login.getErrorMessage().contains("Username is required"));
    }

    @Test(groups = {"login", "regression"},
            description = "The error banner can be dismissed")
    public void errorMessageCanBeDismissed() {

        LoginPage login = new LoginPage(driver)
                .open()
                .loginExpectingFailure(VALID_USER, "wrong_password");

        Assert.assertTrue(login.isErrorDisplayed());

        login.dismissError();

        Assert.assertFalse(login.isErrorDisplayed(),
                "Error banner should disappear after dismissal");
    }

    @Test(dataProvider = "invalidCredentials",
            groups = {"login", "security"},
            description = "Invalid credential combinations never authenticate")
    public void invalidCredentialsAreRejected(
            String username, String password) {

        LoginPage login = new LoginPage(driver)
                .open()
                .loginExpectingFailure(username, password);

        Assert.assertTrue(login.isErrorDisplayed(),
                "An error should be shown for: " + username);
        Assert.assertFalse(
                driver.getCurrentUrl().contains("inventory.html"),
                "User must not reach the inventory page");
    }

    @DataProvider(name = "invalidCredentials")
    public Object[][] invalidCredentials() {
        return new Object[][]{
                {"standard_user", "wrong_password"},
                {"unknown_user", PASSWORD},
                {"standard_user", ""},
                {"", PASSWORD}
        };
    }

    @Test(groups = {"login", "security"},
            description = "SQL-injection style input is treated as plain text")
    public void sqlInjectionInputIsRejected() {

        LoginPage login = new LoginPage(driver)
                .open()
                .loginExpectingFailure(
                        "' OR '1'='1", "' OR '1'='1");

        Assert.assertTrue(login.isErrorDisplayed());
        Assert.assertFalse(
                driver.getCurrentUrl().contains("inventory.html"));
    }

    @Test(groups = {"login", "validation"},
            description = "Password input is masked")
    public void passwordFieldMasksInput() {

        LoginPage login = new LoginPage(driver).open();

        Assert.assertEquals(login.getPasswordFieldType(), "password");
        Assert.assertEquals(login.getUsernameFieldType(), "text");
    }

    @Test(groups = {"login", "smoke"},
            description = "Login screen displays branding and required controls")
    public void loginPageDisplaysBranding() {

        LoginPage login = new LoginPage(driver).open();

        Assert.assertEquals(login.getLogoText(), "Swag Labs");
        Assert.assertEquals(login.getPageTitle(), "Swag Labs");
        Assert.assertTrue(login.isLoginButtonDisplayed());
        Assert.assertEquals(login.getLoginButtonValue(), "Login");
    }

    @Test(groups = {"login", "security"},
            description = "Direct access to a protected page shows the login guard")
    public void protectedPageRequiresAuthentication() {

        driver.get(BASE_URL + "inventory.html");

        String error = new LoginPage(driver)
                .waitUntilLoaded()
                .getErrorMessage();

        Assert.assertTrue(
                error.contains("when you are logged in"),
                "Unexpected guard message: " + error);
        Assert.assertEquals(
                driver.findElements(
                        org.openqa.selenium.By.className("inventory_item"))
                        .size(),
                0,
                "Inventory items must not be visible without a session");
    }

    @Test(groups = {"login", "smoke"},
            description = "Logging out returns the user to the login screen")
    public void logoutReturnsToLogin() {

        LoginPage login = loginAsStandardUser().logout();

        Assert.assertTrue(login.isLoaded(),
                "Login page should be shown after logout");
        Assert.assertFalse(
                driver.getCurrentUrl().contains("inventory.html"));
    }

    @Test(groups = {"login", "security"},
            description = "A logged-out session cannot reopen the inventory page")
    public void sessionIsInvalidAfterLogout() {

        loginAsStandardUser().logout();

        driver.get(BASE_URL + "inventory.html");

        String error = new LoginPage(driver)
                .waitUntilLoaded()
                .getErrorMessage();

        Assert.assertTrue(
                error.contains("when you are logged in"),
                "Unexpected guard message: " + error);
    }
}
