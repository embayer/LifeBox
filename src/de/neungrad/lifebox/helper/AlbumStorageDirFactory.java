package de.neungrad.lifebox.helper;

import android.os.Environment;

import java.io.File;

/**
 * Factory Class to provide paths to store media files.
 * @author Markus Bayer
 * @version 0.1 19.06.2013
 */
public class AlbumStorageDirFactory
{
	// constant folders
	private static final String LIFEBOX_DIR = "LifeBox";		// base folder
	private static final String IMAGES_DIR = "Images";        	// image folder
	private static final String VIDEOS_DIR = "Videos";          // video folder
	private static final String THUMBNAILS_DIR = "Thumbnails";
	private static final String DATABASE_DIR = ".Database";

	/**
	 * Get the proper path for a specific MIME-Type and create a folder.
	 * @param mimeType the type of media to store
	 * @return File (folder) located at the proper path
	 */
	public File getAlbumStorageDir(String mimeType)
	{
		File storageDir;
		if(mimeType.equals(Constants.MIME_TYPE_IMAGE))
		{
			// path should be something like: emulated/0/storage/LifeBox/Images/
			storageDir = new File
					(
						Environment.getExternalStorageDirectory()
						+ File.separator
						+ LIFEBOX_DIR
						+ File.separator
						+ IMAGES_DIR
					);
		}
		else if(mimeType.equals(Constants.MIME_TYPE_VIDEO))
		{
			storageDir = new File
					(
						Environment.getExternalStorageDirectory()
						+ File.separator
						+ LIFEBOX_DIR
						+ File.separator
						+ VIDEOS_DIR
					);
		}
		else if(mimeType.equals(Constants.MIME_TYPE_IMAGE_THUMB))
		{
			storageDir = new File
					(
						Environment.getExternalStorageDirectory()
						+File.separator
						+ LIFEBOX_DIR
						+ File.separator
						+ IMAGES_DIR
						+ File.separator
						+ THUMBNAILS_DIR
					);
		}
		else if(mimeType.equals(Constants.MIME_TYPE_VIDEO_THUMB))
		{
			storageDir = new File
					(
							Environment.getExternalStorageDirectory()
							+ File.separator
							+ LIFEBOX_DIR
							+ File.separator
							+ VIDEOS_DIR
							+ File.separator
							+ THUMBNAILS_DIR
					);
		}
		else
		{
			storageDir = null;
		}

		return storageDir;
	}

	/**
	 * Get the proper path for a database backup on sd-card and creates a folder.
	 * @return (File) the folder.
	 */
	public File getDbStorageDir()
	{
		File storageDir = new File
				(
						Environment.getExternalStorageDirectory()
						+ File.separator
						+ LIFEBOX_DIR
						+ File.separator
						+ DATABASE_DIR
				);

		return storageDir;
	}
}
