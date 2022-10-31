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

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Performs automated tests on the {@code MovieRepository} class.
 *
 * @author jkaib
 */
final class MovieRepositoryTest {

  /** The test release year for the first test movie. */
  private int testMovie1ReleaseYear;

  /** The test release year for the second test movie. */
  private int testMovie2ReleaseYear;

  /** The test release year for the third test movie. */
  private int testMovie3ReleaseYear;

  /** The test release year for the fourth test movie. */
  private int testMovie4ReleaseYear;

  /** The first test movie. */
  private Movie testMovie1;

  /** The second test movie. */
  private Movie testMovie2;

  /** The third test movie. */
  private Movie testMovie3;

  /** The fourth test movie. */
  private Movie testMovie4;

  /** The test movies. */
  private Set<Movie> testMovies;

  /** The unit under test. */
  private MovieRepository uut;

  @BeforeEach
  protected void setUp() throws Exception {

    // This is an example of boundary testing.
    this.testMovie1 = new Movie();
    this.testMovie1ReleaseYear = 1999;
    this.testMovie1.setYear(testMovie1ReleaseYear);

    this.testMovie2 = new Movie();
    this.testMovie2ReleaseYear = 2000;
    this.testMovie2.setYear(this.testMovie2ReleaseYear);

    this.testMovie3 = new Movie();
    this.testMovie3ReleaseYear = 2009;
    this.testMovie3.setYear(testMovie3ReleaseYear);

    this.testMovie4 = new Movie();
    this.testMovie4ReleaseYear = 2010;
    this.testMovie4.setYear(this.testMovie4ReleaseYear);

    this.testMovies =
        new HashSet<>(
            Arrays.asList(this.testMovie1, this.testMovie2, this.testMovie3, this.testMovie4));

    this.uut = new MovieRepository(this.testMovies);
  }

  @Test
  @DisplayName("Verifies a set of movies is required by the repostitory")
  void testMovieService1() {

    // Act and assert.
    Assertions.assertThatNullPointerException()
        .as("A NullPointerException should have been thrown when set of movies was null")
        .isThrownBy(() -> new MovieRepository(null))
        .withMessage("The respository requires at least one movie");
  }

  @Test
  @DisplayName("Verifies the repository contains at least one movie")
  void testMovieService2() {

    // Act and assert.
    Assertions.assertThatIllegalArgumentException()
        .as("A IllegalArgumentException should have been thrown when the set of movies was empty")
        .isThrownBy(() -> new MovieRepository(new HashSet<>()))
        .withMessage("The respository requires at least one movie");
  }

  @Test
  @DisplayName("Verifies the constructor set a defensive copy of the movies injected into it")
  void testMovieService3() throws Exception, Exception {

    // Act.
    final Field field = this.uut.getClass().getDeclaredField("movies");
    field.setAccessible(true);
    @SuppressWarnings("unchecked")
    final Set<Movie> actuals = (Set<Movie>) field.get(this.uut);

    // Assert.
    Assertions.assertThat(actuals)
        .as("The movies set into the repository were incorrect or not a defensive copy")
        .containsAll(this.testMovies)
        .isNotSameAs(this.testMovies);
  }

  @Test
  @DisplayName("Verifies the decade can't occur before the beginning of the 20th century")
  void testFindByDecade1() {

    // Arrange.
    final int testDecade = 1890;

    // Act and assert.
    Assertions.assertThatIllegalArgumentException()
        .as(
            "An IllegalArgumentException should have been thrown when the decade predates the 20th"
                + " century")
        .isThrownBy(() -> this.uut.findByDecade(testDecade));
  }

  @Test
  @DisplayName("Verifies the value must represent a decade")
  void testFindByDecade2() {

    // Arrange.
    final int testDecade = 2001;

    // Act and assert.
    Assertions.assertThatIllegalArgumentException()
        .as(
            "An IllegalArgumentPointerException should have been thrown when the value didn't"
                + " represent the beginning of a decade")
        .isThrownBy(() -> this.uut.findByDecade(testDecade));
  }

  @Test
  @DisplayName(
      "Verifies an empty result set is returned when no movies within the desired decaded are found")
  void testFindByDecade3() {

    // Arrange.
    final int testDecade = 1950;

    // Act.
    final Set<Movie> actuals = this.uut.findByDecade(testDecade);

    // Act and assert.
    Assertions.assertThat(actuals).as("No results should have been returned").isEmpty();
  }

  @Test
  @DisplayName("Verifies the correct result is returned for the specified test decade")
  void testFindByDecade4() {

    // Arrange.
    final int testDecade = 2000;
    final Movie[] expecteds = {this.testMovie2, this.testMovie3};

    // Act.
    final Set<Movie> actuals = this.uut.findByDecade(testDecade);

    // Act and assert.
    Assertions.assertThat(actuals)
        .as("No results should have been returned")
        .containsExactlyInAnyOrder(expecteds);
  }
}
