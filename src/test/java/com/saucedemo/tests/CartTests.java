package com.saucedemo.tests;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.InventoryPage;

import static com.saucedemo.tests.TestData.BACKPACK;
import static com.saucedemo.tests.TestData.BIKE_LIGHT;
import static com.saucedemo.tests.TestData.ONESIE;

/**
 * Shopping cart coverage: empty state, item details, badge consistency,
 * removal, navigation and checkout entry.
 */
public class CartTests extends BaseTest {

    @Test(groups = {"cart", "regression"},
            description = "A new session starts with an empty cart")
    public void emptyCartShowsNoItems() {

        CartPage cart = loginAsStandardUser().openCart();

        Assert.assertTrue(cart.isLoaded());
        Assert.assertTrue(cart.isEmpty(),
                "Cart should be empty before any product is added");
        Assert.assertEquals(cart.getCartBadgeCount(), 0);
    }

    @Test(groups = {"smoke", "cart"},
            description = "Cart shows the product name, price and quantity")
    public void cartDisplaysAddedProductDetails() {

        CartPage cart = loginAsStandardUser()
                .addProduct(BACKPACK)
                .waitForCartBadgeCount(1)
                .openCart();

        Assert.assertEquals(cart.getItemNames(), List.of(BACKPACK));
        Assert.assertEquals(cart.getItemPrices().get(0),
                TestData.BACKPACK_PRICE, 0.001);
        Assert.assertEquals(cart.getItemQuantities(), List.of("1"));
    }

    @Test(groups = {"cart", "regression"},
            description = "Cart badge always matches the number of cart items")
    public void cartBadgeMatchesCartItemCount() {

        InventoryPage inventory = loginAsStandardUser()
                .addProduct(BACKPACK)
                .addProduct(BIKE_LIGHT)
                .addProduct(ONESIE)
                .waitForCartBadgeCount(3);

        CartPage cart = inventory.openCart();

        Assert.assertEquals(cart.getItemCount(), 3);
        Assert.assertEquals(cart.getCartBadgeCount(), 3);
        Assert.assertTrue(
                cart.getItemNames().containsAll(
                        List.of(BACKPACK, BIKE_LIGHT, ONESIE)));
    }

    @Test(groups = {"cart", "regression"},
            description = "Removing the only cart item empties the cart")
    public void removeProductFromCartEmptiesIt() {

        CartPage cart = loginAsStandardUser()
                .addProduct(BACKPACK)
                .waitForCartBadgeCount(1)
                .openCart()
                .removeProduct(BACKPACK)
                .waitForItemCount(0);

        Assert.assertTrue(cart.isEmpty());
        Assert.assertEquals(cart.getCartBadgeCount(), 0);
        Assert.assertFalse(cart.isCartBadgeDisplayed());
    }

    @Test(groups = {"cart", "regression"},
            description = "Removing one of two products keeps the other in the cart")
    public void removeOneOfTwoProducts() {

        CartPage cart = loginAsStandardUser()
                .addProduct(BACKPACK)
                .addProduct(BIKE_LIGHT)
                .waitForCartBadgeCount(2)
                .openCart()
                .removeProduct(BACKPACK)
                .waitForItemCount(1);

        Assert.assertEquals(cart.getItemNames(), List.of(BIKE_LIGHT));
        Assert.assertEquals(cart.getCartBadgeCount(), 1);
    }

    @Test(groups = {"cart", "regression"},
            description = "All products can be removed from the cart")
    public void removeAllProductsFromCart() {

        CartPage cart = loginAsStandardUser()
                .addProduct(BACKPACK)
                .addProduct(BIKE_LIGHT)
                .addProduct(ONESIE)
                .waitForCartBadgeCount(3)
                .openCart()
                .removeAllItems();

        Assert.assertTrue(cart.isEmpty());
        Assert.assertEquals(cart.getCartBadgeCount(), 0);
    }

    @Test(groups = {"cart", "navigation"},
            description = "Continue Shopping returns to the inventory and keeps the cart")
    public void continueShoppingReturnsToInventory() {

        InventoryPage inventory = loginAsStandardUser()
                .addProduct(BACKPACK)
                .waitForCartBadgeCount(1)
                .openCart()
                .continueShopping();

        Assert.assertTrue(inventory.isLoaded());
        Assert.assertEquals(inventory.getCartBadgeCount(), 1);
    }

    @Test(groups = {"cart", "checkout"},
            description = "Checkout from the cart opens the customer information step")
    public void checkoutFromCartOpensCheckoutStepOne() {

        var checkout = loginAsStandardUser()
                .addProduct(BACKPACK)
                .waitForCartBadgeCount(1)
                .openCart()
                .checkout();

        Assert.assertTrue(checkout.isLoaded());
        Assert.assertEquals(checkout.getHeading(),
                "Checkout: Your Information");
        Assert.assertTrue(checkout.isContinueButtonDisplayed());
        Assert.assertTrue(checkout.isCancelButtonDisplayed());
    }

    @Test(groups = {"cart", "regression"},
            description = "Cart page shows its heading and available actions")
    public void cartPageDisplaysActions() {

        CartPage cart = loginAsStandardUser().openCart();

        Assert.assertEquals(cart.getHeading(), "Your Cart");
        Assert.assertTrue(cart.isCheckoutButtonDisplayed());
        Assert.assertTrue(cart.isContinueShoppingButtonDisplayed());
    }

    @Test(groups = {"cart", "navigation"},
            description = "Cart contents survive navigation through product details")
    public void cartPersistsAcrossNavigation() {

        CartPage cart = loginAsStandardUser()
                .addProduct(BACKPACK)
                .waitForCartBadgeCount(1)
                .openCart()
                .continueShopping()
                .openProduct(BACKPACK)
                .backToProducts()
                .openCart();

        Assert.assertEquals(cart.getItemNames(), List.of(BACKPACK));
        Assert.assertEquals(cart.getCartBadgeCount(), 1);
    }
}
