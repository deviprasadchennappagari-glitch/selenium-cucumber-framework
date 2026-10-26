@smoke @saucedemo
Feature: SauceDemo login
  As a user
  I want to sign in to SauceDemo
  So that I can view the products page

  Scenario: Sign in with the public demo account
    Given the user opens the SauceDemo login page
    When the user logs in with username "standard_user" and password "secret_sauce"
    Then the products page should display the heading "Products"
