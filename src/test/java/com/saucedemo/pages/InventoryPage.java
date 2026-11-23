package com.saucedemo.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * Page object for the product inventory screen, including sorting and
 * add-to-cart actions.
 */
public class InventoryPage extends ShopPage {

    public static final String URL_FRAGMENT = "inventory.html";

    private static final By INVENTORY_LIST = By.className("inventory_list");
    private static final By INVENTORY_ITEMS = By.className("inventory_item");
    private static final By PRODUCT_NAMES = By.className("inventory_item_name");
    private static final By PRODUCT_PRICES = By.className("inventory_item_price");
    private static final By SORT_DROPDOWN = By.className("product_sort_container");

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public InventoryPage waitUntilLoaded() {
        waitForUrlContains(URL_FRAGMENT);
        visible(INVENTORY_LIST);
        return this;
    }

    /** Waits until all six product images have rendered with a non-zero size. */
    public InventoryPage waitForProductImages() {

        wait.until(d -> {
            List<WebElement> images = d.findElements(
                    By.cssSelector(".inventory_item img"));
            return images.size() == 6
                    && images.stream().allMatch(image -> {
                        try {
                            return image.isDisplayed()
                                    && image.getSize().getWidth() > 0;
                        } catch (RuntimeException e) {
                            return false;
                        }
                    });
        });

        return this;
    }

    public boolean isLoaded() {
        return getCurrentUrl().contains(URL_FRAGMENT)
                && isDisplayedImmediately(INVENTORY_LIST);
    }

    // ------------------------------------------------------------------
    // Products
    // ------------------------------------------------------------------

    public int getProductCount() {
        visible(INVENTORY_LIST);
        return driver.findElements(INVENTORY_ITEMS).size();
    }

    public List<String> getProductNames() {
        visible(INVENTORY_LIST);

        return driver.findElements(PRODUCT_NAMES)
                .stream()
                .map(element -> element.getText().trim())
                .toList();
    }

    public List<Double> getProductPrices() {
        visible(INVENTORY_LIST);

        return driver.findElements(PRODUCT_PRICES)
                .stream()
                .map(element -> Double.parseDouble(
                        element.getText().replace("$", "").trim()))
                .toList();
    }

    public double getProductPrice(String productName) {

        By price = By.xpath(itemXpath(productName)
                + "//div[contains(@class,'inventory_item_price')]");

        return parseMoney(textOf(price));
    }

    public boolean hasDescription(String productName) {

        By description = By.xpath(itemXpath(productName)
                + "//div[contains(@class,'inventory_item_desc')]");

        return !textOf(description).isBlank();
    }

    public boolean hasImage(String productName) {
        return isDisplayedImmediately(By.xpath(itemXpath(productName)
                + "//img"));
    }

    // ------------------------------------------------------------------
    // Sorting
    // ------------------------------------------------------------------

    public InventoryPage sortBy(String optionValue) {

        new Select(clickable(SORT_DROPDOWN))
                .selectByValue(optionValue);

        String expectedFirst;
        By firstCell;

        switch (optionValue) {
            case "za" -> {
                expectedFirst = "Test.allTheThings() T-Shirt (Red)";
                firstCell = PRODUCT_NAMES;
            }
            case "lohi" -> {
                expectedFirst = "$7.99";
                firstCell = PRODUCT_PRICES;
            }
            case "hilo" -> {
                expectedFirst = "$49.99";
                firstCell = PRODUCT_PRICES;
            }
            default -> {
                expectedFirst = "Sauce Labs Backpack";
                firstCell = PRODUCT_NAMES;
            }
        }

        wait.until(d -> d.findElement(firstCell)
                .getText()
                .trim()
                .equals(expectedFirst));

        return this;
    }

    // ------------------------------------------------------------------
    // Cart actions
    // ------------------------------------------------------------------

    public InventoryPage addProduct(String productName) {
        click(productButton(productName, "Add to cart"));
        return this;
    }

    public InventoryPage removeProduct(String productName) {
        click(productButton(productName, "Remove"));
        return this;
    }

    public InventoryPage addAllProducts() {

        for (String name : getProductNames()) {
            addProduct(name);
        }

        return waitForCartBadgeCount(6);
    }

    @Override
    public InventoryPage waitForCartBadgeCount(int expected) {
        super.waitForCartBadgeCount(expected);
        return this;
    }

    public boolean isAddButtonDisplayed(String productName) {
        return isDisplayedImmediately(
                productButton(productName, "Add to cart"));
    }

    public boolean isRemoveButtonDisplayed(String productName) {
        return isDisplayedImmediately(
                productButton(productName, "Remove"));
    }

    // ------------------------------------------------------------------
    // Navigation
    // ------------------------------------------------------------------

    public ProductDetailsPage openProduct(String productName) {
        click(By.xpath(itemXpath(productName)
                + "//div[contains(@class,'inventory_item_name')]"));

        return new ProductDetailsPage(driver).waitUntilLoaded();
    }

    // ------------------------------------------------------------------
    // Locator builders
    // ------------------------------------------------------------------

    private String itemXpath(String productName) {
        return "//div[contains(@class,'inventory_item')]"
                + "[.//div[contains(@class,'inventory_item_name')"
                + " and normalize-space(.)='"
                + productName + "']]";
    }

    private By productButton(String productName, String label) {
        return By.xpath(itemXpath(productName)
                + "//button[normalize-space(.)='" + label + "']");
    }
}
