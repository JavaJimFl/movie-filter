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
import org.junit.jupiter.api.Test;

/**
 * Performs automated tests on the {@code DecadeValidator} class.
 *
 * @author jkaib
 */
final class DecadeValidatorTest {

  /** Instantiates a new decade validator test. */
  public DecadeValidatorTest() {
    super();
  }

  @Test
  @DisplayName("Verifies the decade can't occur before the beginning of the 20th century")
  void testValidate1() {

    // Arrange.
    final int testDecade = 1890;

    // Act and assert.
    Assertions.assertThatIllegalArgumentException()
        .as(
            "An IllegalArgumentException should have been thrown when the decade predates the 20th"
                + " century")
        .isThrownBy(() -> DecadeValidator.validate(testDecade))
        .withMessage("The decade can't be before the beginning of the 20th century");
  }

  @Test
  @DisplayName("Verifies the value must represent a decade")
  void testValidate2() {

    // Arrange.
    final int testDecade = 2001;

    // Act and assert.
    Assertions.assertThatIllegalArgumentException()
        .as(
            "An IllegalArgumentPointerException should have been thrown when the value didn't"
                + " represent the beginning of a decade")
        .isThrownBy(() -> DecadeValidator.validate(testDecade))
        .withMessage("The specified year doesn't represent the beginning of a decade");
  }

  @Test
  @DisplayName(
      "Verifies no exception is thrown when the year value is a decade after the beginning of the"
          + " 20th century")
  void testValidate4() {

    // Arrange.
    final int testDecade = 2000;

    // Act and assert.
    Assertions.assertThatNoException()
        .as(
            "No exception should have been thrown when the year value is a decade after the"
                + " beginning of the 20th century")
        .isThrownBy(() -> DecadeValidator.validate(testDecade));
  }
}
