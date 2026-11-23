# SauceDemo Automation Framework

End-to-end UI test automation for the [SauceDemo](https://www.saucedemo.com/) e-commerce
demo, built with **Java 25**, **Selenium WebDriver 4**, **TestNG** and **Maven**.

The framework follows the **Page Object Model (POM)** and generates a rich
**ExtentReports** dashboard, Surefire HTML reports, and automatic failure screenshots.

---

## Highlights

- **61 automated test methods / 64 test executions** across five feature areas
- **Page Object Model** with reusable, readable page classes
- **Data-driven login tests** using TestNG `@DataProvider`
- **Explicit waits only** — no fragile `Thread.sleep` calls
- **Fresh browser per test** for complete test isolation
- **Automatic screenshots** attached to every failed test
- **ExtentReports dashboard** with categories, timelines and system information
- Configurable **browser**, **headless mode**, **timeout** and **credentials**
- **Smoke** and **full regression** suites, plus functional and security groups
- Works from **Maven** or directly from **IntelliJ IDEA**

---

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 25 | Language / runtime |
| Selenium WebDriver | 4.52.0 | Browser automation |
| TestNG | 7.10.2 | Test runner, assertions, groups, data providers |
| Maven | 3.8+ | Build and dependency management |
| ExtentReports | 5.1.2 | Rich HTML execution report |
| SLF4J Simple | 2.0.17 | Logging backend |
| Google Chrome | 157+ | Default browser (Firefox and Edge also supported) |

Selenium Manager resolves the matching browser driver automatically — no manual
driver download or `WebDriverManager` dependency is required.

---

## Test Coverage

| Module | Executions | What is covered |
|---|---:|---|
| **Authentication** | 18 | Valid / invalid / empty credentials, locked user, unknown user, SQL-injection input, error dismissal, password masking, login branding, protected-page guard, logout, session invalidation |
| **Inventory** | 17 | Six-product catalogue, product data completeness, all four sort orders (A-Z, Z-A, low-high, high-low), add/remove one, many and all products, badge counts, button toggling, product details, cart persistence |
| **Cart** | 10 | Empty state, item name/price/quantity, badge consistency, remove one/all, continue shopping, checkout entry, page actions, persistence across navigation |
| **Checkout** | 13 | First name / last name / postal code validation, order overview, item total, tax and grand-total calculation, payment & shipping information, multi-item totals, order completion, Back Home, cancel at both steps |
| **Navigation** | 6 | Burger menu contents, close menu, All Items, Reset App State, footer social links, consistent document title |
| **Total** | **64** | 61 methods; the login data provider adds 3 extra executions |

Detailed test cases: [`docs/TEST_CASES.md`](docs/TEST_CASES.md)  
Test plan: [`docs/TEST_PLAN.md`](docs/TEST_PLAN.md)

---

## Project Structure

```text
SauceDemoAutomation/
├── pom.xml
├── README.md
├── docs/
│   ├── TEST_PLAN.md
│   └── TEST_CASES.md
└── src/test/
    ├── java/com/saucedemo/
    │   ├── base/
    │   │   └── BaseTest.java              # Browser lifecycle + shared helpers
    │   ├── config/
    │   │   └── ConfigReader.java          # config.properties + -D overrides
    │   ├── pages/
    │   │   ├── BasePage.java              # Waits and common element helpers
    │   │   ├── ShopPage.java              # Shared header: menu, cart, footer
    │   │   ├── LoginPage.java
    │   │   ├── InventoryPage.java
    │   │   ├── ProductDetailsPage.java
    │   │   ├── CartPage.java
    │   │   ├── CheckoutStepOnePage.java
    │   │   ├── CheckoutStepTwoPage.java
    │   │   └── CheckoutCompletePage.java
    │   ├── tests/
    │   │   ├── TestData.java
    │   │   ├── LoginTests.java
    │   │   ├── InventoryTests.java
    │   │   ├── CartTests.java
    │   │   ├── CheckoutTests.java
    │   │   └── NavigationTests.java
    │   └── utils/
    │       ├── DriverFactory.java         # Chrome / Firefox / Edge factory
    │       ├── ScreenshotUtils.java       # Base64 + file screenshots
    │       └── TestListener.java          # ExtentReports + failure screenshots
    └── resources/
        ├── config.properties
        ├── testng.xml                     # Full regression suite
        ├── testng-smoke.xml               # Critical-path smoke suite
        ├── simplelogger.properties
        └── META-INF/services/
            └── org.testng.ITestNGListener # Auto-registers TestListener
```

### Design at a glance

```text
Test classes  ──►  Page objects  ──►  Selenium WebDriver  ──►  Chrome
      │                  │
      └── BaseTest       └── BasePage / ShopPage (waits + shared behaviour)
      └── TestData
TestListener ──► ExtentReports + screenshots
```

---

## Getting Started

### Prerequisites

- JDK 25 (the project is compiled with `--release 25`)
- Maven 3.8+
- Google Chrome (or Firefox / Edge)
- Internet access to reach `https://www.saucedemo.com/`

### Open the project

```bash
cd ~/Desktop/SauceDemoAutomation
mvn clean test-compile
```

In IntelliJ IDEA: **Open** the folder, let Maven import finish, then run
`src/test/resources/testng.xml` or any test class.

---

## Running the Tests

| Goal | Command |
|---|---|
| Full regression, headed | `mvn test` |
| Full regression, headless | `mvn test -Dheadless=true` |
| Clean run | `mvn clean test -Dheadless=true` |
| Smoke suite only | `mvn test -Dsurefire.suiteXmlFiles=src/test/resources/testng-smoke.xml` |
| Firefox instead of Chrome | `mvn test -Dbrowser=firefox` |
| Custom timeout (seconds) | `mvn test -Dtimeout.seconds=30` |
| Generate Surefire HTML report | `mvn surefire-report:report` |

Run one class from IntelliJ by right-clicking the class and selecting
**Run**. The ExtentReports listener is auto-registered, so the report is
produced for IDE runs too.

---

## Configuration

All settings live in `src/test/resources/config.properties` and can be
overridden with `-Dkey=value` on the command line.

| Key | Default | Description |
|---|---|---|
| `base.url` | `https://www.saucedemo.com/` | Application under test |
| `browser` | `chrome` | `chrome`, `firefox` or `edge` |
| `headless` | `false` | Run without a visible browser window |
| `timeout.seconds` | `15` | Explicit wait timeout |
| `standard.user` | `standard_user` | Valid SauceDemo username |
| `password` | `secret_sauce` | Valid SauceDemo password |

Example:

```bash
mvn test -Dheadless=true -Dbrowser=firefox -Dtimeout.seconds=30
```

---

## Reports and Artifacts

| Artifact | Location |
|---|---|
| ExtentReports dashboard | `target/extent-reports/SauceDemo-Report-<timestamp>.html` |
| Surefire HTML report | `target/surefire-reports/index.html` (or `mvn surefire-report:report`) |
| TestNG raw results | `target/surefire-reports/` |
| Failure screenshots | `target/screenshots/` |

The ExtentReports dashboard includes:

- pass / fail / skip summary and pass percentage
- categories for `login`, `inventory`, `cart`, `checkout`, `navigation`, `smoke`,
  `regression`, `validation` and `security`
- a timeline of the run
- environment and system information
- the full stack trace and an embedded screenshot for every failure

---

## Defect-Detection Demonstration

SauceDemo publishes intentionally faulty accounts. A separate six-test suite
asserts correct behaviour against those accounts and therefore fails on purpose,
demonstrating that the framework detects real defects.

```bash
mvn test -Dsurefire.suiteXmlFiles=src/test/resources/testng-defect-demo.xml
```

Expected: `Tests run: 6, Failures: 6`. This is **not** part of the main
regression suite and is not included in the 64/64 result.

Covered defects include broken product images, non-working sorting, a cart
badge that does not clear, an application alert on sorting, and a page-load
delay above the three-second budget. Full details and evidence:

- `docs/DEFECT_DEMO.md`
- `report/defect-demo/SauceDemo-Defect-Demo-Report.html`

---

## How a Test Is Written

Page objects hide locators and waits; tests read like user stories:

```java
@Test(groups = {"smoke", "checkout"},
      description = "Finishing the order shows the confirmation screen")
public void completingOrderShowsConfirmation() {

    CheckoutCompletePage complete = reachOverview(BACKPACK)
            .finishOrder();

    Assert.assertEquals(complete.getConfirmationHeader(),
            "Thank you for your order!");
}
```

A failed assertion produces an ExtentReports failure entry with an embedded
screenshot, and the screenshot is also written to `target/screenshots/`.

### Test design principles

1. **Isolation** — every test gets a fresh browser and a fresh session.
2. **No hard sleeps** — all synchronisation uses explicit `WebDriverWait`.
3. **Stable locators** — IDs and `data-test` attributes are preferred over
   brittle CSS chains; product buttons are located relative to their card.
4. **Meaningful assertions** — verify complete lists, totals and state, not
   only single elements.
5. **Readable tests** — page objects expose intent (`addProduct`,
   `continueToOverview`), not raw Selenium calls.
6. **Actionable failures** — message, stack trace and screenshot together.

---

## Latest Verified Execution

| Field | Value |
|---|---|
| Date | 10 November 2026 |
| Command | `mvn -Dheadless=true test` |
| Tests run | **64** |
| Passed | **64** |
| Failed | **0** |
| Skipped | **0** |
| Duration | ~1 min 26 s (86 s) |
| Browser | Chrome 157 (headless) |

---

## Troubleshooting

**`SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder"`**

TestNG 7.10.2 transitively depends on `slf4j-api` 1.7.36. Maven's
nearest-wins rule was selecting that old API next to the 2.x binding. The POM
now pins `slf4j-api` and `slf4j-simple` to the same 2.0.17 version, so the
message is gone. If it still appears in IntelliJ, reload the Maven project
(Maven tool window → **Reload All Maven Projects**).

**`Unable to find an exact match for CDP version 157 ... found: 156`**

This is an informational message, not a test failure. Chrome can be newer than
the CDP version bundled with the latest Selenium release. It is suppressed
only for `org.openqa.selenium.devtools.CdpVersionFinder` in `DriverFactory`;
all other warnings are still shown. It can be removed permanently by upgrading
Selenium once a release with matching CDP support is available.

**`Provides transitive vulnerable dependency maven:org.freemarker:freemarker:2.3.32`**

ExtentReports 5.1.2 still declares FreeMarker 2.3.32 transitively, which
dependency scanners flag (CVE-2026-84939). The POM pins the patched
`org.freemarker:freemarker:2.3.35` directly, so Maven resolves 2.3.35 instead.
Reload the Maven project in IntelliJ after pulling the change.

**ChromeDriver download messages on the first run**

Selenium Manager downloads the matching driver into `~/.cache/selenium`. This
happens once per browser version and does not affect test results.

---

## Notes and Limitations

- SauceDemo is a public demo site. Test data is intentionally non-destructive:
  no real orders or personal data are created.
- The burger menu also links to an external **Dynamic Catalog** page; that
  third-party destination is outside this suite's scope.
- Tests run sequentially. The framework is structured for future parallel
  execution (per-test browser isolation), but the suite is intentionally kept
  sequential for stable demonstration runs.
- `problem_user`, `performance_glitch_user`, `error_user` and `visual_user`
  are not used because their injected defects are outside the functional
  scope of this suite.
