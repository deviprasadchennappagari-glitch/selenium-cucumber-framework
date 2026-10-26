package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utils.ConfigReader;

public class SauceDemoLoginPage extends BasePage {

    private final By usernameField = By.id("user-name");
    private final By passwordField = By.id("password");
    private final By loginButton = By.id("login-button");
    private final By productsTitle = By.cssSelector("[data-test='title']");

    public SauceDemoLoginPage(WebDriver driver) {
        super(driver);
    }

    public void openLoginPage() {
        open(ConfigReader.get("saucedemo.url", "https://www.saucedemo.com"));
    }

    public void login(String username, String password) {
        type(usernameField, username);
        type(passwordField, password);
        click(loginButton);
    }

    public String getProductsPageTitle() {
        return waitForVisible(productsTitle).getText();
    }
}
