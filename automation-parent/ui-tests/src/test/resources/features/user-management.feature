@playwright @regression
Feature: User management through the Service Desk browser

  Scenario: Create, search, edit and delete a user
    Given the Service Desk user management page is open
    When I create a unique user
    And I search for the created user
    And I edit the user's name
    Then the updated user is displayed
    When I delete the user
    Then the deleted user is absent
