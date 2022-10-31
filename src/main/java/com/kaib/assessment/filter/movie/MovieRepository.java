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

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

/**
 * An instance of the {@code MovieRepository} class encapsulates search, and retrieval behavior for
 * the movies supported the by application.
 *
 * @author jkaib
 */
public class MovieRepository {

  /** The movies supported by the application. */
  private final Set<Movie> movies;

  /**
   * Instantiates a new movie repository using the movies supported by the application.
   *
   * @param theMovies the movies supported by the application
   */
  public MovieRepository(final Set<Movie> theMovies) {
    super();
    Validate.notEmpty(theMovies, "The respository requires at least one movie", (Object[]) null);
    this.movies = Collections.unmodifiableSet(theMovies);
  }

  /**
   * Returns the movies released during the specified decade. The decade must include the century
   * because movies have been released for more than a hundred years.
   *
   * <p>Uses validation that checks whether the specified decade is valid because the user may pass
   * in an invalid value and receive only a partial result set.
   *
   * @param decade the decade of interest
   * @return the movies released during the specified decade
   */
  public Set<Movie> findByDecade(final int decade) {

    DecadeValidator.validate(decade);

    return movies
        .stream()
        .filter(
            movie ->
                movie.getYear() >= decade && movie.getYear() < DecadeUtils.getNextDecade(decade))
        .collect(Collectors.toSet());
  }
}
