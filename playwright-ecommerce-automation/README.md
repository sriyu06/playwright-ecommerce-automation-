# Playwright E-commerce Automation (Java)

UI test automation framework for the demo shop [SauceDemo](https://www.saucedemo.com/),
built with **Playwright for Java**, **JUnit 5**, **Maven** and the **Page Object Model**.

## Objectives
- Cover the main e-commerce flows: login, product listing, cart and checkout.
- Keep tests independent, readable and easy to maintain.
- Run locally (headed/headless) and in CI on Chromium, Firefox and WebKit.
- Keep diagnostics (screenshot + trace) for every failed test.

## Technology stack
| Area | Tool |
|------|------|
| Language | Java 17 |
| Browser automation | Playwright for Java 1.49.0 |
| Test runner / assertions | JUnit 5 + Playwright web-first assertions |
| Build | Maven |
| CI | GitHub Actions |

## Project structure
```
playwright-ecommerce-automation/
├── .github/workflows/playwright.yml   # CI pipeline
├── src/
│   ├── main/java/pages/               # Page objects (locators + actions only)
│   │   ├── LoginPage.java
│   │   ├── InventoryPage.java
│   │   ├── ProductDetailsPage.java
│   │   ├── CartPage.java
│   │   └── CheckoutPage.java
│   └── test/
│       ├── java/
│       │   ├── base/BaseTest.java     # Browser/context lifecycle + failure diagnostics
│       │   ├── tests/                 # Test scenarios and assertions
│       │   │   ├── LoginTests.java
│       │   │   ├── InventoryTests.java
│       │   │   ├── CartTests.java
│       │   │   └── CheckoutTests.java
│       │   └── utils/                 # Config, test data, price helpers
│       └── resources/config.properties
├── pom.xml
├── .gitignore
└── README.md
```

Design notes
- Page objects hold locators and actions; **assertions live in the tests**.
- One `Browser` per test class, a **new `BrowserContext` and `Page` per test**, so tests are isolated.
- Locators use SauceDemo's stable `data-test` attributes via `getByTestId` (configured in `BaseTest`), and `getByRole` for buttons.
- No fixed sleeps: Playwright auto-waits for actionability, and assertions retry until they pass or time out.
- No retries are configured, so flaky behaviour is not hidden.

## Test coverage
| Area | Scenarios |
|------|-----------|
| Login | valid login; invalid username; invalid password; empty credentials; empty password; locked-out user (error messages verified) |
| Inventory | 6 products listed; names and prices; sort A-Z, Z-A, price low-high, high-low; product details page |
| Cart | add one / several products; badge count; names and prices in cart; remove from inventory page and from cart page; empty cart |
| Checkout | required-field validation (first name, last name, postal code); overview items and totals; complete purchase and confirmation message |

## Prerequisites
- JDK 17+ (`java -version`)
- Maven 3.8+ (`mvn -version`)
- Git

The first run downloads the Playwright browsers (a few hundred MB), so it needs internet access.

## Running the tests
```bash
# all tests, Chromium, headless (defaults)
mvn test

# one test class
mvn test -Dtest=LoginTests

# one test method
mvn test -Dtest=LoginTests#validUserCanLogin

# headed (visible browser), slowed down by 300 ms per action
mvn test -Dheadless=false -DslowMo=300

# other browsers
mvn test -Dbrowser=firefox
mvn test -Dbrowser=webkit
```
Defaults live in `src/test/resources/config.properties`; any key can be overridden with `-D<key>=<value>`.

## Reports and debugging
- **JUnit/Surefire results:** `target/surefire-reports/`
- **HTML report:** `mvn surefire-report:report-only` then open `target/site/surefire-report.html`
- **Failure screenshots and traces:** `target/artifacts/` (only created for failed tests)
- **Open a trace:**
  ```bash
  mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="show-trace target/artifacts/<trace-file>.zip"
  ```
  Or upload the zip to https://trace.playwright.dev
- **Playwright Inspector:** run with `PWDEBUG=1` (e.g. `PWDEBUG=1 mvn test -Dtest=LoginTests -Dheadless=false`).

## GitHub Actions
Workflow: `.github/workflows/playwright.yml`
- Triggers: push to `main`, pull requests to `main`, and manual run (Actions tab -> *Run workflow*).
- Runs the suite in parallel jobs for Chromium, Firefox and WebKit.
- Installs the browser and OS dependencies, runs `mvn test`, and fails the job if any test fails.
- Uploads surefire reports, the HTML report and failure screenshots/traces as artifacts (`test-results-<browser>`).

To inspect a failed run: Actions tab -> the failed run -> the failed job log -> download the `test-results-<browser>` artifact -> open the trace as described above.

## Known limitations
- Targets a public demo site; its behaviour or markup can change.
- Each test logs in through the UI (simple and isolated, but slower than reusing saved login state).
- Tests run sequentially; parallel execution is not configured.
- Only the standard and locked-out users are covered (not `problem_user`, `performance_glitch_user`, etc.).

## Future improvements
- Reuse login state with Playwright `storageState`.
- Parallel test execution.
- Cover the remaining demo users and logout / burger-menu behaviour.
- Richer reporting (e.g. Allure).
