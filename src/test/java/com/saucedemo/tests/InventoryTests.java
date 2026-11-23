package com.saucedemo.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.InventoryPage;

import static com.saucedemo.tests.TestData.BACKPACK;
import static com.saucedemo.tests.TestData.BIKE_LIGHT;
import static com.saucedemo.tests.TestData.FLEECE_JACKET_PRICE;
import static com.saucedemo.tests.TestData.ONESIE;
import static com.saucedemo.tests.TestData.RED_TSHIRT;

/**
 * Inventory coverage: product catalogue, sorting, add/remove behaviour and
 * product details navigation.
 */
public class InventoryTests extends BaseTest {

    @Test(groups = {"smoke", "inventory"},
            description = "Inventory lists all six catalogue products")
    public void inventoryDisplaysSixProducts() {

        InventoryPage inventory = loginAsStandardUser();

        Assert.assertEquals(inventory.getProductCount(), 6);
        Assert.assertEquals(inventory.getHeading(), "Products");
    }

    @Test(groups = {"inventory", "regression"},
            description = "Product names match the catalogue in default A-Z order")
    public void inventoryShowsExpectedProductNames() {

        List<String> names = loginAsStandardUser().getProductNames();

        Assert.assertEquals(names, List.of(
                "Sauce Labs Backpack",
                "Sauce Labs Bike Light",
                "Sauce Labs Bolt T-Shirt",
                "Sauce Labs Fleece Jacket",
                "Sauce Labs Onesie",
                "Test.allTheThings() T-Shirt (Red)"));
    }

    @Test(groups = {"inventory", "regression"},
            description = "Every product card shows name, description, price, image and button")
    public void everyProductCardShowsRequiredDetails() {

        InventoryPage inventory = loginAsStandardUser();
        inventory.waitForProductImages();

        List<WebElement> items = driver.findElements(
                By.className("inventory_item"));

        Assert.assertEquals(items.size(), 6);

        for (WebElement item : items) {

            String name = item.findElement(
                    By.className("inventory_item_name")).getText().trim();
            String description = item.findElement(
                    By.className("inventory_item_desc")).getText().trim();
            String price = item.findElement(
                    By.className("inventory_item_price")).getText().trim();

            Assert.assertFalse(name.isBlank(),
                    "Product name should not be empty");
            Assert.assertFalse(description.isBlank(),
                    "Description missing for " + name);
            Assert.assertTrue(price.matches("\\$\\d+\\.\\d{2}"),
                    "Invalid price format for " + name + ": " + price);
            Assert.assertTrue(
                    item.findElement(By.tagName("img")).isDisplayed(),
                    "Image missing for " + name);
            Assert.assertTrue(
                    item.findElement(By.tagName("button")).isDisplayed(),
                    "Add-to-cart button missing for " + name);
        }
    }

    @Test(groups = {"inventory", "regression"},
            description = "Default order is product name A-Z")
    public void defaultSortIsNameAToZ() {

        List<String> names = loginAsStandardUser().getProductNames();
        List<String> sorted = new ArrayList<>(names);
        Collections.sort(sorted);

        Assert.assertEquals(names, sorted,
                "Default product order should be alphabetical");
        Assert.assertEquals(names.get(0), BACKPACK);
    }

    @Test(groups = {"inventory", "regression"},
            description = "Sorting low to high orders every price ascending")
    public void sortPriceLowToHigh() {

        InventoryPage inventory = loginAsStandardUser()
                .sortBy("lohi");

        List<Double> prices = inventory.getProductPrices();
        List<Double> sorted = new ArrayList<>(prices);
        Collections.sort(sorted);

        Assert.assertEquals(prices, sorted,
                "Prices should be in ascending order");
        Assert.assertEquals(prices.get(0), 7.99, 0.001);
        Assert.assertEquals(prices.get(prices.size() - 1), 49.99, 0.001);
    }

    @Test(groups = {"inventory", "regression"},
            description = "Sorting high to low orders every price descending")
    public void sortPriceHighToLow() {

        InventoryPage inventory = loginAsStandardUser()
                .sortBy("hilo");

        List<Double> prices = inventory.getProductPrices();
        List<Double> sorted = new ArrayList<>(prices);
        sorted.sort(Collections.reverseOrder());

        Assert.assertEquals(prices, sorted,
                "Prices should be in descending order");
        Assert.assertEquals(prices.get(0), FLEECE_JACKET_PRICE, 0.001);
        Assert.assertEquals(prices.get(prices.size() - 1), 7.99, 0.001);
    }

    @Test(groups = {"inventory", "regression"},
            description = "Sorting by name A-Z orders the full product list")
    public void sortNameAToZ() {

        List<String> names = loginAsStandardUser()
                .sortBy("az")
                .getProductNames();

        List<String> sorted = new ArrayList<>(names);
        Collections.sort(sorted);

        Assert.assertEquals(names, sorted);
        Assert.assertEquals(names.get(0), BACKPACK);
    }

    @Test(groups = {"inventory", "regression"},
            description = "Sorting by name Z-A orders the full product list")
    public void sortNameZToA() {

        List<String> names = loginAsStandardUser()
                .sortBy("za")
                .getProductNames();

        List<String> sorted = new ArrayList<>(names);
        sorted.sort(Collections.reverseOrder());

        Assert.assertEquals(names, sorted);
        Assert.assertEquals(names.get(0), RED_TSHIRT);
    }

    @Test(groups = {"smoke", "inventory", "cart"},
            description = "Adding one product updates the cart badge to 1")
    public void addSingleProductUpdatesCartBadge() {

        InventoryPage inventory = loginAsStandardUser()
                .addProduct(BACKPACK)
                .waitForCartBadgeCount(1);

        Assert.assertEquals(inventory.getCartBadgeCount(), 1);
        Assert.assertTrue(inventory.isRemoveButtonDisplayed(BACKPACK));
    }

    @Test(groups = {"inventory", "cart"},
            description = "Adding three products updates the badge to 3")
    public void addMultipleProductsUpdatesCartBadge() {

        InventoryPage inventory = loginAsStandardUser()
                .addProduct(BACKPACK)
                .addProduct(BIKE_LIGHT)
                .addProduct(ONESIE)
                .waitForCartBadgeCount(3);

        Assert.assertEquals(inventory.getCartBadgeCount(), 3);
    }

    @Test(groups = {"inventory", "cart"},
            description = "All six products can be added to the cart")
    public void addAllProductsUpdatesCartBadgeToSix() {

        InventoryPage inventory = loginAsStandardUser()
                .addAllProducts();

        Assert.assertEquals(inventory.getCartBadgeCount(), 6);
    }

    @Test(groups = {"inventory", "cart"},
            description = "Removing the only product hides the cart badge")
    public void removeProductHidesCartBadge() {

        InventoryPage inventory = loginAsStandardUser()
                .addProduct(BACKPACK)
                .waitForCartBadgeCount(1)
                .removeProduct(BACKPACK)
                .waitForCartBadgeCount(0);

        Assert.assertFalse(inventory.isCartBadgeDisplayed(),
                "Cart badge should disappear when the cart is empty");
        Assert.assertTrue(inventory.isAddButtonDisplayed(BACKPACK));
    }

    @Test(groups = {"inventory", "cart"},
            description = "Removing all products empties the badge")
    public void removeAllProductsHidesCartBadge() {

        InventoryPage inventory = loginAsStandardUser()
                .addProduct(BACKPACK)
                .addProduct(BIKE_LIGHT)
                .waitForCartBadgeCount(2)
                .removeProduct(BACKPACK)
                .removeProduct(BIKE_LIGHT)
                .waitForCartBadgeCount(0);

        Assert.assertEquals(inventory.getCartBadgeCount(), 0);
        Assert.assertFalse(inventory.isCartBadgeDisplayed());
    }

    @Test(groups = {"inventory", "cart"},
            description = "Add and Remove buttons toggle for the same product")
    public void addToCartButtonTogglesToRemoveButton() {

        InventoryPage inventory = loginAsStandardUser()
                .addProduct(BACKPACK)
                .waitForCartBadgeCount(1);

        Assert.assertTrue(inventory.isRemoveButtonDisplayed(BACKPACK));
        Assert.assertFalse(inventory.isAddButtonDisplayed(BACKPACK));

        inventory.removeProduct(BACKPACK).waitForCartBadgeCount(0);

        Assert.assertTrue(inventory.isAddButtonDisplayed(BACKPACK));
        Assert.assertFalse(inventory.isRemoveButtonDisplayed(BACKPACK));
    }

    @Test(groups = {"inventory", "navigation"},
            description = "Product details open with the correct information")
    public void productDetailsOpenCorrectly() {

        var details = loginAsStandardUser().openProduct(BACKPACK);

        Assert.assertTrue(details.isLoaded());
        Assert.assertEquals(details.getProductName(), BACKPACK);
        Assert.assertEquals(details.getProductPrice(),
                TestData.BACKPACK_PRICE, 0.001);
        Assert.assertFalse(details.getProductDescription().isBlank(),
                "Product description should not be empty");

        InventoryPage inventory = details.backToProducts();

        Assert.assertTrue(inventory.isLoaded(),
                "Back to products should return to the inventory");
    }

    @Test(groups = {"inventory", "cart"},
            description = "A product can be added to the cart from its details page")
    public void productCanBeAddedFromDetailsPage() {

        var details = loginAsStandardUser().openProduct(BACKPACK);

        details.addToCart();

        Assert.assertTrue(details.isRemoveDisplayed());
        Assert.assertEquals(details.getCartBadgeCount(), 1);
    }

    @Test(groups = {"inventory", "cart", "navigation"},
            description = "Cart contents survive navigating through product details")
    public void cartSurvivesProductDetailsNavigation() {

        InventoryPage inventory = loginAsStandardUser()
                .addProduct(BACKPACK)
                .waitForCartBadgeCount(1);

        inventory.openProduct(BACKPACK).backToProducts();

        Assert.assertEquals(inventory.getCartBadgeCount(), 1);

        var cart = inventory.openCart();

        Assert.assertEquals(cart.getItemNames(), List.of(BACKPACK));
    }
}
