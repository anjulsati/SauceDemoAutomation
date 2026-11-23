package com.saucedemo.tests;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.InventoryPage;

import static com.saucedemo.tests.TestData.BACKPACK;
import static com.saucedemo.tests.TestData.BIKE_LIGHT;

/**
 * Navigation and session-state coverage: burger menu, All Items,
 * Reset App State and footer links.
 */
public class NavigationTests extends BaseTest {

    @Test(groups = {"navigation", "regression"},
            description = "Burger menu exposes every navigation option")
    public void burgerMenuShowsAllOptions() {

        InventoryPage inventory = loginAsStandardUser();

        inventory.openMenu();

        Assert.assertTrue(inventory.isMenuOpen(),
                "Burger menu should be open");
        Assert.assertTrue(
                inventory.getMenuItems().containsAll(List.of(
                        "All Items", "About", "Logout", "Reset App State")),
                "Unexpected menu items: " + inventory.getMenuItems());
        Assert.assertTrue(inventory.isAllItemsLinkDisplayed());
        Assert.assertTrue(inventory.isAboutLinkDisplayed());
        Assert.assertTrue(inventory.isLogoutLinkDisplayed());
        Assert.assertTrue(inventory.isResetAppStateLinkDisplayed());
    }

    @Test(groups = {"navigation", "regression"},
            description = "The burger menu can be closed again")
    public void burgerMenuCanBeClosed() {

        InventoryPage inventory = loginAsStandardUser();

        inventory.openMenu();
        Assert.assertTrue(inventory.isMenuOpen());

        inventory.closeMenu();

        Assert.assertFalse(inventory.isMenuOpen(),
                "Burger menu should be closed");
        Assert.assertTrue(inventory.getMenuItems().isEmpty());
    }

    @Test(groups = {"navigation", "regression"},
            description = "All Items always returns to the inventory page")
    public void allItemsLinkReturnsToInventory() {

        InventoryPage inventory = loginAsStandardUser()
                .addProduct(BACKPACK)
                .waitForCartBadgeCount(1);

        CartPage cart = inventory.openCart();
        Assert.assertTrue(cart.isLoaded());

        InventoryPage returned = cart.goToAllItems();

        Assert.assertTrue(returned.isLoaded());
        Assert.assertEquals(returned.getCartBadgeCount(), 1,
                "Cart contents must survive navigation");
    }

    @Test(groups = {"navigation", "cart", "regression"},
            description = "Reset App State clears the cart")
    public void resetAppStateClearsCart() {

        InventoryPage inventory = loginAsStandardUser()
                .addProduct(BACKPACK)
                .addProduct(BIKE_LIGHT)
                .waitForCartBadgeCount(2);

        inventory.resetAppState();

        Assert.assertEquals(inventory.getCartBadgeCount(), 0);
        Assert.assertFalse(inventory.isCartBadgeDisplayed());
        Assert.assertTrue(inventory.openCart().isEmpty(),
                "Cart should be empty after Reset App State");
    }

    @Test(groups = {"navigation", "regression"},
            description = "Footer exposes the three social media links")
    public void footerShowsSocialMediaLinks() {

        InventoryPage inventory = loginAsStandardUser();

        List<String> links = inventory.getFooterSocialLinks();

        Assert.assertEquals(links.size(), 3,
                "Expected Twitter, Facebook and LinkedIn links");

        for (String link : links) {
            Assert.assertTrue(link.startsWith("http"),
                    "Invalid social link: " + link);
        }
    }

    @Test(groups = {"navigation", "regression"},
            description = "Every page keeps the shared Swag Labs document title")
    public void pagesShareApplicationTitle() {

        InventoryPage inventory = loginAsStandardUser();

        Assert.assertEquals(inventory.getPageTitle(), "Swag Labs");

        CartPage cart = inventory.openCart();

        Assert.assertEquals(cart.getPageTitle(), "Swag Labs");
    }
}
