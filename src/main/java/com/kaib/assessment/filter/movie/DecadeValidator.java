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

import org.apache.commons.lang3.Validate;

/**
 * Reusable validator that verifies a given year represents the beginning of a decade.
 *
 * @author jkaib
 */
public final class DecadeValidator {

  /** Private constructor prevents instantiation. */
  private DecadeValidator() {
    super();
  }

  /**
   * Verifies the specified year is the beginning of a decade. It can't precede the invention of the
   * movie, which is 1900.
   *
   * @param year the year to evaluate
   * @throws IllegalArgumentException if the specified year specified year doesn't represent the
   *     beginning of a decade
   */
  public static void validate(final int year) {
    Validate.isTrue(
        DecadeUtils.isNotBefore20thCentury(year),
        "The decade can't be before the beginning of the 20th century",
        (Object[]) null);
    Validate.isTrue(
        DecadeUtils.isDecadeYear(year),
        "The specified year doesn't represent the beginning of a decade",
        (Object[]) null);
  }
}
