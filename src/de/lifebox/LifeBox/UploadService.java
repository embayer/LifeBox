package de.lifebox.LifeBox;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
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
	//TODO change the upload directory to AppData
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

	// metadata of the uploaded file
	private String driveId;
	private String downloadUrl;

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
		showToast("Begin upload");

		// get the extras
		// MIME-Type of file
		String mimeType = intent.getStringExtra(Constants.MIME_TYPE_EXTRA);
		// Path to the local file that should be uploaded
		Uri fileUri = Uri.parse(intent.getStringExtra(Constants.FILE_URL_EXTRA));
		// thumbnail
		isThumbnail = intent.getBooleanExtra(Constants.IS_THUMB_EXTRA, false);

		// retrieve the users accountname from the sharedpreferences
		SharedPreferences settings = getSharedPreferences("preferences", 0);
		String accountName = settings.getString("accountName", "");                    //TODO accountname default

		credential = GoogleAccountCredential.usingOAuth2(this, DriveScopes.DRIVE); 		//TODO OAuth2 ersetzen && APPDATA verwenden

		if(null != accountName)                                                        	//TODO accountname prüfen
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
					mediaContent = new FileContent("image/jpeg", fileContent);
					body = new File();
					body.setMimeType("image/jpeg");
				}
				else if(mimeType.equals(Constants.MIME_TYPE_VIDEO))
				{
					mediaContent = new FileContent("video/mp4", fileContent);
					body = new File();
					body.setMimeType("video/mp4");
				}

				body.setTitle(fileContent.getName());
				body.setDescription("LifeBoxFile");
//				body.setParents(Arrays.asList(new ParentReference().setId("appdata")));

				// upload
				File file = service.files().insert(body, mediaContent).execute();
				Log.e("uploadservice", file.toString());

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

					Log.e("json", file.toString());
					Log.e("downloadUrl", file.get("downloadUrl").toString());
					sendDriveMetaData(file);
				}
			}
			catch(UserRecoverableAuthIOException e)
			{
				showToast("Authentication-Error. Please try again.");
			}
			catch(IOException e)
			{
				Log.e("ioerror", e.toString());
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

	/**
	 * Getter for Google Drive service objects.
	 * @param credential the login information
	 * @return Drive instance
	 */
	private Drive getDriveService(GoogleAccountCredential credential)
	{
		return new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
				.build();
	}

	/**
	 * Sends the fileId retrieved from the drive service back to the activity
	 * which started this service
	 * @param file (File) the meta data of file stored on drive
	 */
	private void sendDriveMetaData(File file)
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

		// broadcasts the Intent to receivers in this app
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
			int duration = Toast.LENGTH_LONG;

			@Override
			public void run()
			{
				Toast.makeText(context, text, duration).show();
			}
		});
	}
}