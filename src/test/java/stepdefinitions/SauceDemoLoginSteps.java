package stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import pages.SauceDemoLoginPage;
import utils.DriverManager;

public class SauceDemoLoginSteps {

    private SauceDemoLoginPage loginPage;

    private SauceDemoLoginPage loginPage() {
        if (loginPage == null) {
            loginPage = new SauceDemoLoginPage(DriverManager.getDriver());
        }
        return loginPage;
    }

    @Given("the user opens the SauceDemo login page")
    public void theUserOpensTheSauceDemoLoginPage() {
        loginPage().openLoginPage();
    }

    @When("the user logs in with username {string} and password {string}")
    public void theUserLogsIn(String username, String password) {
        loginPage().login(username, password);
    }

    @Then("the products page should display the heading {string}")
    public void theProductsPageShouldDisplayTheHeading(String expectedHeading) {
        Assertions.assertEquals(expectedHeading, loginPage().getProductsPageTitle());
    }
}
