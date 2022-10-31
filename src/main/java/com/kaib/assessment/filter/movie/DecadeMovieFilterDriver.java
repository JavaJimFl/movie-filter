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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Set;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * Entry point into the decade-based movie filter. Performs command line validation before invoking
 * the filter itself.
 *
 * @author jkaib
 */
public final class DecadeMovieFilterDriver {

  /** The command-line option for the decade of interest. */
  private static final Option DECADE_OPTION =
      Option.builder("d")
          .required(true)
          .longOpt("decade")
          .hasArg()
          .type(Number.class)
          .desc("The decade of interest in the format yyyy")
          .build();

  /** The command-line option for the filtered movies file. */
  private static final Option DESTINATION_DIR_OPTION =
      Option.builder("o")
          .required(true)
          .longOpt("output-dir")
          .hasArg()
          .desc("The path to the filtered movies output directory")
          .build();

  /** The JSON serializer/deserializer. */
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  /** The logger for this class. */
  private static final Logger LOG = LogManager.getLogger(DecadeMovieFilterDriver.class);

  /** The command-line option for the source movies file. */
  private static final Option SOURCE_FILE_OPTION =
      Option.builder("i")
          .required(true)
          .longOpt("input-file")
          .hasArg()
          .desc("The path to the file containing all movies supported by the application")
          .build();

  /** Private constructor prevents instantiation. */
  private DecadeMovieFilterDriver() {

    super();
  }

  /**
   * Binds the JSON structure in the specified source file to the set of {@link Movies} supported by
   * the application.
   *
   * @param sourceFilePath the file that contains the supported movies in JSON format
   * @return the movies supported by the application
   * @throws IOException if the file can't be read
   */
  private static Set<Movie> bindSourceFile(final Path sourceFilePath) throws IOException {

    final String json = readSourceFile(sourceFilePath);
    final var serializedType = new TypeToken<Set<Movie>>() {}.getType();

    return GSON.fromJson(json, serializedType);
  }

  /**
   * Builds the required command-line options.
   *
   * @return the required command-line options
   */
  private static Options buildClOptions() {

    final var options = new Options();
    options.addOption(DECADE_OPTION);
    options.addOption(DESTINATION_DIR_OPTION);
    options.addOption(SOURCE_FILE_OPTION);

    return options;
  }

  /**
   * Builds the movie service using the specified movies.
   *
   * @param allMovies the movies supported by the application
   * @return a properly configured movie service
   */
  private static MovieService buildMovieService(final Set<Movie> allMovies) {
    return new MovieService(new MovieRepository(allMovies));
  }

  /**
   * Returns the subset of movies released in the specified decade.
   *
   * @param movieService filters all movies to those in the specified decade
   * @param decade the decade of interest
   * @return the subset of movies released in the specified decade
   */
  private static Set<Movie> filterMovies(final MovieService movieService, final int decade) {
    return movieService.filter(decade);
  }

  /**
   * The entry point into the application.
   *
   * @param args the command-line arguments
   */
  public static void main(final String[] args) {

    // Please note the use of the Single Level of Abstraction principle here.
    ApplicationArgs applicationArgs = null;
    try {
      applicationArgs = parseCommandline(args);
    } catch (final ParseException e) {
      LOG.error("Can't parse the command line arguments", e);
      final var formatter = new HelpFormatter();
      formatter.printHelp("DecadeMovieFilterDriver", buildClOptions());
      System.exit(-1);
    }
    Set<Movie> allMovies = null;
    try {
      allMovies = bindSourceFile(applicationArgs.getSourceFilePath());
    } catch (final IOException | JsonSyntaxException e) {
      LOG.error(
          "Can't load the JSON structure containing the movies supported by the application", e);
      System.exit(-1);
    }
    final var movieService = buildMovieService(allMovies);
    final Set<Movie> filteredMovies = filterMovies(movieService, applicationArgs.getDecade());
    try {
      writeFilteredResults(filteredMovies, applicationArgs.getDestFilePath());
    } catch (final IOException e) {
      LOG.error(
          "Can't write the JSON structure containing the filtered movies to the destination file",
          e);
      System.exit(-1);
    }
  }

  /**
   * Parses the command-line arguments into a strongly typed set.
   *
   * @param appArgs the string arguments passed into the application
   * @return the strongly type versions of the command line arguments
   * @throws ParseException if the command-line couldn't be parsed
   */
  private static ApplicationArgs parseCommandline(final String[] appArgs) throws ParseException {

    final var clOptions = buildClOptions();
    final CommandLineParser clParser = new DefaultParser();
    final var commandLine = clParser.parse(clOptions, appArgs);

    final String decadeArg = commandLine.getOptionValue(DECADE_OPTION).trim();
    final var decade = Integer.parseInt(decadeArg);
    final var destFilePath =
        Path.of(
            commandLine.getOptionValue(DESTINATION_DIR_OPTION).trim(),
            decadeArg + "s-movies.json");
    final var sourceFilePath = Path.of(commandLine.getOptionValue(SOURCE_FILE_OPTION).trim());

    return new ApplicationArgs(decade, destFilePath, sourceFilePath);
  }

  /**
   * Reads the specified movie source file to a JSON string.
   *
   * @param sourceFilePath the file containing the movies supported by the application
   * @return the specified movie source file as a JSON string
   * @throws IOException if the file can't be read
   */
  private static String readSourceFile(final Path sourceFilePath) throws IOException {
    return Files.readString(sourceFilePath);
  }

  /**
   * Writes the specified movies to the specified file as a JSON structure.
   *
   * @param filteredMovies the movies to write as a JSON structure
   * @param destFilePath the file to which the JSON structure will be written
   * @throws IOException if the file can't be written
   */
  private static void writeFilteredResults(final Set<Movie> filteredMovies, final Path destFilePath)
      throws IOException {

    final String json = GSON.toJson(filteredMovies);
    Files.writeString(destFilePath, json, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
  }

  /** Simple container that holds the strongly-typed application arguments. */
  private static final class ApplicationArgs {

    /** The desired decade by which the movies will be filtered. */
    private final int decade;

    /** The file to which the results will be written. */
    private final Path destFilePath;

    /**
     * The file containing the JSON structure representing the movies supported by the application.
     */
    private final Path sourceFilePath;

    /**
     * Instantiates a new application arguments container.
     *
     * @param theDecade the desired decade by which the movies will be filtered
     * @param theDestFilePath the file to which the results will be written
     * @param theSourceFilePath the file containing the JSON structure representing the movies
     *     supported by the application
     */
    public ApplicationArgs(
        final int theDecade, final Path theDestFilePath, final Path theSourceFilePath) {

      this.decade = theDecade;
      this.destFilePath = theDestFilePath;
      this.sourceFilePath = theSourceFilePath;
    }

    /**
     * Gets the desired decade by which the movies will be filtered.
     *
     * @return the desired decade by which the movies will be filtered
     */
    public int getDecade() {
      return this.decade;
    }

    /**
     * Gets the file to which the results will be written.
     *
     * @return the file to which the results will be written
     */
    public Path getDestFilePath() {
      return this.destFilePath;
    }

    /**
     * Gets the file containing the JSON structure representing the movies supported by the
     * application.
     *
     * @return the file containing the JSON structure representing the movies supported by the
     *     application
     */
    public Path getSourceFilePath() {
      return this.sourceFilePath;
    }
  }
}
