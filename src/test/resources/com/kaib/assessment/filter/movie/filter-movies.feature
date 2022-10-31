Feature: Filter movies

  Scenario: Filter movies by decade
    Given a JSON file of movies named "movies.json"
    When I provide a decade like 1980 as a parameter
    And I run the application
    Then a file is created called "1980s-movies.json" in the data folder
    And it contains a JSON array of movies within the provided decade