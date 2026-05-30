@smoke @regression
Feature: Sample API

  @smoke
  Scenario: Get user by id
    When I send GET request to "/users/1"
    Then status code should be 200
    And response should contain "Leanne Graham"
    And response should match schema "user.json"

  @regression
  Scenario: Get post by id
    When I send GET request to "/posts/1"
    Then status code should be 200
    And response should contain "sunt aut facere"
    And response should match schema "post.json"
