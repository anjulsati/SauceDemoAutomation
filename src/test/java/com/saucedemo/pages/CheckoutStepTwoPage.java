package com.saucedemo.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page object for checkout step two: order overview.
 */
public class CheckoutStepTwoPage extends BasePage {

    public static final String URL_FRAGMENT = "checkout-step-two.html";

    private static final By PAGE_TITLE = By.className("title");
    private static final By ITEM_TOTAL = By.className("summary_subtotal_label");
    private static final By TAX = By.className("summary_tax_label");
    private static final By TOTAL = By.className("summary_total_label");
    private static final By ITEM_NAMES =
            By.cssSelector(".cart_item .inventory_item_name");
    private static final By SUMMARY_VALUES = By.className("summary_value_label");
    private static final By FINISH_BUTTON = By.id("finish");
    private static final By CANCEL_BUTTON = By.id("cancel");

    public CheckoutStepTwoPage(WebDriver driver) {
        super(driver);
    }

    public CheckoutStepTwoPage waitUntilLoaded() {
        waitForUrlContains(URL_FRAGMENT);
        visible(ITEM_TOTAL);
        return this;
    }

    public boolean isLoaded() {
        return getCurrentUrl().contains(URL_FRAGMENT)
                && isDisplayedImmediately(ITEM_TOTAL);
    }

    public String getHeading() {
        return textOf(PAGE_TITLE);
    }

    public List<String> getItemNames() {
        return driver.findElements(ITEM_NAMES)
                .stream()
                .map(element -> element.getText().trim())
                .toList();
    }

    public double getItemTotal() {
        return parseMoney(textOf(ITEM_TOTAL));
    }

    public double getTax() {
        return parseMoney(textOf(TAX));
    }

    public double getTotal() {
        return parseMoney(textOf(TOTAL));
    }

    public String getPaymentAndShippingInformation() {
        return String.join(" | ",
                driver.findElements(SUMMARY_VALUES)
                        .stream()
                        .map(element -> element.getText().trim())
                        .toList());
    }

    public CheckoutCompletePage finishOrder() {
        click(FINISH_BUTTON);
        return new CheckoutCompletePage(driver).waitUntilLoaded();
    }

    public InventoryPage cancel() {
        click(CANCEL_BUTTON);
        return new InventoryPage(driver).waitUntilLoaded();
    }

    public boolean isFinishButtonDisplayed() {
        return isDisplayedImmediately(FINISH_BUTTON);
    }
}
