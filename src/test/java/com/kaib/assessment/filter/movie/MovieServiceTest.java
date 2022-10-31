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

import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import mockit.Expectations;
import mockit.Mocked;

/**
 * Performs automated tests on the {@code MovieService} class.
 *
 * @author jkaib
 */
final class MovieServiceTest {

  /** The mock that simulates the behavior of the movie repository. */
  @Mocked private MovieRepository mockMovieRepository;

  /** The unit under test. */
  private MovieService uut;

  /** The test movie; */
  private Movie testMovie;

  /**
   * Sets up the test fixture prior to each test.
   *
   * @throws Exception if something unexpected happens during the test
   */
  @BeforeEach
  void setUp() throws Exception {
    this.uut = new MovieService(this.mockMovieRepository);
    this.testMovie = new Movie();
  }

  @Test
  @DisplayName("Verifies a movie repository is required by the decade-based movie filter")
  void testMovieService1() {

    // Act and assert.
    Assertions.assertThatNullPointerException()
        .as("A NullPointerException should have been thrown when the movie respoitory was null")
        .isThrownBy(() -> new MovieService(null));
  }


  @Test
  @DisplayName("Verifies the movie repository is invoked")
  void testFilter2() {

    // Arrange.
    final int testDecade = 1900;
    final Movie expected = this.testMovie;
    new Expectations() {
      {
        mockMovieRepository.findByDecade(testDecade);
        result = expected;
      }
    };

    // Act.
    final Set<Movie> actuals = this.uut.filter(testDecade);

    // Assert.
    Assertions.assertThat(actuals)
        .as("A movie should have been returned by the UUT")
        .containsExactly(testMovie);
  }
  
  @Test
  @DisplayName("Verifies the value must represent a decade")
  void testFilter3() {

    // Arrange.
    final int testDecade = 2001;

    // Act and assert.
    Assertions.assertThatIllegalArgumentException()
        .as(
            "An IllegalArgumentPointerException should have been thrown when the value didn't"
                + " represent the beginning of a decade")
        .isThrownBy(() -> this.uut.filter(testDecade));
  }
}
