/*
 * Copyright 2022 James P. Kaib Jr.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kaib.assessment.filter.movie;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.assertj.core.api.Assertions;

import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class StepDefinitions {

  /** The number of arguments passed to the application. */
  private static final int APP_ARG_COUNT = 3;

  /** The index of the decade test application argument. */
  private static final int DECADE_ARG_IDX = 0;

  /** The index of the input file test application argument. */
  private static final int IN_FILE_ARG_IDX = 1;

  /** The index of the output file test application argument. */
  private static final int OUT_DIR_ARG_IDX = 2;

  /** The decade short option name. */
  private static final String DECADE_SHORT_OPT_NAME = "-d ";

  /** The input file short option name. */
  private static final String IN_FILE_SHORT_OPT_NAME = "-i ";

  /** The output file short option name. */
  private static final String OUT_DIR_SHORT_OPT_NAME = "-o ";

  /** The test decade. */
  private String testDecade;

  /** The test output directory to which the filtered results will be written. */
  private Path testSourceMovieDir;

  /** The test input file. */
  private String testSourceMovieFile;

  /** The test output file name. */
  private String outputFilename;

  /** The test output directory. */
  static Path testOutputDir;

  /** The test program arguments that use the short option names. */
  private String[] testShortOptionArgs;

  /** Instantiates a new step definitions file for the movie filter feature. */
  public StepDefinitions() {
    super();
  }

  /**
   * Tears down the test fixture after all Cucumber steps have run.
   *
   * @throws Exception if something unexpected happens during test fixture tear down
   */
  @AfterAll
  public static void afterAll() throws Exception {
    FileUtils.deleteDirectory(testOutputDir.toFile());
  }

  /**
   * Sets up the test fixture before any Cucumber tests run.
   * @throws Exception
   *
   * @throws Exception if something unexpected happens during test fixture set up
   */
  @BeforeAll
  public static void beforeAll() throws Exception {
    final Path targetDir = Path.of(System.getProperty("java.io.tmpdir"));
    testOutputDir = Files.createTempDirectory(targetDir, "cucumber-movie-filter-test-");
  }

  @Before
  public void before(Scenario scenario) {
    System.out.println("Test output dir " + testOutputDir);

    this.testSourceMovieDir = Path.of("", "src/test/resources");
    this.testShortOptionArgs = new String[APP_ARG_COUNT];
    this.testShortOptionArgs[OUT_DIR_ARG_IDX] = OUT_DIR_SHORT_OPT_NAME + testOutputDir;
  }

  @Given("a JSON file of movies named {string}")
  public void givenAjsonFileOfMovies(final String sourceMovieFilename) {

    this.testSourceMovieFile = this.testSourceMovieDir + File.separator + sourceMovieFilename;
    this.testShortOptionArgs[IN_FILE_ARG_IDX] = IN_FILE_SHORT_OPT_NAME + this.testSourceMovieFile;
  }

  @When("I provide a decade like {int} as a parameter")
  public void whenDecadeIsProvided(final Integer decade) {

    this.testDecade = decade.toString();
    this.testShortOptionArgs[DECADE_ARG_IDX] = DECADE_SHORT_OPT_NAME + this.testDecade;
  }

  @And("I run the application")
  public void andIrunTheApplication() {
    DecadeMovieFilterDriver.main(this.testShortOptionArgs);
  }

  @Then("a file is created called {string} in the data folder")
  public void thenAfileIsCreatedInTheDataFolder(final String outputFilename) {

    this.outputFilename = outputFilename;
    final File actual = testOutputDir.resolve(Path.of(outputFilename)).toFile();
    Assertions.assertThat(actual)
        .as("Filtered movies file not in expected location")
        .exists()
        .isFile();
  }

  @And("it contains a JSON array of movies within the provided decade")
  public void andItContainsAjsonArrayOfMoviesWithinTheProvidedDecade() throws Exception {

    final String expected =
        Files.readString(Path.of("", "src/test/resources/ExpectedFilteredMovies.json"));
    final String actual = Files.readString(testOutputDir.resolve(this.outputFilename));

    Assertions.assertThat(actual).as("The movies weren't filtered correctly").isEqualTo(expected);
  }
}
