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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Permission;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.gson.JsonSyntaxException;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;

/**
 * Performs automated tests on the {@link DecadeMovieFilterDriver} class.
 *
 * @author jkaib
 */
final class DecadeMovieFilterDriverTest {

  /** The number of arguments passed to the application. */
  private static final int APP_ARG_COUNT = 3;

  /** The index of the decade test application argument. */
  private static final int DECADE_ARG_IDX = 0;

  /** The index of the input file test application argument. */
  private static final int IN_FILE_ARG_IDX = 1;

  /** The index of the output file test application argument. */
  private static final int OUT_DIR_ARG_IDX = 2;

  /**
   * The expected status code when the JVM exits abnormally. It's a string because it's evaluated as
   * the string message of the {@link SecurityException} thrown by the test {@link SecurityManager}.
   */
  private String abnormalJvmExitCode;

  /**
   * The expected usage message generated when the command-line arguments passed to the application
   * can't be parsed.
   */
  private static String expectedUsage;

  /** The decade long option name. */
  private static final String DECADE_LONG_OPT_NAME = "-decade ";

  /** The decade short option name. */
  private static final String DECADE_SHORT_OPT_NAME = "-d ";

  /** The input file long option name. */
  private static final String IN_FILE_LONG_OPT_NAME = "-input-file ";

  /** The input file short option name. */
  private static final String IN_FILE_SHORT_OPT_NAME = "-i ";

  /** The output file long option name. */
  private static final String OUT_DIR_LONG_OPT_NAME = "-output-dir";

  /** The output file short option name. */
  private static final String OUT_DIR_SHORT_OPT_NAME = "-o ";

  /** The mock that simulates the behavior of the logger manager. */
  @Mocked private LogManager mockLogManager;

  /** The mock that simulates the behavior of the class logger. */
  @Mocked private Logger mockLogger;

  /**
   * The security manager used before any test runs. Cached while a test runs and re-applied between
   * tests.
   */
  private SecurityManager prodSecurityManager;

  /**
   * The security manager that prevents the JVM from shutting down when tests intentionally call
   * {@link System#exit(int)}. Also provides a {@link SecurityException} the test can use to
   * evaluate the JVM exit code.
   */
  private TestSecurityManager testSecurityManager;

  /** The test program arguments that use the long option names. */
  private String[] testLongOptionArgs;

  /** The test program arguments that use the short option names. */
  private String[] testShortOptionArgs;

  /** The test output directory. */
  private String testOutputDirectory;

  /** The test input file. */
  private String testInputFile;

  /** The test decade. */
  private String testDecade;

  @TempDir File tempDir;

  @BeforeAll
  public static void beforeAll() throws Exception {

    final Path path = Path.of("", "src/test/resources/ExpectedUsage.txt");
    expectedUsage = Files.readString(path);
  }

  @BeforeEach
  protected void setUp() throws Exception {

    prodSecurityManager = System.getSecurityManager();
    testSecurityManager = new TestSecurityManager();
    System.setSecurityManager(testSecurityManager);
    this.abnormalJvmExitCode = "-1";
    this.testDecade = "1980";
    this.testInputFile = "movies.json";
    this.testOutputDirectory = this.tempDir.getAbsolutePath();
    this.testLongOptionArgs = new String[APP_ARG_COUNT];
    this.testLongOptionArgs[DECADE_ARG_IDX] = DECADE_LONG_OPT_NAME + this.testDecade;
    this.testLongOptionArgs[IN_FILE_ARG_IDX] = IN_FILE_LONG_OPT_NAME + this.testInputFile;
    this.testLongOptionArgs[OUT_DIR_ARG_IDX] = OUT_DIR_LONG_OPT_NAME + this.testOutputDirectory;
    this.testShortOptionArgs = new String[APP_ARG_COUNT];
    this.testShortOptionArgs[DECADE_ARG_IDX] = DECADE_SHORT_OPT_NAME + this.testDecade;
    this.testShortOptionArgs[IN_FILE_ARG_IDX] = IN_FILE_SHORT_OPT_NAME + this.testInputFile;
    this.testShortOptionArgs[OUT_DIR_ARG_IDX] = OUT_DIR_SHORT_OPT_NAME + this.testOutputDirectory;

    // Mocking the logger because an exception occurs when initializing Log4J after switching over
    // to the test security manager.
    new Expectations() {
      {
        LogManager.getLogger(DecadeMovieFilterDriver.class);
        result = mockLogger;
        minTimes = 0;
      }
    };
  }

  @AfterEach
  public void tearDown() {
    System.setSecurityManager(prodSecurityManager);
  }

  /**
   * Provides invalid command line argument parameters with the associated test labels.
   *
   * @return the invalid command line argument parameters with the associated test labels
   */
  @MethodSource("provideParameters")
  private static Stream<Arguments> invalidArgsParameters() {
    return Stream.of(
        Arguments.of("No Arguments", ",,"),
        Arguments.of("No Decade Argument (short)", ",-o testOutputFile, -s testSourceFile"),
        Arguments.of("No Decade Argument (long)", ",-output testOutputFile, -s testSourceFile"),
        Arguments.of("No Source File Arguments (short)", "-d 1980,,-o testOutputFile"),
        Arguments.of("No Source File Arguments (Long)", "-d 1980,,-o testOutputFile"),
        Arguments.of("No Output File Arguments (short)", "-d 1980,,-s testSourceFile"),
        Arguments.of("No Output File Arguments (Long)", "-d 1980,,-s testSourceFile"));
  }

  @ParameterizedTest(name = "{index} {0}")
  @MethodSource("invalidArgsParameters")
  @DisplayName(
      "Verifies System.exit is called when at least one required command-line argument isn't passed"
          + " to the application")
  void testMain1(final String testDesc, final String argString) {

    // Arrange.
    final String[] testArgs = argString.split(",");

    // Act.
    final String expected = this.abnormalJvmExitCode;

    // Act and assert.
    Assertions.assertThatExceptionOfType(SecurityException.class)
        .as(
            "System.exit should have been called when there was/were  "
                + testDesc
                + " passed to the application")
        .isThrownBy(() -> DecadeMovieFilterDriver.main(testArgs))
        .withMessage(expected);
  }

  @Test()
  @DisplayName(
      "Verifies the usage message is correct when at least one command-line argument isn't passed"
          + " to the application")
  void testMain2() {

    // Arrange.
    final ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(testOutputStream));
    final String[] testArgs = {};

    // Act.
    String actual = null;
    // I would normally *never* put a try-catch in a unit test, but I need to capture the
    // redirected standard out before the JVM exits.
    try {
      DecadeMovieFilterDriver.main(testArgs);
    } catch (Exception e) {
      actual = testOutputStream.toString();
    } finally {

      // Assert.
      Assertions.assertThat(actual)
          .as(
              "Incorrect usage message when at least one command-line"
                  + " argument wasn't passed to the application")
          .isEqualTo(expectedUsage);
    }
  }

  @Test
  @DisplayName("Verify the system exit status is correct when the movies source file can't be read")
  void testMain3() {

    // Arrange.
    // The invalid file name will cause the test to fail either because it doesn't exist on the
    // machine running the test or if it does, it's a directory, not a file.
    this.testLongOptionArgs[IN_FILE_ARG_IDX] = IN_FILE_LONG_OPT_NAME + "/temp/non-existent.json";
    final String expected = this.abnormalJvmExitCode;

    // Act and assert.
    Assertions.assertThatExceptionOfType(SecurityException.class)
        .as(
            "System.exit should have been called with an abnormal exit code when the input file"
                + " path is invalid")
        .isThrownBy(() -> DecadeMovieFilterDriver.main(this.testLongOptionArgs))
        .withMessage(expected);
  }

  @Test()
  @DisplayName(
      "Verifies an IOException is the cause of the abnormal JVM exit when the input file argument"
          + " is a directory")
  void testMain4() {

    // Arrange.
    this.testLongOptionArgs[IN_FILE_ARG_IDX] = IN_FILE_LONG_OPT_NAME + "/temp";
    final String expected =
        "Can't load the JSON structure containing the movies supported by the application";

    // Act.
    // I would normally *never* put a try-catch in a unit test, but I need to capture the
    // redirected standard out before the JVM exits.
    try {
      DecadeMovieFilterDriver.main(this.testLongOptionArgs);
    } catch (SecurityException e) {

      // Assert.
      new Verifications() {
        {
          mockLogger.error(expected, (IOException) any);
        }
      };
    }
  }

  @Test()
  @DisplayName(
      "Verifies an JsonSyntaxException is the cause of the abnormal JVM exit when the input file"
          + " argument represents a file that doesn't contain a valid JSON structure")
  void testMain5() {

    // Arrange.
    final Path testFilePath = Path.of("", "src/test/resources/InvalidMovies.json");
    this.testLongOptionArgs[IN_FILE_ARG_IDX] = IN_FILE_LONG_OPT_NAME + testFilePath.toString();
    final String expected =
        "Can't load the JSON structure containing the movies supported by the application";

    // Act.
    // I would normally *never* put a try-catch in a unit test, but I need to capture the
    // contents of the log entry before the JVM exits.
    try {
      DecadeMovieFilterDriver.main(this.testLongOptionArgs);
    } catch (SecurityException e) {

      // Assert.
      new Verifications() {
        {
          mockLogger.error(expected, (JsonSyntaxException) any);
        }
      };
    }
  }

  @Test
  @DisplayName(
      "Verifies System.exit is called when the output directy doesn't exist")
  void testMain6() {

    // Arrange.
    final Path testFilePath = Path.of("", "src/test/resources/movies.json");
    this.testLongOptionArgs[IN_FILE_ARG_IDX] = IN_FILE_LONG_OPT_NAME + testFilePath.toString();
    this.testLongOptionArgs[OUT_DIR_ARG_IDX] =
        OUT_DIR_LONG_OPT_NAME + this.testOutputDirectory + File.separator + "Unknown";

    // Act.
    final String expected = this.abnormalJvmExitCode;

    // Act and assert.
    Assertions.assertThatExceptionOfType(SecurityException.class)
        .as(
            "System.exit should have been called when the output directory doesn't exist")
        .isThrownBy(() -> DecadeMovieFilterDriver.main(this.testLongOptionArgs))
        .withMessage(expected);
  }

  @Test()
  @DisplayName(
      "Verifies an IOException is the cause of the abnormal JVM exit when the output file can't"
          + " be written to disk")
  void testMain7() {

    // Arrange.
    final String testFilePath = Path.of("", "src/test/resources/movies.json").toString();
    this.testShortOptionArgs[IN_FILE_ARG_IDX] = IN_FILE_SHORT_OPT_NAME + testFilePath;
    this.testShortOptionArgs[OUT_DIR_ARG_IDX] = OUT_DIR_SHORT_OPT_NAME + this.tempDir;
    final String expected =
        "Can't write the JSON structure containing the filtered movies to the destination file";

    // Act.
    // I would normally *never* put a try-catch in a unit test, but I need to capture the
    // contents of the log entry before the JVM exits.
    try {
      DecadeMovieFilterDriver.main(this.testShortOptionArgs);
    } catch (SecurityException e) {

      // Assert.
      new Verifications() {
        {
          mockLogger.error(expected, (IOException) any);
        }
      };
    }
  }

  @Test()
  @DisplayName("Verifies the movies were filtered correctly")
  void testMain8() throws Exception {

    // Arrange.
    final String testInputFilePath =
        Path.of("", "src/test/resources/" + this.testInputFile).toString();
    final String testOutputFilePath =
        this.testOutputDirectory + File.separator + this.testDecade + "s-movies.json";
    this.testShortOptionArgs[IN_FILE_ARG_IDX] = IN_FILE_SHORT_OPT_NAME + testInputFilePath;
    this.testShortOptionArgs[OUT_DIR_ARG_IDX] = OUT_DIR_SHORT_OPT_NAME + this.testOutputDirectory;
    final String expected =
        Files.readString(Path.of("", "src/test/resources/ExpectedFilteredMovies.json"));

    // Act.
    DecadeMovieFilterDriver.main(this.testShortOptionArgs);
    final String actual = Files.readString(tempDir.toPath().resolve(Path.of(testOutputFilePath)));
    System.out.println(actual);

    // Assert.
    Assertions.assertThat(actual).as("The movies weren't filtered correctly").isEqualTo(expected);
  }

  /**
   * Simple {@link SecurityManager} implementation that allows tests to detect when {@code
   * System.exit} has been called by throwing a {@link SecurityException} the test can evaluate. It
   * also prevents the test JVM from shutting down.
   *
   * @author jkaib
   */
  private static class TestSecurityManager extends SecurityManager {

    /**
     * {@inheritDoc}
     *
     * <p>Only responds to the check for the "exitVM' permission.
     */
    @Override
    public void checkPermission(final Permission permission) {
      if ("exitVM".equals(permission.getName())) {
        throw new SecurityException("System.exit attempted and blocked.");
      }
    }

    @Override
    public void checkExit(int status) {
      throw new SecurityException(Integer.toString(status));
    }
  }
}
