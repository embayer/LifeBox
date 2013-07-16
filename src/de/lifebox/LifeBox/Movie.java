package de.lifebox.LifeBox;

/**
 * Class for the media type movie.
 * @version 0.1 11.07.13

 * @autor Markus Bayer
 */
public class Movie
{
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
}
