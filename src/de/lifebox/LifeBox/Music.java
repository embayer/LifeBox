package de.lifebox.LifeBox;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class for the media type music.
 * @version 0.1 11.07.13
 * @autor Markus Bayer
 */
public class Music
{
	// members of the mediatype movie
	private final String artist;
	private final String album;
	private final String releaseDate;
	private final String thumbnailUrl;
	private final String track;
	private final String musicGenre;

	/** Constructor. */
	public Music(String artist, String album, String releaseDate, String thumbnailUrl, String track, String musicGenre)
	{
		this.artist = artist;
		this.album = album;
		this.releaseDate = releaseDate;
		this.thumbnailUrl = thumbnailUrl;
		this.track = track;
		this.musicGenre = musicGenre;
	}

	public String getArtist()
	{
		return artist;
	}

	public String getAlbum()
	{
		return album;
	}

	public String getReleaseDate()
	{
		return releaseDate;
	}

	public String getThumbnailUrl()
	{
		return thumbnailUrl;
	}

	public String getTrack()
	{
		return track;
	}

	public String getMusicGenre()
	{
		return musicGenre;
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
