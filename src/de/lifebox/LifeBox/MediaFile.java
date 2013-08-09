package de.lifebox.LifeBox;

/**
 * Wrapper class for the media type file
 * @version 0.1 06.08.13
 * @autor Markus Bayer
 */
public class MediaFile
{
	private final String filetype;

	private final String offlinePathFile;
	private final String offlinePathThumbnail;

	private final String driveIdFile;
	private final String driveIdThumbnail;
	private final String urlFile;
	private final String urlThumbnail;

	/** Constructor. */
	public MediaFile(String filetype, String offlinePathFile, String offlinePathThumbnail, String driveIdFile, String driveIdThumbnail, String urlFile, String urlThumbnail)
	{
		this.filetype = filetype;
		this.offlinePathFile = offlinePathFile;
		this.offlinePathThumbnail = offlinePathThumbnail;
		this.driveIdFile = driveIdFile;
		this.driveIdThumbnail = driveIdThumbnail;
		this.urlFile = urlFile;
		this.urlThumbnail = urlThumbnail;
	}

	public String getFiletype()
	{

		return filetype;
	}

	public String getOfflinePathFile()
	{
		return offlinePathFile;
	}

	public String getOfflinePathThumbnail()
	{
		return offlinePathThumbnail;
	}

	public String getDriveIdFile()
	{
		return driveIdFile;
	}

	public String getDriveIdThumbnail()
	{
		return driveIdThumbnail;
	}

	public String getUrlFile()
	{
		return urlFile;
	}

	public String getUrlThumbnail()
	{
		return urlThumbnail;
	}
}
