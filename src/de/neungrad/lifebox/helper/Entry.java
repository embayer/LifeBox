package de.neungrad.lifebox.helper;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class for database entries.
 * @version 0.1 30.07.13
 * @autor Markus Bayer
 */
public class Entry
{
	// mediatype public key
	private final long mediaId;
	// mediatype
	private final long typesId;
	// user specified title
	private final String title;
	// user specified description
	private final String description;
	// user specified date
	private final long userDate;
	// date of creatiion
	private final long creationDate;
	// date of the last edit
	private final long modificationDate;

	private String type = "";

	/** Constructor */
	public Entry(long mediaId, long typesId, String title, String description, long userDate, long creationDate, long modificationDate)
	{
		this.mediaId = mediaId;
		this.typesId = typesId;
		this.title = title;
		this.description = description;
		this.userDate = userDate;
		this.creationDate = creationDate;
		this.modificationDate = modificationDate;
	}

	public long getMediaId()
	{
		return mediaId;
	}

	public long getTypesId()
	{
		return typesId;
	}

	public String getTitle()
	{
		return title;
	}

	public String getDescription()
	{
		return description;
	}

	public long getUserDate()
	{
		return userDate;
	}

	public long getCreationDate()
	{
		return creationDate;
	}

	public long getModificationDate()
	{
		return modificationDate;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 * Converts the userDate timestamp and returns the date.
	 * @return (String) the date
	 */
	public String getDate()
	{
		Timestamp timestamp = new Timestamp(userDate);
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
		Timestamp timestamp = new Timestamp(userDate);
		Date date = new Date(timestamp.getTime());

		String timeString = new SimpleDateFormat("HH:mm").format(date);

		return timeString;
	}
}
