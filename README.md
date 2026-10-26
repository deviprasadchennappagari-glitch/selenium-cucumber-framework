# Selenium + Cucumber BDD Test Automation Framework

A reusable, production-ready UI test automation framework built with **Java, Selenium WebDriver, Cucumber (BDD), JUnit 5, WebDriverManager and ExtentReports**.

It ships with a working sample test (Google Search) so you can clone it, run it immediately, and see reports/dashboards generate end-to-end. The framework itself is application-agnostic — point it at any website by adding feature files, step definitions and page objects.

---

## Features

- **BDD with Cucumber** — human-readable `.feature` files (Gherkin), reusable step definitions
- **Page Object Model** — clean separation between test logic and page locators (`BasePage` gives every page reusable wait/click/type helpers)
- **Multi-browser** — Chrome / Firefox / Edge, switchable via config or CLI flag
- **Headless support** — for CI pipelines
- **Auto driver management** — WebDriverManager downloads the correct driver binary automatically, no manual setup
- **Automatic screenshots on failure** — attached directly inside the report, plus saved to `target/screenshots`
- **Orphan process cleanup** — kills leftover `chromedriver`/`geckodriver`/`msedgedriver` processes after every scenario and at the end of the suite, so failed runs never leave zombie processes behind
- **Professional HTML dashboard** — ExtentReports Spark reporter (charts, pass/fail %, timeline, tags/categories, embedded screenshots) plus the standard Cucumber HTML/JSON report
- **CI-ready** — GitHub Actions workflow included, runs headless and uploads reports/screenshots as build artifacts
- **Config-driven** — browser, URL, timeouts, headless mode all controlled from `config.properties` or CLI overrides, no code changes needed

---

## Project Structure

```
selenium-cucumber-framework/
├── pom.xml
├── .github/workflows/tests.yml        # CI pipeline
├── scripts/
│   ├── kill-drivers.sh                # manual cleanup (Linux/Mac)
│   └── kill-drivers.bat               # manual cleanup (Windows)
└── src/test/
    ├── java/
    │   ├── runners/
    │   │   └── TestRunner.java        # Cucumber + JUnit5 entry point
    │   ├── stepdefinitions/
    │   │   ├── Hooks.java             # setup/teardown, screenshots, cleanup
    │   │   └── GoogleSearchSteps.java # sample step definitions
    │   ├── pages/
    │   │   ├── BasePage.java          # shared page-object helpers
    │   │   └── GooglePage.java        # sample page object
    │   └── utils/
    │       ├── ConfigReader.java      # reads config.properties (+ -D overrides)
    │       ├── DriverManager.java     # thread-safe WebDriver factory
    │       ├── ScreenshotUtils.java   # capture screenshots
    │       └── ProcessUtils.java      # kills orphan driver processes
    └── resources/
        ├── config.properties          # browser, URL, timeouts, headless
        ├── extent.properties          # ExtentReports dashboard config
        ├── spark-config.json          # dashboard theme
        ├── junit-platform.properties
        └── features/
            ├── GoogleSearch.feature   # Google search sample
            └── SauceDemoLogin.feature # web app login sample
```

---

## Prerequisites

- Java 25+
- Maven 3.9+
- Google Chrome (or Firefox/Edge) installed locally — driver binaries are downloaded automatically

---

## Getting Started

```bash
# 1. Clone
git clone <your-repo-url>
cd selenium-cucumber-framework

# 2. Run all sample scenarios
mvn test
```

That's it — Maven will download dependencies, WebDriverManager will fetch the matching ChromeDriver, and the Google Search and SauceDemo login examples will run.

### Try the web application example

The SauceDemo login scenario demonstrates testing a web application using its public demo site and sample account:

- URL: `https://www.saucedemo.com`
- Username: `standard_user`
- Password: `secret_sauce`

Run only this scenario with:

```bash
mvn test -Dheadless=true -Dcucumber.filter.tags="@saucedemo"
```

The SauceDemo URL can be overridden for another SauceDemo environment:

```bash
mvn test -Dheadless=true -Dsaucedemo.url=https://your-app.example.com -Dcucumber.filter.tags="@saucedemo"
```

For a different application, update the page object's locators and adapt the feature/step definitions to that application's flow.
The example credentials are public demo credentials only. Use environment variables or GitHub Secrets for credentials belonging to your own application.

### Run headless (e.g. for CI or a machine without a display)

```bash
mvn test -Dheadless=true
```

### Run against a different browser

```bash
mvn test -Dbrowser=firefox
mvn test -Dbrowser=edge
```

### Run only tests with a specific tag

```bash
mvn test -Dcucumber.filter.tags="@smoke"
```

---

## Reports & Dashboards

After a run, open:

| Report | Path |
|---|---|
| **ExtentReports dashboard** (recommended) | `target/extent-reports/ExtentDashboard.html` |
| Cucumber HTML report | `target/cucumber-reports/cucumber.html` |
| Cucumber JSON (for CI tooling) | `target/cucumber-reports/cucumber.json` |
| Screenshots (failures) | `target/screenshots/` |

The ExtentReports dashboard includes pass/fail charts, execution timeline, tag/category breakdown, and failure screenshots embedded inline per step — this is the one to share with stakeholders.

---

## How Process Cleanup Works

Every scenario ends by calling `DriverManager.quitDriver()` followed by `ProcessUtils.killOrphanDriverProcesses()`, and the suite runs one final sweep in `@AfterAll`. This guards against zombie `chromedriver`/`geckodriver`/`msedgedriver` processes if a browser crashes or a test is force-killed mid-run.

If you ever need to clean up manually (e.g. after a hard JVM crash):

```bash
# Linux/Mac
./scripts/kill-drivers.sh

# Windows
scripts\kill-drivers.bat
```

---

## Adapting the Framework to a New Application

This is the whole point of the framework — reuse it for any web app:

1. **Add a feature file** in `src/test/resources/features/YourFeature.feature`
2. **Add a Page Object** in `src/test/java/pages/` extending `BasePage` (get `waitForVisible`, `click`, `type`, `isDisplayed` for free)
3. **Add step definitions** in `src/test/java/stepdefinitions/` that call your page object
4. **Update `config.properties`** — set `base.url` to your application's URL
5. Run `mvn test` — no other changes needed. `Hooks`, `DriverManager`, screenshots and reporting all work automatically for the new tests.

Example of a minimal new page object:

```java
public class LoginPage extends BasePage {
    private final By usernameField = By.id("username");
    private final By passwordField = By.id("password");
    private final By loginButton = By.id("loginBtn");

    public LoginPage(WebDriver driver) { super(driver); }

    public void login(String user, String pass) {
        type(usernameField, user);
        type(passwordField, pass);
        click(loginButton);
    }
}
```

---

## Configuration Reference (`config.properties`)

| Property | Default | Description |
|---|---|---|
| `browser` | `chrome` | `chrome` \| `firefox` \| `edge` |
| `base.url` | `https://www.google.com` | Application under test |
| `saucedemo.url` | `https://www.saucedemo.com` | URL used by the SauceDemo login example |
| `headless` | `false` | Run browser headless |
| `implicit.wait` | `5` | Implicit wait (seconds) |
| `explicit.wait` | `15` | Explicit wait used by `BasePage` (seconds) |
| `page.load.timeout` | `30` | Page load timeout (seconds) |
| `maximize.window` | `true` | Maximize browser on launch |
| `screenshot.on.every.step` | `false` | Attach a screenshot after every scenario, not just failures |

Any property can be overridden without touching the file, e.g. `mvn test -Dbase.url=https://example.com`.

---

## Continuous Integration

A ready-to-use GitHub Actions workflow is included at `.github/workflows/tests.yml`. It runs the suite headlessly on every push/PR to `master` and uploads the Extent dashboard, Cucumber report, and screenshots as build artifacts. Adapt the same pattern for Jenkins/GitLab/Azure DevOps if needed — the underlying command is just `mvn test -Dheadless=true`.

---

## License

Use freely as a starting point for your own automation projects.
