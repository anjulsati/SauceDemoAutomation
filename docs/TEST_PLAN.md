# Test Plan — SauceDemo UI Automation

| Field | Value |
|---|---|
| Project | SauceDemo Automation Framework |
| Application under test | https://www.saucedemo.com/ |
| Document version | 1.0 |
| Date | 10 November 2026 |
| Prepared for | Software Testing course demonstration |
| Automation stack | Java 25, Selenium WebDriver 4.52, TestNG 7.10.2, Maven, ExtentReports |

---

## 1. Objective

Verify the critical business flows of the SauceDemo web application — login,
product browsing, cart management and checkout — through maintainable,
repeatable automated UI tests, and provide clear evidence of execution through
HTML reports and screenshots.

The project also demonstrates professional test-automation practices:

- Page Object Model separation of tests and UI locators
- explicit synchronisation instead of fixed sleeps
- data-driven negative testing
- isolated test execution
- automatic evidence capture on failure
- configuration-driven execution (browser, headless mode, timeout)

---

## 2. Scope

### In scope

| Area | Coverage |
|---|---|
| Authentication | Valid login, invalid password, unknown user, locked user, empty fields, error dismissal, input masking, login guard, logout, session invalidation |
| Inventory | Product catalogue, product card completeness, four sort orders, add/remove products, cart badge, product details |
| Cart | Empty state, item information, badge consistency, remove one/all, continue shopping, checkout entry, persistence |
| Checkout | Mandatory field validation, order overview, item total, tax and grand total, payment/shipping details, multi-item orders, completion, cancellation |
| Navigation | Burger menu, All Items, Reset App State, footer links, document title |

### Out of scope

- SauceDemo mobile native applications (none exist)
- REST/SOAP API testing (the demo has no public API contract)
- Load, stress or performance testing
- Penetration testing and vulnerability assessment
- Visual regression / pixel comparison
- Third-party destinations reached from the burger menu
  (for example the external **Dynamic Catalog** page)
- Payment gateway integration (checkout is simulated)

---

## 3. Test Approach

### 3.1 Levels and types

| Type | Description |
|---|---|
| System / end-to-end UI testing | Tests drive the real application in a real browser |
| Functional testing | Positive and negative business flows |
| Regression testing | Broad suite executed after every change |
| Smoke testing | Small critical-path subset (`smoke` group) |
| Input validation testing | Empty, invalid and injection-style credentials |
| Session/security-oriented functional checks | Protected-page guard and post-logout session invalidation |

### 3.2 Design techniques

- **Equivalence partitioning** — valid vs invalid credentials, existing vs
  unknown user, empty vs populated cart.
- **Boundary/empty-value analysis** — empty username, empty password, empty
  first name, empty last name, empty postal code.
- **Data-driven testing** — a TestNG `@DataProvider` executes four invalid
  credential combinations through one test method.
- **State-transition testing** — login → inventory → cart → checkout →
  confirmation, and cancellation paths.
- **Risk-based prioritisation** — authentication, add-to-cart and order
  completion are marked `smoke` / P1.

### 3.3 Synchronisation strategy

All waits are explicit (`WebDriverWait` + `ExpectedConditions`) with a
configurable 15-second timeout. No `Thread.sleep` is used. Where the browser
reports an animated overlay control as covered, a JavaScript click is used as
a documented, targeted workaround.

### 3.4 Isolation strategy

`BaseTest` launches a fresh browser before every test and always quits it
afterwards. Each test therefore starts with a clean session, empty cart and
default sort order. Failures in one test cannot contaminate another.

---

## 4. Environment

| Item | Value |
|---|---|
| Operating system | Ubuntu Linux (also portable to Windows/macOS) |
| JDK | 25 |
| Build tool | Maven 3.8+ |
| Browsers | Chrome 157 (default); Firefox and Edge supported |
| Execution mode | Headed for demonstration; headless for CI |
| Application URL | https://www.saucedemo.com/ |
| Test data | Public SauceDemo demo accounts (`standard_user` / `secret_sauce`) |

### Configuration

| Key | Default |
|---|---|
| `base.url` | `https://www.saucedemo.com/` |
| `browser` | `chrome` |
| `headless` | `false` |
| `timeout.seconds` | `15` |
| `standard.user` | `standard_user` |
| `password` | `secret_sauce` |

---

## 5. Test Data

| Data | Value |
|---|---|
| Valid user | `standard_user` / `secret_sauce` |
| Locked user | `locked_out_user` / `secret_sauce` |
| Invalid password | `standard_user` / `wrong_password` |
| Unknown user | `unknown_user` / `secret_sauce` |
| Injection-style input | `' OR '1'='1` |
| Customer information | `Test` / `User` / `110001` |
| Products used | Backpack ($29.99), Bike Light ($9.99), Onesie ($7.99), Fleece Jacket ($49.99) |

Expected tax is validated dynamically as `grand total = item total + tax`,
so the suite is not coupled to a hard-coded tax rate.

---

## 6. Entry and Exit Criteria

### Entry criteria

- Application `https://www.saucedemo.com/` is reachable
- JDK 25 and Maven are installed
- Dependencies resolve successfully (`mvn test-compile`)
- A supported browser is installed

### Exit criteria

- 100% of automated tests executed
- 0 critical (P1) failures
- All failures documented with evidence (screenshot + stack trace)
- ExtentReports dashboard generated
- Test summary recorded in the README

### Suspension criteria

- Application unavailable for more than 15 minutes
- Browser/driver incompatibility that blocks all execution
- Test environment corruption that prevents a clean run

---

## 7. Defect Management

For every failure the framework records:

1. Test name and description
2. Full stack trace
3. Embedded screenshot in the ExtentReports dashboard
4. Screenshot file under `target/screenshots/`
5. Environment details (browser, OS, Java, base URL)

Severity guide:

| Severity | Meaning |
|---|---|
| Critical | Blocks login, cart or order completion |
| Major | Important function incorrect but a workaround exists |
| Minor | Cosmetic, wording or low-impact issue |

---

## 8. Risks and Mitigations

| Risk | Impact | Mitigation |
|---|---|---|
| Public demo site changes | Locators or expected text break | Stable IDs/`data-test` attributes; centralised page objects make updates one-line changes |
| Network latency | Intermittent timeouts | Explicit waits (15 s, configurable); no fixed sleeps |
| Browser/driver mismatch | Tests cannot start | Selenium Manager resolves drivers automatically |
| Test interdependence | Cascading failures | Fresh browser per test; no shared state |
| Animated menu overlay | Click intercepted | Documented JavaScript click for the close control |
| Third-party links | Slow or unavailable external pages | Presence validated; navigation to third parties excluded |
| Demo data changes | Price/name assertions fail | Prices centralised in `TestData` and verified against the live catalogue |

---

## 9. Deliverables

- Automated TestNG suite (64 executions across 5 feature areas)
- Page Object Model framework source code
- ExtentReports HTML dashboard
- Surefire HTML/XML reports
- Failure screenshots
- This test plan
- Test case catalogue (`docs/TEST_CASES.md`)
- README with setup, execution and results

---

## 10. Execution Schedule

| Phase | Activity |
|---|---|
| 1 | Requirement and flow analysis of the demo store |
| 2 | Framework setup (Maven, Selenium, TestNG, reporting) |
| 3 | Page object implementation |
| 4 | Test implementation (authentication → inventory → cart → checkout → navigation) |
| 5 | Smoke execution and stabilisation |
| 6 | Full regression execution and evidence collection |
| 7 | Documentation and demonstration |

---

## 11. Approval

| Role | Name | Signature |
|---|---|---|
| Student / Test Engineer | ______________________ | __________ |
| Course Instructor | ______________________ | __________ |
