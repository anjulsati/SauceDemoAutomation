package com.saucedemo.tests;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;

/**
 * Defect-detection demonstration.
 *
 * <p>SauceDemo publishes four deliberately faulty accounts. These tests assert
 * the behaviour a correct shop should provide, so they fail against the
 * intentionally broken accounts. They are NOT part of the main regression
 * suite (testng.xml) and are excluded from the 64/64 result. Run them with
 * testng-defect-demo.xml to show that the framework can detect real defects.</p>
 */
public class DefectDetectionTests extends BaseTest {

    private static final String RED_TSHIRT =
            "Test.allTheThings() T-Shirt (Red)";

    private InventoryPage loginAs(String username) {
        return new LoginPage(driver).open().loginAs(username, PASSWORD);
    }

    @Test(groups = {"defect-demo"},
            description = "Problem user: all product images should be valid and distinct")
    public void problemUserProductImagesAreValid() {

        loginAs("problem_user");

        List<WebElement> images = driver.findElements(
                By.cssSelector(".inventory_item img"));

        Assert.assertEquals(images.size(), 6,
                "Expected six product images");

        long placeholders = images.stream()
                .map(image -> image.getAttribute("src"))
                .filter(src -> src != null && src.contains("sl-404"))
                .count();

        long distinct = images.stream()
                .map(image -> image.getAttribute("src"))
                .distinct()
                .count();

        Assert.assertEquals(placeholders, 0,
                "Defect: some product images use the 404 placeholder");
        Assert.assertEquals(distinct, 6,
                "Defect: product images are not distinct");
    }

    @Test(groups = {"defect-demo"},
            description = "Problem user: name Z-A sorting should reorder the catalogue")
    public void problemUserSortNameZToAWorks() {

        loginAs("problem_user");

        new Select(driver.findElement(By.className("product_sort_container")))
                .selectByValue("za");

        // Give the UI two seconds to reorder, then inspect the result.
        try {
            new WebDriverWait(driver, Duration.ofSeconds(2)).until(
                    d -> d.findElement(By.className("inventory_item_name"))
                            .getText().trim().equals(RED_TSHIRT));
        } catch (TimeoutException ignored) {
            // The assertion below reports the actual defect.
        }

        String firstProduct = driver.findElement(
                By.className("inventory_item_name")).getText().trim();

        Assert.assertEquals(firstProduct, RED_TSHIRT,
                "Defect: Z-A sorting did not place the expected product first");
    }

    @Test(groups = {"defect-demo"},
            description = "Problem user: removing a product should clear the cart badge")
    public void problemUserRemoveProductUpdatesCart() {

        InventoryPage inventory = loginAs("problem_user");

        inventory.addProduct("Sauce Labs Backpack")
                .waitForCartBadgeCount(1);

        inventory.removeProduct("Sauce Labs Backpack");

        try {
            new WebDriverWait(driver, Duration.ofSeconds(2)).until(
                    d -> d.findElements(By.className("shopping_cart_badge"))
                            .isEmpty());
        } catch (TimeoutException ignored) {
            // The assertion below reports the actual defect.
        }

        int badge = inventory.getCartBadgeCount();

        Assert.assertEquals(badge, 0,
                "Defect: cart badge still shows items after removal");
    }

    @Test(groups = {"defect-demo"},
            description = "Error user: sorting should not raise an application alert")
    public void errorUserSortingDoesNotRaiseAlert() {

        loginAs("error_user");

        new Select(driver.findElement(By.className("product_sort_container")))
                .selectByValue("za");

        try {
            new WebDriverWait(driver, Duration.ofSeconds(3)).until(
                    ExpectedConditions.alertIsPresent());

            String alertText = driver.switchTo().alert().getText();
            driver.switchTo().alert().dismiss();

            Assert.fail("Defect: sorting raised an application alert: "
                    + alertText);

        } catch (TimeoutException e) {
            // No alert is the correct behaviour.
        }
    }

    @Test(groups = {"defect-demo"},
            description = "Visual user: product images should not use the 404 placeholder")
    public void visualUserProductImagesAreValid() {

        loginAs("visual_user");

        List<WebElement> images = driver.findElements(
                By.cssSelector(".inventory_item img"));

        long placeholders = images.stream()
                .map(image -> image.getAttribute("src"))
                .filter(src -> src != null && src.contains("sl-404"))
                .count();

        Assert.assertEquals(placeholders, 0,
                "Defect: a product image is rendered with the 404 placeholder");
    }

    @Test(groups = {"defect-demo"},
            description = "Performance-glitch user: inventory should load within a 3 s budget")
    public void performanceGlitchUserLoadsWithinBudget() {

        new LoginPage(driver).open();

        driver.findElement(By.id("user-name")).sendKeys("performance_glitch_user");
        driver.findElement(By.id("password")).sendKeys(PASSWORD);

        long start = System.currentTimeMillis();
        driver.findElement(By.id("login-button")).click();
        new InventoryPage(driver).waitUntilLoaded();
        long elapsed = System.currentTimeMillis() - start;

        Assert.assertTrue(elapsed < 3000,
                "Defect: inventory took " + elapsed + " ms, above the 3 s budget");
    }
}
