package de.lifebox.LifeBox;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.io.IOException;

/**
 * IntentService for uploading media onto Google Drive as asynchronous requests.
 * @author Markus Bayer
 * @version 0.1 17.06.2013
 */
public class UploadService extends IntentService
{
	public static final String TAG = "UploadService";

	/**Google Drive Scope for the AppData folder */
	public static String APP_DATA_SCOPE = "https://www.googleapis.com/auth/drive.appdata";
	/** Drive service object */
	private static Drive service;
	/** Google account authentification object */
	private GoogleAccountCredential credential;
	/** Handle to pass toasts from a service*/
	private Handler mHandler;

	// thumbnail flag (extra from MetaFormActivity)
	private boolean isThumbnail;

	/** Constructor */
	public UploadService()
	{
		super("UploadService");
	}

	/** Called when the service is first created. */
	@Override
	public void onCreate()
	{
		super.onCreate();
		mHandler = new Handler();
	}

	/** Invoke the worker thread that runs independently from other application logic. */
	@Override
	protected void onHandleIntent(Intent intent)
	{
		upload(intent);
	}

	/**
	 * Upload a file to Google Drive
	 * @param intent (Intent) the file parameters
	 */
	private void upload(Intent intent)
	{
		// inform the user
		showToast("Begin upload");

		// get the extras
		// MIME-Type of file
		String mimeType = intent.getStringExtra(Constants.MIME_TYPE_EXTRA);
		// Path to the local file that should be uploaded
		Uri fileUri = Uri.parse(intent.getStringExtra(Constants.FILE_URL_EXTRA));
		// thumbnail
		isThumbnail = intent.getBooleanExtra(Constants.IS_THUMB_EXTRA, false);

		// retrieve the user accountname from the sharedpreferences
		SharedPreferences settings = getSharedPreferences("preferences", 0);
		String accountName = settings.getString("accountName", "");

		// login with OAuth2.0, full rights on Google Drive
		credential = GoogleAccountCredential.usingOAuth2(this, DriveScopes.DRIVE);

		if(!accountName.equals(""))
		{
			// pass the accountname to the authentification object
			credential.setSelectedAccountName(accountName);
			// initialize the Google Drive service
			service = getDriveService(credential);

			try
			{
				// assemble the file
				// File's binary content
				java.io.File fileContent = new java.io.File(fileUri.getPath());
				FileContent mediaContent = null;

				// File's metadata
				File body = null;

				if(mimeType.equals(Constants.MIME_TYPE_IMAGE))
				{
					mediaContent = new FileContent(Constants.MIME_TYPE_IMAGE, fileContent);
					body = new File();
					body.setMimeType(Constants.MIME_TYPE_IMAGE);
				}
				else if(mimeType.equals(Constants.MIME_TYPE_VIDEO))
				{
					mediaContent = new FileContent(Constants.MIME_TYPE_VIDEO, fileContent);
					body = new File();
					body.setMimeType(Constants.MIME_TYPE_VIDEO);
				}

				body.setTitle(fileContent.getName());
				body.setDescription("LifeBoxFile");

				// upload the file
				File file = service.files().insert(body, mediaContent).execute();
				Log.d(TAG, "file uploaded: " + file.toString());

				// succeeded?
				if (null != file)
				{
					// notify the user via a toast
					if(mimeType.equals(Constants.MIME_TYPE_IMAGE))
					{
						showToast("Photo uploaded: " + file.getTitle());
					}
					else if(mimeType.equals(Constants.MIME_TYPE_VIDEO))
					{
						showToast("Video uploaded: " + file.getTitle());
					}

					Log.d(TAG, "Google Drive JSON: " + file.toString());

					// send the meta information back to MetaFormActivity
					sendMeta(file);
				}
			}
			// inform the user about the error and try it again
			catch(UserRecoverableAuthIOException e)
			{
				// inform user and try again
				Log.e(TAG, "Authentication-Error: " + e.getMessage());
				showToast("Authentication-Error. Trying to restart.");
				upload(intent);
			}
			catch(IOException e)
			{
				// inform user and try again
				Log.e(TAG, "IO Error: " + e.getMessage());
				showToast("IO-Error. Trying to restart.");
				upload(intent);
			}
		}
		else
		{
			// inform user
			Log.e(TAG, "Login Error");
			showToast("Login Error. Please login again.");
//			startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
		}
	}

	/**
	 * Getter for Google Drive service objects.
	 * @param credential the login information
	 * @return Drive instance
	 */
	protected Drive getDriveService(GoogleAccountCredential credential)
	{
		return new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
				.build();
	}

	/**
	 * Sends the fileId retrieved from the drive service back to the activity
	 * which started this service
	 * @param file (File) the meta data of file stored on drive
	 */
	private void sendMeta(File file)
	{
		Intent localIntent = new Intent(Constants.BROADCAST_ACTION_UPLOADRESPONSE);

		// set the extras
		localIntent.putExtra(Constants.DRIVE_ID_EXTRA, file.get("id").toString());
		localIntent.putExtra(Constants.DOWNLOAD_URL_EXTRA, file.get("downloadUrl").toString());

		// check if the uploaded file was a thumbnail
		if(isThumbnail == true)
		{
			localIntent.putExtra(Constants.IS_THUMB_EXTRA, true);
		}
		else
		{
			localIntent.putExtra(Constants.IS_THUMB_EXTRA, false);
		}

		localIntent.addCategory(Intent.CATEGORY_DEFAULT);

		// broadcasts the Intent to receivers in MetaFormActivity
		LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
	}

	/**
	 * Show toasts at the main thread which is responsible for io.
	 * @param text String to display as toast
	 */
	public void showToast(final String text)
	{
		mHandler.post(new Runnable()
		{
			Context context = UploadService.this;
			int duration = Toast.LENGTH_SHORT;

			@Override
			public void run()
			{
				Toast toast = Toast.makeText(context, text, duration);
				toast.setGravity(Gravity.RIGHT, 0, 380);
				toast.show();
			}
		});
	}
}