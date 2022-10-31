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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Performs automated tests on the {@code DecadeUtils} class.
 *
 * @author jkaib
 */
final class DecadeUtilsTest {

  @ParameterizedTest
  @CsvSource({
    "1999,false,Should have been false because 1999 is not the beginning of a decade",
    "2000, true, Should have been true because 2000 is the beginning of a decade"
  })
  @DisplayName("Verifies whether a given year is correctly identified as the beginning of a decade")
  void testIsDecadeYear1(final int testYear, final boolean expected, final String assertionMsg) {

    // Act.
    final boolean actual = DecadeUtils.isDecadeYear(testYear);

    // Assert.
    Assertions.assertThat(actual).as(assertionMsg).isEqualTo(expected);
  }

  @ParameterizedTest
  @CsvSource({"2020, 2030", "2021, 2030"})
  @DisplayName("Verifies the beginning of the next decade is correctly returned")
  void testGetNextDecade1(final int testYear, final int expected) {

    // Act.
    final int actual = DecadeUtils.getNextDecade(testYear);

    // Assert
    Assertions.assertThat(actual).as("The next decade was incorrect").isEqualTo(expected);
  }

  @ParameterizedTest
  @CsvSource({
    "1899, false, Should have been flse because 1899 is before the beginning of the 20th century",
    "1900, true, Should have been true because 1900 is the beginning of a 20th century",
    "1901, true, Should have been true because 1901 after the beginning of a 20th century"
  })
  @DisplayName("Verifies the beginning of the next decade is correctly returned")
  void testIsNotBefore20thCentury1(
      final int testYear, final boolean expected, final String assertionMsg) {

    // Act.
    final boolean actual = DecadeUtils.isNotBefore20thCentury(testYear);

    // Assert
    Assertions.assertThat(actual).as(assertionMsg).isEqualTo(expected);
  }
}
