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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.jparams.verifier.tostring.ToStringVerifier;
import com.jparams.verifier.tostring.preset.Presets;

final class MovieTest {

  /** The first test cast member name. */
  private String testCastMember1;

  /** The second test cast member name. */
  private String testCastMember2;

  /** The test cast member names. */
  private String[] testCastMembers;

  /** The first test film category. */
  private String testGenre1;

  /** The second test film category. */
  private String testGenre2;

  /** The test film categories. */
  private String[] testGenres;

  /** The first film title. */
  private String testTitle;

  /** The test film release year. */
  private int testReleaseYear;

  /** The unit under test. */
  private Movie uut;

  /** A second unit under test, used for {@code equals} and {@code hashCode} tests. */
  private Movie otherUut;

  /** Sets the test fixture up prior to each test. */
  @BeforeEach
  void setUp() {

    this.testCastMember1 = "testCastMember1";
    this.testCastMember2 = "testCastMember2";
    this.testCastMembers = new String[] {this.testCastMember1, this.testCastMember2};

    this.testGenre1 = "testGenre1";
    this.testGenre2 = "testGenre2";
    this.testGenres = new String[] {this.testGenre1, this.testGenre2};

    this.testReleaseYear = 2000;

    this.testTitle = "testTitle";

    this.uut = buildUut();
    this.otherUut = buildUut();
  }

  /**
   * Builds a fully populated UUT with default test values.
   *
   * @return a fully populated UUT with default test values
   */
  private Movie buildUut() {

    final Movie movie = new Movie();
    movie.setCast(this.testCastMembers);
    movie.setGenres(this.testGenres);
    movie.setTitle(this.testTitle);
    movie.setYear(this.testReleaseYear);

    return movie;
  }

  @Test
  @DisplayName("Verifies the cast getter and setter are wired correctly")
  void testGetSetCast1() {

    // Arrange.
    final String[] expecteds = this.testCastMembers;

    // Act.
    final String[] actuals = this.uut.getCast();

    // Assert.
    Assertions.assertThat(actuals)
        .as("The cast getter and setter weren't wired correctly")
        .containsExactly(expecteds);
  }

  @Test
  @DisplayName("Verifies the genre getter and setter are wired correctly")
  void testGetSetGenre1() {

    // Arrange.
    final String[] expecteds = this.testGenres;

    // Act.
    final String[] actuals = this.uut.getGenres();

    // Assert.
    Assertions.assertThat(actuals)
        .as("The genre getter and setter weren't wired correctly")
        .containsExactly(expecteds);
  }

  @Test
  @DisplayName("Verifies the title getter and setter are wired correctly")
  void testGetSetTitle1() {

    // Arrange.
    final String expected = this.testTitle;

    // Act.
    final String actual = this.uut.getTitle();

    // Assert.
    Assertions.assertThat(actual)
        .as("The title getter and setter weren't wired correctly")
        .isSameAs(expected);
  }

  @Test
  @DisplayName("Verifies the release year getter and setter are wired correctly")
  void testGetSetTitle2() {

    // Arrange.
    final int expected = this.testReleaseYear;

    // Act.
    final int actual = this.uut.getYear();

    // Assert.
    Assertions.assertThat(actual)
        .as("The release year getter and setter weren't wired correctly")
        .isEqualTo(expected);
  }

  @Test
  @DisplayName("Verifies an instance of the UUT isn't equal to null (nullity)")
  void testEquals1() {

    // Act.
    final boolean actual = this.uut.equals(null);

    // Assert.
    Assertions.assertThat(actual).as("Failed equals nullity check").isFalse();
  }

  @Test
  @DisplayName(
      "Verifies that, for any non-null reference value x, x.equals(x) should return true"
          + " (reflexivity)")
  void testEquals2() {

    // Act.
    final boolean actual = this.uut.equals(this.uut);

    // Assert.
    Assertions.assertThat(actual).as("Failed equals reflexivity check").isTrue();
  }

  @Test
  @DisplayName(
      "Verifies that, for any non-null reference values x and y, x.equals(y) should return"
          + " true if and only if y.equals(x) returns true. (symmetry)")
  void testEquals3() {

    // Act.
    final boolean actual = this.uut.equals(this.otherUut) && this.otherUut.equals(this.uut);

    // Assert.
    Assertions.assertThat(actual).as("Failed equals symmetry check").isTrue();
  }

  @Test
  @DisplayName(
      "Verifies that, for any non-null reference values x and y, multiple invocations of"
          + " x.equals(y) consistently return true or consistently return false, provided no "
          + "information used in equals comparisons on the objects is modified. (consistency)")
  void testEquals4() {

    // Act.
    final boolean actual =
        this.uut.equals(this.otherUut)
            && this.uut.equals(this.otherUut)
            && this.uut.equals(this.otherUut);

    // Assert.
    Assertions.assertThat(actual).as("Failed equals consistency check").isTrue();
  }

  @Test
  @DisplayName(
      "Verifies that, for any non-null reference values x and y, multiple invocations of"
          + " x.equals(y) consistently return true or consistently return false, provided no "
          + "information used in equals comparisons on the objects is modified. (consistency)")
  void testEquals5() {

    // Arrange.
    final Movie thirdUut = buildUut();

    // Act.
    final boolean actual =
        this.uut.equals(this.otherUut)
            && this.otherUut.equals(thirdUut)
            && thirdUut.equals(this.uut);

    // Assert.
    Assertions.assertThat(actual).as("Failed equals transitivity check").isTrue();
  }

  @Test
  @DisplayName("Verifies that a Movie instance isn't equal to an instance of another class")
  void testEquals6() {

    // Arrange.
    final String testObject = "A String";

    // Act.
    final boolean actual = this.uut.equals(testObject);

    // Assert.
    Assertions.assertThat(actual).as("Failed equals class check").isFalse();
  }

  @Test
  @DisplayName("Verifies that two Movie instances aren't equal when their casts differ")
  void testEquals7() {

    // Arrange.
    this.uut.setCast(null);

    // Act.
    final boolean actual = this.uut.equals(this.otherUut);

    // Assert.
    Assertions.assertThat(actual)
        .as("Two Movie instances shouldn't have been equal when  their casts were different")
        .isFalse();
  }

  @Test
  @DisplayName("Verifies that two Movie instances aren't equal when their genres differ")
  void testEquals8() {

    // Arrange.
    this.uut.setGenres(null);

    // Act.
    final boolean actual = this.uut.equals(this.otherUut);

    // Assert.
    Assertions.assertThat(actual)
        .as("Two Movie instances shouldn't have been equal when  their genres were different")
        .isFalse();
  }

  @Test
  @DisplayName("Verifies that two Movie instances aren't equal when  their titles differ")
  void testEquals9() {

    // Arrange.
    this.uut.setTitle(null);

    // Act.
    final boolean actual = this.uut.equals(this.otherUut);

    // Assert.
    Assertions.assertThat(actual)
        .as("Two Movie instances shouldn't have been equal when  their titles were different")
        .isFalse();
  }

  @Test
  @DisplayName("Verifies that two Movie instances aren't equal when their release years differ")
  void testEquals10() {

    // Arrange.
    this.uut.setYear(0);

    // Act.
    final boolean actual = this.uut.equals(this.otherUut);

    // Assert.
    Assertions.assertThat(actual)
        .as("Two Movie instances shouldn't have been equal when their release years were different")
        .isFalse();
  }

  @Test
  @DisplayName("Verifies that two equal Movie instances return the same hash code")
  void testHashCode1() {

    // Act.
    final boolean actual = this.uut.hashCode() == this.otherUut.hashCode();

    // Assert.
    Assertions.assertThat(actual)
        .as("Two equal Movie instances should return had the same hash code")
        .isTrue();
  }

  @Test
  @DisplayName(
      "Verifies that two Movie instances return different hash codes when their casts differ")
  void testHashCode2() {

    // Arrange.
    this.uut.setCast(null);

    // Act.
    final boolean actual = this.uut.hashCode() == this.otherUut.hashCode();

    // Assert.
    Assertions.assertThat(actual)
        .as(
            "Two Movie instances shouldn't have returned the same hash code when their casts were"
                + " different")
        .isFalse();
  }

  @Test
  @DisplayName(
      "Verifies that two Movie instances return different hash codes when their genres differ")
  void testHashCode3() {

    // Arrange.
    this.uut.setGenres(null);

    // Act.
    final boolean actual = this.uut.hashCode() == this.otherUut.hashCode();

    // Assert.
    Assertions.assertThat(actual)
        .as(
            "Two Movie instances shouldn't have returned the same hash code when their genres were"
                + " different")
        .isFalse();
  }

  @Test
  @DisplayName(
      "Verifies that two Movie instances have different hash codes when their titles differ")
  void testHashCode4() {

    // Arrange.
    this.uut.setTitle(null);

    // Act.
    final boolean actual = this.uut.hashCode() == this.otherUut.hashCode();

    // Assert.
    Assertions.assertThat(actual)
        .as(
            "Two Movie instances shouldn't have returned the same hash code when their titles were"
                + " different")
        .isFalse();
  }

  @Test
  @DisplayName(
      "Verifies that two Movie instances should  have returned different hash codes when their"
          + " release years differ")
  void testHashCode5() {

    // Arrange.
    this.uut.setYear(0);

    // Act.
    final boolean actual = this.uut.hashCode() == this.otherUut.hashCode();

    // Assert.
    Assertions.assertThat(actual)
        .as(
            "Two Movie instances shouldn't return the same hash code when their release years"
                + " were different")
        .isFalse();
  }

  @Test
  @DisplayName("Verifies the toString output")
  void testToString() {

    // Act and assert.
    ToStringVerifier
     .forClass(Movie.class)
     .withPreset(Presets.APACHE_TO_STRING_BUILDER_DEFAULT_STYLE)
     .verify();
  }
}
