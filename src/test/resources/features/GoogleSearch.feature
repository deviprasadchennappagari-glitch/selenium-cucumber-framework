@smoke @google
Feature: Google Search
  As a user
  I want to search on Google
  So that I can find relevant results

  Background:
    Given the browser is launched
    And the user navigates to the application home page

  @sanity
  Scenario: Search for a single term on Google
    When the user searches for "Selenium Cucumber BDD Framework"
    Then the search results page should be displayed
    And the results should contain "Selenium"

  Scenario Outline: Search for multiple terms on Google
    When the user searches for "<searchTerm>"
    Then the search results page should be displayed

    Examples:
      | searchTerm          |
      | Cucumber BDD         |
      | Java Selenium        |
      | Test Automation      |
