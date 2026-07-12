package utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Captures screenshots and stores them under target/screenshots so they are
 * always fresh per build and picked up as CI artifacts. Also used to attach
 * screenshots directly into the Cucumber/Extent report on failure.
 */
public final class ScreenshotUtils {

    private static final Logger log = LoggerFactory.getLogger(ScreenshotUtils.class);
    private static final Path SCREENSHOT_DIR = Path.of("target", "screenshots");
    private static final DateTimeFormatter TIMESTAMP =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private ScreenshotUtils() {
    }

    /**
     * Captures a screenshot as raw bytes - suitable for attaching to the
     * Cucumber Scenario report (scenario.attach(bytes, "image/png", name)).
     */
    public static byte[] captureAsBytes(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    /**
     * Captures a screenshot and saves it to disk under target/screenshots,
     * returning the saved file path. Filenames are sanitized and
     * timestamped to avoid collisions.
     */
    public static String captureToFile(WebDriver driver, String testName) {
        try {
            Files.createDirectories(SCREENSHOT_DIR);
            String safeName = testName.replaceAll("[^a-zA-Z0-9-_]", "_");
            String fileName = safeName + "_" + LocalDateTime.now().format(TIMESTAMP) + ".png";
            Path destination = SCREENSHOT_DIR.resolve(fileName);

            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(source.toPath(), destination);

            return destination.toAbsolutePath().toString();
        } catch (IOException e) {
            log.error("Failed to save screenshot for test '{}'", testName, e);
            return null;
        }
    }
}
