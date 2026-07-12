package stepdefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import pages.GooglePage;
import utils.DriverManager;

/**
 * Step definitions for GoogleSearch.feature.
 * This is the sample/reference implementation - to test a different
 * application, add new feature files + step definition classes following
 * this same pattern (Given/When/Then delegating to a Page Object).
 */
public class GoogleSearchSteps {

    private GooglePage googlePage;

    private GooglePage page() {
        if (googlePage == null) {
            googlePage = new GooglePage(DriverManager.getDriver());
        }
        return googlePage;
    }

    @Given("the browser is launched")
    public void theBrowserIsLaunched() {
        // Driver is already initialized in the @Before hook; this step
        // exists purely for readability/BDD flow in the feature file.
        Assertions.assertNotNull(DriverManager.getDriver(), "WebDriver was not initialized");
    }

    @And("the user navigates to the application home page")
    public void theUserNavigatesToTheApplicationHomePage() {
        page().navigateToHomePage();
    }

    @When("the user searches for {string}")
    public void theUserSearchesFor(String searchTerm) {
        page().searchFor(searchTerm);
    }

    @Then("the search results page should be displayed")
    public void theSearchResultsPageShouldBeDisplayed() {
        Assertions.assertTrue(page().areResultsDisplayed(), "Search results were not displayed");
    }

    @And("the results should contain {string}")
    public void theResultsShouldContain(String expectedText) {
        Assertions.assertTrue(page().resultsContainText(expectedText),
                "Expected results to contain: " + expectedText);
    }
}
