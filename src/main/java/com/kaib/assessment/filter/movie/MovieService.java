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

import org.apache.commons.lang3.Validate;

/**
 * Instances of the {@Code MovieService} class filter movies that were released in a given
 * decade from a larger set of movies. that spans multiple decades. Since movies have been around
 * for more than a hundred years, the century is required, e.g, "1910-1919" and "2010-2019".
 *
 * @author jkaib
 */
public class MovieService {

  /** The sourc of the movie information. */
  private final MovieRepository movieRepository;

  /**
   * Instantiate a new decade-based movie filter using the specified movie repository.
   *
   * @param theMovieRepository contains all the movies supported by the application.
   */
  public MovieService(final MovieRepository theMovieRepository) {

    super();
    Validate.notNull(theMovieRepository, "The movie repository can't be null", (Object[]) null);
    this.movieRepository = theMovieRepository;
  }

  /**
   * Returns the movies released during the specified decade. The decade must include the century
   * because movies have been released for more than a hundred years. The specified decade must
   * represent the beginning of a decade, e.g. "2010", can be no earlier than 1900 and no later than
   * the decade in which the current year falls.
   *
   * @param decade the decade of interest
   * @return the movies released during the specified decade.
   */
  public Set<Movie> filter(final int decade) {

    DecadeValidator.validate(decade);

    return this.movieRepository.findByDecade(decade);
  }
}
