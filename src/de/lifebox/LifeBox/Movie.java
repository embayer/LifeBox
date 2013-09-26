package de.lifebox.LifeBox;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class for the media type movie.
 * @version 0.1 11.07.13

 * @autor Markus Bayer
 */
public class Movie
{
	// members of the mediatype movie
	private final String title;
	private final String description;
	private final String director;
	private final String movieGenre;
	private final String releaseDate;
	private final String thumbnailUrl;

	/** Constructor. */
	public Movie(String title, String description, String director, String movieGenre, String releaseDate, String thumbnailUrl)
	{
		this.title = title;
		this.description = description;
		this.director = director;
		this.movieGenre = movieGenre;
		this.releaseDate = releaseDate;
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getTitle()
	{
		return title;
	}

	public String getDescription()
	{
		return description;
	}

	public String getDirector()
	{
		return director;
	}

	public String getMovieGenre()
	{
		return movieGenre;
	}

	public String getReleaseDate()
	{
		return releaseDate;
	}

	public String getThumbnailUrl()
	{
		return thumbnailUrl;
	}

	/**
	 * Converts the userDate timestamp and returns the date.
	 * @return (String) the date
	 */
	public String getDate()
	{
		Timestamp timestamp = new Timestamp(Long.parseLong(releaseDate));
		Date date = new Date(timestamp.getTime());

		String dateString = new SimpleDateFormat("yyyy-MM-dd").format(date);

		return dateString;
	}

	/**
	 * Converts the userDate timestamp and returns the time.
	 * @return (String) the time
	 */
	public String getTime()
	{
		Timestamp timestamp = new Timestamp(Long.parseLong(releaseDate));
		Date date = new Date(timestamp.getTime());

		String timeString = new SimpleDateFormat("HH:mm").format(date);

		return timeString;
	}
}
