# Defect-Detection Demonstration

## Purpose

SauceDemo publishes four deliberately faulty demo accounts. This demo suite
asserts the behaviour a correct shop should provide, so its tests **fail on
purpose** against those accounts. It proves that the framework can detect real
application defects rather than only confirming happy paths.

This suite is **not part of the main regression suite**. It is excluded from
`testng.xml` and from the reported 64/64 regression result.

## Accounts and defects exercised

| Account | Test | Expected behaviour | Observed defect |
|---|---|---|---|
| `problem_user` | `problemUserProductImagesAreValid` | Six distinct valid product images | All six images use the `sl-404` placeholder |
| `problem_user` | `problemUserSortNameZToAWorks` | Red T-shirt first after Z-A sort | Backpack remains first; sorting does not apply |
| `problem_user` | `problemUserRemoveProductUpdatesCart` | Badge clears after removal | Badge remains at 1 |
| `error_user` | `errorUserSortingDoesNotRaiseAlert` | Sorting works without an alert | Alert: “Sorting is broken! This error has been reported to Backtrace.” |
| `visual_user` | `visualUserProductImagesAreValid` | No broken product images | One product image uses the `sl-404` placeholder |
| `performance_glitch_user` | `performanceGlitchUserLoadsWithinBudget` | Inventory loads within 3 s | Inventory took about 5.08 s |

## Run the demo

```bash
mvn test -Dsurefire.suiteXmlFiles=src/test/resources/testng-defect-demo.xml
```

Expected result:

```
Tests run: 6, Failures: 6, Errors: 0, Skipped: 0
```

The build fails because the assertions correctly detect the defects. This is
the intended outcome for this demonstration.

## Evidence

- Demo ExtentReports dashboard:
  `report/defect-demo/SauceDemo-Defect-Demo-Report.html`
- Failure screenshots:
  `report/defect-demo/screenshots/`

Each failure includes the assertion message, the application state and a
screenshot captured by `TestListener`.

## Main regression suite

The main suite remains unchanged and passing:

```bash
mvn clean test -Dheadless=true
```

```
Tests run: 64, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Use the demo suite to show defect detection; use the main suite to show a clean
regression result. Do not combine the two counts.
