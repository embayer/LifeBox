package de.lifebox.LifeBox;

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
}
