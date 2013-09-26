package de.neungrad.lifebox.helper;

/**
 * Class for the entries on TimelineFragment.
 * @version 0.1 31.07.13
 * @autor Markus Bayer
 */
public class TimelineEntry
{
	private final String type;
	private final String filetype;
	private final String entryId;
	private final String thumbnail;
	private final String date;
	private final String time;
	private final String title;
	private final String firstText;
	private final String secondText;

	public TimelineEntry(String type, String filetype, String entryId, String thumbnail, String date, String time, String title, String firstText, String secondText)
	{
		this.type = type;
		this.filetype = filetype;
		this.entryId = entryId;
		this.thumbnail = thumbnail;
		this.date = date;
		this.time = time;
		this.title = title;
		this.firstText = firstText;
		this.secondText = secondText;
	}

	public String getType()
	{
		return type;
	}

	public String getFiletype()
	{
		return filetype;
	}

	public String getEntryId()
	{
		return entryId;
	}

	public String getThumbnail()
	{
		return thumbnail;
	}

	public String getDate()
	{
		return date;
	}

	public String getTime()
	{
		return time;
	}

	public String getTitle()
	{
		return title;
	}

	public String getFirstText()
	{
		return firstText;
	}

	public String getSecondText()
	{
		return secondText;
	}

}
