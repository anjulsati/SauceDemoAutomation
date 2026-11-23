# Test Case Catalogue — SauceDemo Automation

**61 test methods → 64 test executions** (the login data provider adds three
extra executions). Every test is automated and executed by `mvn test`.

| Module | Methods | Executions |
|---|---:|---:|
| Authentication | 15 | 18 |
| Inventory | 17 | 17 |
| Cart | 10 | 10 |
| Checkout | 13 | 13 |
| Navigation | 6 | 6 |
| **Total** | **61** | **64** |

Priority: **P1** = critical path / smoke, **P2** = high-value regression,
**P3** = supporting validation.

---

## 1. Authentication — `LoginTests`

| ID | Test case | Method | Groups | Priority | Expected result |
|---|---|---|---|---|---|
| L01 | Valid login opens the inventory | `validLoginNavigatesToInventory` | smoke, login | P1 | URL contains `inventory.html`, heading is `Products`, 6 products shown |
| L02 | Wrong password is rejected | `invalidPasswordShowsError` | login, regression | P1 | Error says username/password do not match; inventory not reached |
| L03 | Unknown username is rejected | `unknownUsernameShowsError` | login, regression | P2 | Same credential error is shown |
| L04 | Locked-out user is rejected | `lockedOutUserShowsError` | login, regression | P1 | Error contains `locked out`; inventory not reached |
| L05 | Empty username is rejected | `emptyUsernameShowsValidationError` | login, validation | P2 | Error contains `Username is required` |
| L06 | Empty password is rejected | `emptyPasswordShowsValidationError` | login, validation | P2 | Error contains `Password is required` |
| L07 | Empty form is rejected | `emptyFormShowsValidationError` | login, validation | P2 | Error reports the first missing field (username) |
| L08 | Error banner can be dismissed | `errorMessageCanBeDismissed` | login, regression | P3 | Banner disappears after clicking the close icon |
| L09 | Invalid credential combinations are rejected | `invalidCredentialsAreRejected` (data provider) | login, security | P2 | **4 executions**: wrong password, unknown user, empty password, empty username — each shows an error and never reaches inventory |
| L10 | Injection-style input is rejected | `sqlInjectionInputIsRejected` | login, security | P3 | Input treated as plain text; login denied |
| L11 | Password field masks input | `passwordFieldMasksInput` | login, validation | P3 | Password input type is `password`; username type is `text` |
| L12 | Login screen branding and controls | `loginPageDisplaysBranding` | smoke, login | P2 | Logo and title are `Swag Labs`; login button is visible with value `Login` |
| L13 | Protected page requires login | `protectedPageRequiresAuthentication` | login, security | P1 | Direct visit to `inventory.html` shows the login guard; no products visible |
| L14 | Logout returns to login | `logoutReturnsToLogin` | smoke, login | P1 | Login form visible; URL no longer contains `inventory.html` |
| L15 | Session is invalid after logout | `sessionIsInvalidAfterLogout` | login, security | P1 | Reopening `inventory.html` shows the login guard again |

---

## 2. Inventory — `InventoryTests`

| ID | Test case | Method | Groups | Priority | Expected result |
|---|---|---|---|---|---|
| I01 | Six products are displayed | `inventoryDisplaysSixProducts` | smoke, inventory | P1 | Exactly 6 product cards; heading is `Products` |
| I02 | Product names match the catalogue | `inventoryShowsExpectedProductNames` | inventory, regression | P2 | All six names match in default A-Z order |
| I03 | Product cards are complete | `everyProductCardShowsRequiredDetails` | inventory, regression | P2 | Each card has a non-empty name and description, a `$0.00` price, an image and a button |
| I04 | Default sort is A-Z | `defaultSortIsNameAToZ` | inventory, regression | P2 | Product names are alphabetically ascending |
| I05 | Sort price low to high | `sortPriceLowToHigh` | inventory, regression | P2 | Full price list ascending; first `$7.99`, last `$49.99` |
| I06 | Sort price high to low | `sortPriceHighToLow` | inventory, regression | P2 | Full price list descending; first `$49.99`, last `$7.99` |
| I07 | Sort name A-Z | `sortNameAToZ` | inventory, regression | P2 | Full name list ascending; first is `Sauce Labs Backpack` |
| I08 | Sort name Z-A | `sortNameZToA` | inventory, regression | P2 | Full name list descending; first is `Test.allTheThings() T-Shirt (Red)` |
| I09 | Add one product updates badge | `addSingleProductUpdatesCartBadge` | smoke, inventory, cart | P1 | Badge shows `1`; card shows `Remove` |
| I10 | Add three products updates badge | `addMultipleProductsUpdatesCartBadge` | inventory, cart | P2 | Badge shows `3` |
| I11 | Add all six products | `addAllProductsUpdatesCartBadgeToSix` | inventory, cart | P2 | Badge shows `6` |
| I12 | Remove the only product | `removeProductHidesCartBadge` | inventory, cart | P2 | Badge disappears; card returns to `Add to cart` |
| I13 | Remove all products | `removeAllProductsHidesCartBadge` | inventory, cart | P2 | Badge count returns to 0 |
| I14 | Add/Remove buttons toggle | `addToCartButtonTogglesToRemoveButton` | inventory, cart | P2 | `Remove` replaces `Add to cart`, then reverts after removal |
| I15 | Product details open correctly | `productDetailsOpenCorrectly` | inventory, navigation | P2 | Name, price and description are correct; Back to Products returns to inventory |
| I16 | Add to cart from details page | `productCanBeAddedFromDetailsPage` | inventory, cart | P2 | Details button changes to `Remove`; badge shows `1` |
| I17 | Cart survives details navigation | `cartSurvivesProductDetailsNavigation` | inventory, cart, navigation | P2 | Badge stays at `1`; cart still lists the product |

---

## 3. Cart — `CartTests`

| ID | Test case | Method | Groups | Priority | Expected result |
|---|---|---|---|---|---|
| C01 | Empty cart has no items | `emptyCartShowsNoItems` | cart, regression | P2 | Cart loads with 0 items and no badge |
| C02 | Cart shows product details | `cartDisplaysAddedProductDetails` | smoke, cart | P1 | Name is Backpack, price is `$29.99`, quantity is `1` |
| C03 | Badge matches cart item count | `cartBadgeMatchesCartItemCount` | cart, regression | P2 | 3 items in cart and badge shows `3` |
| C04 | Remove the only item | `removeProductFromCartEmptiesIt` | cart, regression | P2 | Cart becomes empty and badge disappears |
| C05 | Remove one of two items | `removeOneOfTwoProducts` | cart, regression | P2 | Remaining item is Bike Light; badge shows `1` |
| C06 | Remove all items | `removeAllProductsFromCart` | cart, regression | P2 | Cart empty; badge count 0 |
| C07 | Continue shopping | `continueShoppingReturnsToInventory` | cart, navigation | P2 | Inventory loads and badge remains `1` |
| C08 | Checkout from cart | `checkoutFromCartOpensCheckoutStepOne` | cart, checkout | P1 | Customer information page loads with Continue and Cancel buttons |
| C09 | Cart page actions | `cartPageDisplaysActions` | cart, regression | P3 | Heading is `Your Cart`; Checkout and Continue Shopping are visible |
| C10 | Cart persists across navigation | `cartPersistsAcrossNavigation` | cart, navigation | P2 | Cart still contains Backpack after inventory → details → cart |

---

## 4. Checkout — `CheckoutTests`

| ID | Test case | Method | Groups | Priority | Expected result |
|---|---|---|---|---|---|
| K01 | First name is required | `checkoutWithoutFirstNameShowsError` | checkout, validation | P1 | Error contains `First Name is required`; overview not reached |
| K02 | Last name is required | `checkoutWithoutLastNameShowsError` | checkout, validation | P2 | Error contains `Last Name is required` |
| K03 | Postal code is required | `checkoutWithoutPostalCodeShowsError` | checkout, validation | P2 | Error contains `Postal Code is required` |
| K04 | Valid information opens overview | `validCustomerInformationOpensOverview` | checkout, regression | P1 | Heading is `Checkout: Overview`; Finish button visible |
| K05 | Overview lists purchased products | `overviewListsPurchasedProducts` | checkout, regression | P2 | Backpack appears in the order summary |
| K06 | Item total matches price | `overviewShowsCorrectItemTotal` | checkout, regression | P2 | Item total equals `$29.99` |
| K07 | Tax and grand total are correct | `overviewCalculatesTaxAndTotal` | checkout, regression | P1 | Tax > 0 and total = item total + tax (0.01 tolerance) |
| K08 | Payment and shipping information | `overviewShowsPaymentAndShippingInformation` | checkout, regression | P3 | Summary shows `SauceCard` and `Free Pony Express Delivery` |
| K09 | Multi-item totals | `multiItemOrderTotalsAreCorrect` | checkout, regression | P2 | Two items; item total `$39.98`; total = item total + tax |
| K10 | Complete the order | `completingOrderShowsConfirmation` | smoke, checkout | P1 | Header is `Thank you for your order!`; message mentions dispatch |
| K11 | Back Home after order | `backHomeAfterOrderEmptiesCart` | checkout, regression | P1 | Inventory loads; badge is 0; cart is empty |
| K12 | Cancel on overview | `cancelOnOverviewReturnsToInventory` | checkout, regression | P2 | Inventory loads; cart still contains Backpack |
| K13 | Cancel customer information | `cancelOnCustomerInformationReturnsToCart` | checkout, regression | P2 | Cart loads with 1 item (Backpack) |

---

## 5. Navigation — `NavigationTests`

| ID | Test case | Method | Groups | Priority | Expected result |
|---|---|---|---|---|---|
| N01 | Burger menu shows options | `burgerMenuShowsAllOptions` | navigation, regression | P2 | Menu opens and contains All Items, About, Logout, Reset App State |
| N02 | Burger menu can be closed | `burgerMenuCanBeClosed` | navigation, regression | P3 | Menu closes; no menu items remain visible |
| N03 | All Items returns to inventory | `allItemsLinkReturnsToInventory` | navigation, regression | P2 | Inventory loads; cart badge remains `1` |
| N04 | Reset App State clears cart | `resetAppStateClearsCart` | navigation, cart, regression | P2 | Badge is 0 and the cart is empty |
| N05 | Footer social links | `footerShowsSocialMediaLinks` | navigation, regression | P3 | Exactly 3 valid social URLs are present |
| N06 | Consistent document title | `pagesShareApplicationTitle` | navigation, regression | P3 | Inventory and cart pages both use title `Swag Labs` |

---

## Traceability

| Requirement | Test cases |
|---|---|
| User can log in with valid credentials | L01, L12, L14 |
| Invalid or locked users cannot log in | L02–L10 |
| Protected pages require a session | L13, L15 |
| Products and prices are displayed correctly | I01–I03 |
| Products can be sorted in all supported orders | I04–I08 |
| Cart badge and contents stay consistent | I09–I17, C01–C10 |
| Checkout enforces customer information | K01–K03 |
| Order totals are calculated correctly | K06, K07, K09 |
| Order can be completed or cancelled | K10–K13 |
| Navigation and session reset work | N01–N04 |
