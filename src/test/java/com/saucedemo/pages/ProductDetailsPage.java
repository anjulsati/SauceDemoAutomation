package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page object for a single product's details screen.
 */
public class ProductDetailsPage extends ShopPage {

    public static final String URL_FRAGMENT = "inventory-item.html";

    private static final By PRODUCT_NAME =
            By.className("inventory_details_name");
    private static final By PRODUCT_DESCRIPTION =
            By.className("inventory_details_desc");
    private static final By PRODUCT_PRICE =
            By.className("inventory_details_price");
    private static final By BACK_TO_PRODUCTS = By.id("back-to-products");

    public ProductDetailsPage(WebDriver driver) {
        super(driver);
    }

    public ProductDetailsPage waitUntilLoaded() {
        waitForUrlContains(URL_FRAGMENT);
        visible(PRODUCT_NAME);
        return this;
    }

    public boolean isLoaded() {
        return getCurrentUrl().contains(URL_FRAGMENT)
                && isDisplayedImmediately(PRODUCT_NAME);
    }

    public String getProductName() {
        return textOf(PRODUCT_NAME);
    }

    public String getProductDescription() {
        return textOf(PRODUCT_DESCRIPTION);
    }

    public double getProductPrice() {
        return parseMoney(textOf(PRODUCT_PRICE));
    }

    public ProductDetailsPage addToCart() {
        click(actionButton("Add to cart"));
        return this;
    }

    public ProductDetailsPage removeFromCart() {
        click(actionButton("Remove"));
        return this;
    }

    public boolean isAddToCartDisplayed() {
        return isDisplayedImmediately(actionButton("Add to cart"));
    }

    public boolean isRemoveDisplayed() {
        return isDisplayedImmediately(actionButton("Remove"));
    }

    public InventoryPage backToProducts() {
        click(BACK_TO_PRODUCTS);
        return new InventoryPage(driver).waitUntilLoaded();
    }

    private By actionButton(String label) {
        return By.xpath(
                "//div[contains(@class,'inventory_details_desc_container')]"
                        + "//button[normalize-space(.)='" + label + "']");
    }
}
