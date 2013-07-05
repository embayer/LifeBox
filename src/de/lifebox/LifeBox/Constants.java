package de.lifebox.LifeBox;

/**
 * Holds system-wide constants
 *
 * @version 0.1 24.06.13
 * @autor Markus Bayer
 */
public final class Constants
{
	// defines a custom Intent action for the communication from UploadService to MetaFormActivity
	public static final String BROADCAST_ACTION = "de.lifebox.LifeBox.BROADCAST";

	// codes for the different MIME-Types
	public static final String MIME_TYPE_IMAGE = "image/jpeg";
	public static final String MIME_TYPE_VIDEO = "video/mp4";

	// codes for Thumbnails
	public static final String MIME_TYPE_IMAGE_THUMB = "image/thumbnail";
	public static final String MIME_TYPE_VIDEO_THUMB = "video/thumbnail";

	// codes for the extras
	public static final String HASHTAG_EXTRA = "hashtagExtra";

	/** Constructor is empty to prevent instantiating the Constants class. */
	public Constants() {}
}
