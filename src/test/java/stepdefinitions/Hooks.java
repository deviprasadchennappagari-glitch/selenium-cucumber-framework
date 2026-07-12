package stepdefinitions;

import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigReader;
import utils.DriverManager;
import utils.ProcessUtils;
import utils.ScreenshotUtils;

/**
 * Lifecycle hooks shared by every scenario:
 *  - @Before  : starts a fresh browser session
 *  - @After   : captures a screenshot on failure (attached to the report),
 *               always quits the driver, and sweeps orphan driver processes
 *  - @AfterAll: final safety-net process cleanup once the whole suite ends
 */
public class Hooks {

    private static final Logger log = LoggerFactory.getLogger(Hooks.class);

    @Before
    public void setUp(Scenario scenario) {
        log.info("Starting scenario: {}", scenario.getName());
        DriverManager.initDriver();
    }

    @After
    public void tearDown(Scenario scenario) {
        WebDriver driver = DriverManager.getDriver();
        try {
            if (scenario.isFailed()) {
                byte[] screenshot = ScreenshotUtils.captureAsBytes(driver);
                scenario.attach(screenshot, "image/png", scenario.getName());
                ScreenshotUtils.captureToFile(driver, scenario.getName());
                log.warn("Scenario FAILED: {} - screenshot captured", scenario.getName());
            } else if (ConfigReader.getBoolean("screenshot.on.every.step", false)) {
                byte[] screenshot = ScreenshotUtils.captureAsBytes(driver);
                scenario.attach(screenshot, "image/png", scenario.getName());
            }
        } finally {
            DriverManager.quitDriver();
            // Belt-and-braces cleanup: kill any chromedriver/geckodriver/
            // msedgedriver process left behind by a crashed session.
            ProcessUtils.killOrphanDriverProcesses();
        }
    }

    @AfterAll
    public static void globalCleanup() {
        ProcessUtils.killOrphanDriverProcesses();
        log.info("Suite finished - orphan driver process sweep complete.");
    }
}
