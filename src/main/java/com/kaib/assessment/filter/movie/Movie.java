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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Domain object that contains metadata about a movie.
 *
 * @author jkaib
 */
public final class Movie {

  /** The actors in the movie. */
  private String[] cast;

  /** The movie categories. */
  private String[] genres;

  /** The movie title. */
  private String title;

  /** The year the movie was released. */
  private int year;

  /** Instantiate a new movie. */
  public Movie() {
    super();
  }

  @Override
  public boolean equals(final Object obj) {

    if (obj == null) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }

    final Movie other = (Movie) obj;
    final var builder = new EqualsBuilder();
    return builder
        .append(this.cast, other.cast)
        .append(this.genres, other.genres)
        .append(this.title, other.title)
        .append(this.year, other.year)
        .isEquals();
  }

  /**
   * Gets the actors in the movie.
   *
   * @return the actors in the movie
   */
  public String[] getCast() {
    return this.cast.clone();
  }

  /**
   * Gets the movie categories.
   *
   * @return the movie categories
   */
  public String[] getGenres() {
    return this.genres.clone();
  }

  /**
   * Gets the movie title.
   *
   * @return the movie title
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * Gets the year the movie was released.
   *
   * @return the year the movie was released
   */
  public int getYear() {
    return this.year;
  }

  @Override
  public int hashCode() {

    final var hashCodeBuilder = new HashCodeBuilder(23, 13);
    return hashCodeBuilder
        .append(this.cast)
        .append(this.genres)
        .append(this.title)
        .append(this.year)
        .toHashCode();
  }

  /**
   * Sets the actors in the movie.
   *
   * @param newCast the new actors in the movie
   */
  public void setCast(final String[] newCast) {

    if (newCast == null) {
      this.cast = null;
    } else {
      this.cast = newCast.clone();
    }
  }

  /**
   * Sets the movie categories.
   *
   * @param newGenres the new movie categories
   */
  public void setGenres(final String[] newGenres) {

    if (newGenres == null) {
      this.genres = null;
    } else {
      this.genres = newGenres.clone();
    }
  }

  /**
   * Sets the movie title.
   *
   * @param newTitle the new movie title
   */
  public void setTitle(final String newTitle) {
    this.title = newTitle;
  }

  /**
   * Sets the year the movie was released.
   *
   * @param year the new year the movie was released
   */
  public void setYear(final int year) {
    this.year = year;
  }

  @Override
  public String toString() {

    return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
        .append("cast", this.cast)
        .append("genres", this.genres)
        .append("title", this.title)
        .append("year", this.year)
        .build();
  }
}
