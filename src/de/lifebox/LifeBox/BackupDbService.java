package de.lifebox.LifeBox;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.*;

import java.io.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * IntentService to execute a full database backup,
 * which includes offline backup to sd-card and online backup.
 * @version 0.1 09.08.13
 * @autor Markus Bayer
 */
public class BackupDbService extends UploadService
{
	private final static String TAG = "BackupDbService";

	/** Drive service object */
	private static Drive service;
	/** Google account authentification object */
	private GoogleAccountCredential credential;

	/** Creates an IntentService.  Invoked the subclass's constructor. */
	public BackupDbService()
	{
		super();
	}

	/** Invoke the worker thread that runs independently from other application logic. */
	@Override
	protected void onHandleIntent(Intent intent)
	{
		showToast("Begin database backup");

		// create a timestamp
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

		// offline backup to sd-card
		String path = offlineBackup(getBaseContext(), Constants.DATABASE_NAME, timeStamp);

		// online backup to google drive
		onlineBackup(path);
	}

	private String offlineBackup(Context c, String databaseName, String timeStamp)
	{
		// the result
		File db = null;
		String databasePath = c.getDatabasePath(databaseName).getPath();
		File f = new File(databasePath);
		OutputStream myOutput = null;
		InputStream myInput = null;
		Log.d(TAG, " testing db path " + databasePath);
		Log.d(TAG, " testing db exist " + f.exists());

		if (f.exists())
		{
			try
			{
				AlbumStorageDirFactory mAlbumStorageDirFactory = new AlbumStorageDirFactory();
				File directory = mAlbumStorageDirFactory.getDbStorageDir();

				if (!directory.exists())
				{
					directory.mkdir();
				}

				db = new File(
						directory.getAbsolutePath() + File.separator + Constants.APP_NAME + "_" + timeStamp + ".db");
				myOutput = new FileOutputStream(db);
				myInput = new FileInputStream(databasePath);

				byte[] buffer = new byte[1024];
				int length;
				while ((length = myInput.read(buffer)) > 0)
				{
					myOutput.write(buffer, 0, length);
				}

				myOutput.flush();
			}
			catch (Exception e)
			{
				Log.e(TAG, e.getMessage());
			}
			finally
			{
				try
				{
					if (myOutput != null)
					{
						myOutput.close();
						myOutput = null;
					}
					if (myInput != null)
					{
						myInput.close();
						myInput = null;
					}
				}
				catch (Exception e)
				{
					Log.e(TAG, e.getMessage());
				}
			}
		}

		return db.getAbsolutePath();
	}

	private void onlineBackup(String path)
	{
		// retrieve the users accountname from the sharedpreferences
		SharedPreferences settings = getSharedPreferences("preferences", 0);
		String accountName = settings.getString("accountName", "");

		credential = GoogleAccountCredential.usingOAuth2(this, DriveScopes.DRIVE);

		if(null != accountName)
		{
			// pass the accountname to the authentification object
			credential.setSelectedAccountName(accountName);
			// initialize the Google Drive service
			service = super.getDriveService(credential);
			try
			{
				// assemble the file
				// File's binary content
				java.io.File fileContent = new java.io.File(path);
				FileContent mediaContent = null;

				// File's metadata
				com.google.api.services.drive.model.File body = null;

				mediaContent = new FileContent("application/octet-stream", fileContent);

				body = new com.google.api.services.drive.model.File();
				body.setMimeType("application/octet-stream");

				body.setTitle(fileContent.getName());
				body.setDescription("LifeBoxFile");
//				body.setParents(Arrays.asList(new ParentReference().setId("appdata")));

				// upload
				com.google.api.services.drive.model.File file = service.files().insert(body, mediaContent).execute();
				Log.d(TAG, file.toString());

				// succeeded?
				if (null != file)
				{
					// notify the user via a toast
					showToast("Database backup complete.");
				}
			}
			catch(UserRecoverableAuthIOException e)
			{
				showToast("Authentication-Error. Please try again.");
			}
			catch(IOException e)
			{
				Log.e("IO Error", e.getMessage());
				showToast("IO-Error. Please try again.");
			}
		}
		else
		{
			//TODO Fehlerbehandlung
			showToast("Log in Error. Please try again.");
//			startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
		}
	}
}
