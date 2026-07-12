package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import utils.ConfigReader;

/**
 * Page Object for the Google homepage / results page.
 * This is the sample page used by the bundled smoke test - use it as a
 * template for adding page objects for your own application under test.
 */
public class GooglePage extends BasePage {

    private final By searchBox = By.name("q");
    private final By resultsContainer = By.id("search");

    public GooglePage(WebDriver driver) {
        super(driver);
    }

    public void navigateToHomePage() {
        open(ConfigReader.get("base.url", "https://www.google.com"));
    }

    public void searchFor(String term) {
        type(searchBox, term);
        driver.findElement(searchBox).sendKeys(Keys.ENTER);
    }

    public boolean areResultsDisplayed() {
        return isDisplayed(resultsContainer);
    }

    public boolean resultsContainText(String expectedText) {
        String pageText = driver.findElement(resultsContainer).getText();
        return pageText.toLowerCase().contains(expectedText.toLowerCase());
    }
}
