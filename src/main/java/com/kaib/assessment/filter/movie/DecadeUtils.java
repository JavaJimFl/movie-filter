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

/**
 * Utility class that provide
 *
 * @author jkaib
 */
public final class DecadeUtils {

  /** The beginning of the twentieth century */
  private static final int START_20TH_CENTURY = 1900;
  
  /** The number of years in a decade. */
  private static final int ONE_DECADE = 10;

  /** The divisor used to determine if a year represents a decade. */
  private static final int DECADE_MODULO_DIVISOR = ONE_DECADE;

  /** Private constructor prevents instantiation. */
  private DecadeUtils() {
    super();
  }

  /**
   * Indicates whether the specified year is not before the beginning of the 20th century.
   *
   * @param year the year to evaluate
   * @return {@code true} if the specified year is not before the beginning of the 20th century,
   *     {@code false} otherwise
   */
  public static boolean isNotBefore20thCentury(int year) {
    return START_20TH_CENTURY - year <= 0;
  }

  /**
   * Indicates whether the specified year is the beginning of a decade. For example, "2020" is the
   * beginning of the third decade of the 21st century.
   *
   * @param year the year to evaluate
   * @return {@code true} if the specified year is the beginning of a decade, {@code false}
   *     otherwise
   */
  public static boolean isDecadeYear(final int year) {

    return year % DECADE_MODULO_DIVISOR == 0;
  }

  /**
   * Gets the year representing the decade after the specified year. For example, if the year is
   * 2022, the next decade would be 2030.
   *
   * @return the decade after the specified year
   */
  public static int getNextDecade(final int year) {

    final int currentYear = year;
    int result = currentYear;
    if (isDecadeYear(result)) {
      result = currentYear + ONE_DECADE;
    } else {
      while (!isDecadeYear(result)) {
        result++;
      }
    }

    return result;
  }
}
