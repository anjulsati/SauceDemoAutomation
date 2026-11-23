package com.saucedemo.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.CheckoutCompletePage;
import com.saucedemo.pages.CheckoutStepOnePage;
import com.saucedemo.pages.CheckoutStepTwoPage;
import com.saucedemo.pages.InventoryPage;

import static com.saucedemo.tests.TestData.BACKPACK;
import static com.saucedemo.tests.TestData.BIKE_LIGHT;

/**
 * Checkout coverage: mandatory field validation, order overview, price and
 * tax calculation, order completion, cancellation and multi-item orders.
 */
public class CheckoutTests extends BaseTest {

    private static final String FIRST_NAME = "Test";
    private static final String LAST_NAME = "User";
    private static final String POSTAL_CODE = "110001";

    // ------------------------------------------------------------------
    // Validation
    // ------------------------------------------------------------------

    @Test(groups = {"checkout", "validation"},
            description = "Checkout blocks an order when first name is missing")
    public void checkoutWithoutFirstNameShowsError() {

        CheckoutStepOnePage step = startCheckout(BACKPACK);

        step.clickContinue().waitForError();

        Assert.assertTrue(step.isErrorDisplayed());
        Assert.assertTrue(
                step.getErrorMessage().contains("First Name is required"),
                "Unexpected error: " + step.getErrorMessage());
    }

    @Test(groups = {"checkout", "validation"},
            description = "Checkout blocks an order when last name is missing")
    public void checkoutWithoutLastNameShowsError() {

        CheckoutStepOnePage step = startCheckout(BACKPACK)
                .fillCustomerInformation(FIRST_NAME, "", POSTAL_CODE);

        step.clickContinue().waitForError();

        Assert.assertTrue(
                step.getErrorMessage().contains("Last Name is required"),
                "Unexpected error: " + step.getErrorMessage());
    }

    @Test(groups = {"checkout", "validation"},
            description = "Checkout blocks an order when postal code is missing")
    public void checkoutWithoutPostalCodeShowsError() {

        CheckoutStepOnePage step = startCheckout(BACKPACK)
                .fillCustomerInformation(FIRST_NAME, LAST_NAME, "");

        step.clickContinue().waitForError();

        Assert.assertTrue(
                step.getErrorMessage().contains("Postal Code is required"),
                "Unexpected error: " + step.getErrorMessage());
    }

    // ------------------------------------------------------------------
    // Order overview
    // ------------------------------------------------------------------

    @Test(groups = {"checkout", "regression"},
            description = "Valid customer information opens the order overview")
    public void validCustomerInformationOpensOverview() {

        CheckoutStepTwoPage overview = reachOverview(BACKPACK);

        Assert.assertTrue(overview.isLoaded());
        Assert.assertEquals(overview.getHeading(),
                "Checkout: Overview");
        Assert.assertTrue(overview.isFinishButtonDisplayed());
    }

    @Test(groups = {"checkout", "regression"},
            description = "Order overview lists the products being purchased")
    public void overviewListsPurchasedProducts() {

        CheckoutStepTwoPage overview = reachOverview(BACKPACK);

        Assert.assertTrue(
                overview.getItemNames().contains(BACKPACK),
                "Overview should list the purchased product");
    }

    @Test(groups = {"checkout", "regression"},
            description = "Overview item total equals the catalogue price")
    public void overviewShowsCorrectItemTotal() {

        CheckoutStepTwoPage overview = reachOverview(BACKPACK);

        Assert.assertEquals(overview.getItemTotal(),
                TestData.BACKPACK_PRICE, 0.01);
    }

    @Test(groups = {"checkout", "regression"},
            description = "Order total equals item total plus tax")
    public void overviewCalculatesTaxAndTotal() {

        CheckoutStepTwoPage overview = reachOverview(BACKPACK);

        double itemTotal = overview.getItemTotal();
        double tax = overview.getTax();
        double total = overview.getTotal();

        Assert.assertTrue(tax > 0, "Tax should be greater than zero");
        Assert.assertEquals(total, itemTotal + tax, 0.01,
                "Grand total should equal item total + tax");
    }

    @Test(groups = {"checkout", "regression"},
            description = "Overview shows payment and shipping information")
    public void overviewShowsPaymentAndShippingInformation() {

        String information = reachOverview(BACKPACK)
                .getPaymentAndShippingInformation();

        Assert.assertTrue(
                information.contains("SauceCard"),
                "Payment information missing: " + information);
        Assert.assertTrue(
                information.contains("Free Pony Express Delivery"),
                "Shipping information missing: " + information);
    }

    @Test(groups = {"checkout", "regression"},
            description = "Multi-item order total equals the sum of both prices")
    public void multiItemOrderTotalsAreCorrect() {

        CheckoutStepTwoPage overview = reachOverview(
                BACKPACK, BIKE_LIGHT);

        double expectedItems =
                TestData.BACKPACK_PRICE + TestData.BIKE_LIGHT_PRICE;

        Assert.assertEquals(overview.getItemNames().size(), 2);
        Assert.assertEquals(overview.getItemTotal(),
                expectedItems, 0.01);
        Assert.assertEquals(overview.getTotal(),
                overview.getItemTotal() + overview.getTax(), 0.01);
    }

    // ------------------------------------------------------------------
    // Completion and cancellation
    // ------------------------------------------------------------------

    @Test(groups = {"smoke", "checkout"},
            description = "Finishing the order shows the confirmation screen")
    public void completingOrderShowsConfirmation() {

        CheckoutCompletePage complete = reachOverview(BACKPACK)
                .finishOrder();

        Assert.assertTrue(complete.isLoaded());
        Assert.assertEquals(complete.getConfirmationHeader(),
                "Thank you for your order!");
        Assert.assertTrue(
                complete.getConfirmationMessage()
                        .contains("Your order has been dispatched"),
                "Confirmation message is missing");
    }

    @Test(groups = {"checkout", "regression"},
            description = "Back Home after an order returns to an empty inventory cart")
    public void backHomeAfterOrderEmptiesCart() {

        InventoryPage inventory = reachOverview(BACKPACK)
                .finishOrder()
                .backToHome();

        Assert.assertTrue(inventory.isLoaded());
        Assert.assertEquals(inventory.getCartBadgeCount(), 0);
        Assert.assertTrue(inventory.openCart().isEmpty(),
                "Cart should be empty after a completed order");
    }

    @Test(groups = {"checkout", "regression"},
            description = "Cancelling the overview returns to inventory and keeps the cart")
    public void cancelOnOverviewReturnsToInventory() {

        InventoryPage inventory = reachOverview(BACKPACK)
                .cancel();

        Assert.assertTrue(inventory.isLoaded());
        Assert.assertEquals(inventory.getCartBadgeCount(), 1);
        Assert.assertEquals(
                inventory.openCart().getItemNames(),
                java.util.List.of(BACKPACK));
    }

    @Test(groups = {"checkout", "regression"},
            description = "Cancelling customer information returns to the cart")
    public void cancelOnCustomerInformationReturnsToCart() {

        CartPage cart = startCheckout(BACKPACK).cancel();

        Assert.assertTrue(cart.isLoaded());
        Assert.assertEquals(cart.getItemCount(), 1);
        Assert.assertEquals(cart.getItemNames(),
                java.util.List.of(BACKPACK));
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private CheckoutStepOnePage startCheckout(String... products) {

        InventoryPage inventory = loginAsStandardUser();

        for (String product : products) {
            inventory.addProduct(product);
        }

        inventory.waitForCartBadgeCount(products.length);

        return inventory.openCart().checkout();
    }

    private CheckoutStepTwoPage reachOverview(String... products) {

        return startCheckout(products)
                .fillCustomerInformation(
                        FIRST_NAME, LAST_NAME, POSTAL_CODE)
                .continueToOverview();
    }
}
