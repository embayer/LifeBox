package de.lifebox.LifeBox;

/**
 * Holds system-wide constants
 *
 * @version 0.1 24.06.13
 * @autor Markus Bayer
 */
public final class Constants
{
	// Defines a custom Intent action for the communication from UploadService to MetaFormActivity
	public static final String BROADCAST_ACTION = "de.lifebox.LifeBox.BROADCAST";

	// codes for the different MIME-Types
	public static final String MIME_TYPE_IMAGE = "image/jpeg";
	public static final String MIME_TYPE_VIDEO = "video/mp4";

	/** Constructor is empty to prevent instantiating the Constants class. */
	public Constants() {}
}
