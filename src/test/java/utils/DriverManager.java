package utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * Thread-safe WebDriver factory/holder. Using a ThreadLocal allows this
 * framework to run scenarios in parallel in the future without drivers
 * bleeding across threads.
 *
 * Supports chrome / firefox / edge, driven entirely by config.properties
 * (or -Dbrowser=... / -Dheadless=... system property overrides), so the
 * same framework can target any application under test.
 */
public final class DriverManager {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static WebDriver getDriver() {
        if (DRIVER.get() == null) {
            initDriver();
        }
        return DRIVER.get();
    }

    public static void initDriver() {
        String browser = ConfigReader.get("browser", "chrome").toLowerCase();
        boolean headless = ConfigReader.getBoolean("headless", false);

        WebDriver driver = switch (browser) {
            case "firefox" -> createFirefoxDriver(headless);
            case "edge" -> createEdgeDriver(headless);
            case "chrome" -> createChromeDriver(headless);
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };

        driver.manage().timeouts().implicitlyWait(
                Duration.ofSeconds(ConfigReader.getInt("implicit.wait", 5)));
        driver.manage().timeouts().pageLoadTimeout(
                Duration.ofSeconds(ConfigReader.getInt("page.load.timeout", 30)));

        if (ConfigReader.getBoolean("maximize.window", true) && !headless) {
            driver.manage().window().maximize();
        }

        DRIVER.set(driver);
    }

    private static WebDriver createChromeDriver(boolean headless) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-notifications");
        options.addArguments("--window-size=1920,1080");
        return new ChromeDriver(options);
    }

    private static WebDriver createFirefoxDriver(boolean headless) {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless");
        }
        return new FirefoxDriver(options);
    }

    private static WebDriver createEdgeDriver(boolean headless) {
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        return new EdgeDriver(options);
    }

    /**
     * Quits the driver for the current thread and clears the ThreadLocal
     * reference to avoid memory leaks / stale sessions.
     */
    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                DRIVER.remove();
            }
        }
    }
}
