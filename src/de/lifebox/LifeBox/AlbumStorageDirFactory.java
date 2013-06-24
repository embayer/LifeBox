package de.lifebox.LifeBox;

import android.os.Environment;

import java.io.File;

/**
 * Factory Class to provide paths to store media files.
 * @author Markus Bayer
 * @version 0.1 19.06.2013
 */
public class AlbumStorageDirFactory
{
	private static final String LIFEBOX_DIR = "LifeBox";		// base folder
	private static final String PICTURES_DIR = "Images";        // image folder
	private static final String VIDEOS_DIR = "Videos";          // video folder

	/**
	 * Get the proper path for a specific MIME-Type and create a folder.
	 * @param mimeType the type of media to store
	 * @return File (folder) located at the proper path
	 */
	public File getAlbumStorageDir(String mimeType)
	{
		File storageDir;
		if(mimeType.equals("image"))
		{
			// path should be something like: emulated/0/storage/LifeBox/Images/
			storageDir = new File
					(
						Environment.getExternalStorageDirectory()
						+File.separator
						+ LIFEBOX_DIR
						+File.separator
						+ PICTURES_DIR
					);
		}
		else if(mimeType.equals("video"))
		{
			storageDir = new File
					(
						Environment.getExternalStorageDirectory()
						+File.separator
						+ LIFEBOX_DIR
						+File.separator
						+ VIDEOS_DIR
					);
		}
		else
		{
			storageDir = null;
		}

		return storageDir;
	}
}
