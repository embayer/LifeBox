package de.lifebox.LifeBox;

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
	private final long mediaId;
	private final long typesId;
	private final String title;
	private final String description;
	private final long userDate;
	private final long creationDate;
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
