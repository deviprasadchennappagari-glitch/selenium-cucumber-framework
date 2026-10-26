


package runners;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * Entry point for running all Cucumber scenarios via `mvn test`.
 *
 * Reports produced:
 *  - target/cucumber-reports/cucumber.html   (plain Cucumber HTML report)
 *  - target/cucumber-reports/cucumber.json   (machine readable, for CI)
 *  - target/extent-reports/ExtentDashboard.html (rich dashboard with charts,
 *    pass/fail trends, embedded failure screenshots)
 *
 * Filter by tag: mvn test -Dcucumber.filter.tags="@smoke"
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "stepdefinitions")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value =
        "pretty, " +
        "html:target/cucumber-reports/cucumber.html, " +
        "json:target/cucumber-reports/cucumber.json, " +
        "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:")
public class TestRunner {
}
