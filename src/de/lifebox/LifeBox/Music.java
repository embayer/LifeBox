package de.lifebox.LifeBox;

/**
 * Class for the media type music.
 * @version 0.1 11.07.13
 * @autor Markus Bayer
 */
public class Music
{
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
}
