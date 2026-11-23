package com.saucedemo.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page object for the shopping cart screen.
 */
public class CartPage extends ShopPage {

    public static final String URL_FRAGMENT = "cart.html";

    private static final By CART_LIST = By.className("cart_list");
    private static final By CART_ITEMS = By.className("cart_item");
    private static final By ITEM_NAMES =
            By.cssSelector(".cart_item .inventory_item_name");
    private static final By ITEM_PRICES =
            By.cssSelector(".cart_item .inventory_item_price");
    private static final By ITEM_QUANTITIES =
            By.cssSelector(".cart_item .cart_quantity");

    private static final By CHECKOUT_BUTTON = By.id("checkout");
    private static final By CONTINUE_SHOPPING_BUTTON = By.id("continue-shopping");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public CartPage waitUntilLoaded() {
        waitForUrlContains(URL_FRAGMENT);
        visible(CART_LIST);
        return this;
    }

    public boolean isLoaded() {
        return getCurrentUrl().contains(URL_FRAGMENT)
                && isDisplayedImmediately(CART_LIST);
    }

    public int getItemCount() {
        return driver.findElements(CART_ITEMS).size();
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public List<String> getItemNames() {
        return driver.findElements(ITEM_NAMES)
                .stream()
                .map(element -> element.getText().trim())
                .toList();
    }

    public List<Double> getItemPrices() {
        return driver.findElements(ITEM_PRICES)
                .stream()
                .map(element -> Double.parseDouble(
                        element.getText().replace("$", "").trim()))
                .toList();
    }

    public List<String> getItemQuantities() {
        return driver.findElements(ITEM_QUANTITIES)
                .stream()
                .map(element -> element.getText().trim())
                .toList();
    }

    public CartPage removeProduct(String productName) {
        click(removeButton(productName));
        return this;
    }

    public CartPage waitForItemCount(int expected) {
        wait.until(d -> d.findElements(CART_ITEMS).size() == expected);
        return this;
    }

    public CartPage removeAllItems() {

        while (!driver.findElements(CART_ITEMS).isEmpty()) {
            int before = getItemCount();
            click(By.cssSelector(".cart_item button"));
            wait.until(d -> d.findElements(CART_ITEMS).size() < before);
        }

        return this;
    }

    public CheckoutStepOnePage checkout() {
        click(CHECKOUT_BUTTON);
        return new CheckoutStepOnePage(driver).waitUntilLoaded();
    }

    public InventoryPage continueShopping() {
        click(CONTINUE_SHOPPING_BUTTON);
        return new InventoryPage(driver).waitUntilLoaded();
    }

    public boolean isCheckoutButtonDisplayed() {
        return isDisplayedImmediately(CHECKOUT_BUTTON);
    }

    public boolean isContinueShoppingButtonDisplayed() {
        return isDisplayedImmediately(CONTINUE_SHOPPING_BUTTON);
    }

    private By removeButton(String productName) {
        return By.xpath(
                "//div[contains(@class,'cart_item')]"
                        + "[.//div[contains(@class,'inventory_item_name')"
                        + " and normalize-space(.)='" + productName + "']]"
                        + "//button[normalize-space(.)='Remove']");
    }
}
