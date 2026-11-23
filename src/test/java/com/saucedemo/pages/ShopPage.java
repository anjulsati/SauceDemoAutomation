package com.saucedemo.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Page object for the screens shown after login (inventory, cart, product
 * details). These screens share the header: burger menu, page title, cart
 * link and cart badge.
 */
public abstract class ShopPage extends BasePage {

    protected static final By PAGE_TITLE = By.className("title");
    protected static final By CART_LINK = By.className("shopping_cart_link");
    protected static final By CART_BADGE = By.className("shopping_cart_badge");

    private static final By MENU_BUTTON = By.id("react-burger-menu-btn");
    private static final By MENU_WRAPPER = By.className("bm-menu-wrap");
    private static final By CLOSE_MENU_BUTTON = By.id("react-burger-cross-btn");
    private static final By ALL_ITEMS_LINK = By.id("inventory_sidebar_link");
    private static final By ABOUT_LINK = By.id("about_sidebar_link");
    private static final By LOGOUT_LINK = By.id("logout_sidebar_link");
    private static final By RESET_LINK = By.id("reset_sidebar_link");
    private static final By MENU_ITEMS = By.cssSelector(".bm-item-list a");
    private static final By FOOTER_LINKS = By.cssSelector(".social a");

    protected ShopPage(WebDriver driver) {
        super(driver);
    }

    public String getHeading() {
        return textOf(PAGE_TITLE);
    }

    // ------------------------------------------------------------------
    // Cart
    // ------------------------------------------------------------------

    public int getCartBadgeCount() {

        List<WebElement> badges = driver.findElements(CART_BADGE);

        return badges.isEmpty()
                ? 0
                : Integer.parseInt(badges.get(0).getText().trim());
    }

    public boolean isCartBadgeDisplayed() {
        return isDisplayedImmediately(CART_BADGE);
    }

    public ShopPage waitForCartBadgeCount(int expected) {

        wait.until(d -> {
            List<WebElement> badges = d.findElements(CART_BADGE);

            if (badges.isEmpty()) {
                return expected == 0;
            }

            try {
                return Integer.parseInt(
                        badges.get(0).getText().trim()) == expected;
            } catch (NumberFormatException e) {
                return false;
            }
        });

        return this;
    }

    public CartPage openCart() {
        click(CART_LINK);
        return new CartPage(driver).waitUntilLoaded();
    }

    // ------------------------------------------------------------------
    // Burger menu
    // ------------------------------------------------------------------

    public boolean isMenuOpen() {
        List<WebElement> wrappers = driver.findElements(MENU_WRAPPER);
        return !wrappers.isEmpty()
                && "false".equals(
                        wrappers.get(0).getAttribute("aria-hidden"));
    }

    public ShopPage openMenu() {

        if (!isMenuOpen()) {
            click(MENU_BUTTON);
            wait.until(d -> !d.findElements(MENU_WRAPPER).isEmpty()
                    && "false".equals(d.findElements(MENU_WRAPPER)
                            .get(0)
                            .getAttribute("aria-hidden")));
        }

        return this;
    }

    public ShopPage closeMenu() {

        if (isMenuOpen()) {
            jsClick(CLOSE_MENU_BUTTON);
            wait.until(d -> !d.findElements(MENU_WRAPPER).isEmpty()
                    && "true".equals(d.findElements(MENU_WRAPPER)
                            .get(0)
                            .getAttribute("aria-hidden")));
        }

        return this;
    }

    public List<String> getMenuItems() {

        if (!isMenuOpen()) {
            return List.of();
        }

        // The React burger menu animates its items into view. Wait until the
        // core items are actually rendered before reading their text.
        wait.until(d -> d.findElements(MENU_ITEMS)
                .stream()
                .filter(WebElement::isDisplayed)
                .count() >= 4);

        return driver.findElements(MENU_ITEMS)
                .stream()
                .filter(WebElement::isDisplayed)
                .map(element -> element.getText().trim())
                .filter(text -> !text.isEmpty())
                .toList();
    }

    public boolean isMenuItemDisplayed(By locator) {
        return isDisplayedImmediately(locator);
    }

    public boolean isAllItemsLinkDisplayed() {
        return isMenuItemDisplayed(ALL_ITEMS_LINK);
    }

    public boolean isAboutLinkDisplayed() {
        return isMenuItemDisplayed(ABOUT_LINK);
    }

    public boolean isLogoutLinkDisplayed() {
        return isMenuItemDisplayed(LOGOUT_LINK);
    }

    public boolean isResetAppStateLinkDisplayed() {
        return isMenuItemDisplayed(RESET_LINK);
    }

    public LoginPage logout() {
        openMenu();
        click(LOGOUT_LINK);
        return new LoginPage(driver).waitUntilLoaded();
    }

    public ShopPage resetAppState() {
        openMenu();
        click(RESET_LINK);
        closeMenu();
        waitForCartBadgeCount(0);
        return this;
    }

    public InventoryPage goToAllItems() {
        openMenu();
        click(ALL_ITEMS_LINK);
        return new InventoryPage(driver).waitUntilLoaded();
    }

    // ------------------------------------------------------------------
    // Footer
    // ------------------------------------------------------------------

    public List<String> getFooterSocialLinks() {
        return driver.findElements(FOOTER_LINKS)
                .stream()
                .map(link -> link.getAttribute("href"))
                .filter(href -> href != null && !href.isBlank())
                .toList();
    }
}
