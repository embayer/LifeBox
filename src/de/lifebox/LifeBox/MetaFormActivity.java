package de.lifebox.LifeBox;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * Activity representing the form for getting the media's meta information
 * @autor Markus Bayer
 * @version 0.1 18.06.13
 */
public class MetaFormActivity extends Activity
{
	// will be inserted into the database
	private String mimeType;
	private String fileUri;
	private String timeStamp;

	private ResponseReceiver mResponseReceiver;
	private TimePicker timepicker;

	/** Executed when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.metaform);

		// the filter
		IntentFilter mStatusIntentFilter = new IntentFilter(Constants.BROADCAST_ACTION);
		// Adds a data filter for the HTTP scheme
//		mStatusIntentFilter.addDataScheme("http");

		// Sets the filter's category to DEFAULT
		mStatusIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);

		// Instantiates a new ResponseReceiver
		mResponseReceiver = new ResponseReceiver();
		// Registers the ResponseReceiver and its intent filters
		LocalBroadcastManager.getInstance(this).registerReceiver(mResponseReceiver, mStatusIntentFilter);

		// get the extras
		// MIME-Type of file
		Intent intent = getIntent();
		mimeType = intent.getStringExtra("mimeType");
		// path to the local file that was uploaded
		fileUri = intent.getStringExtra("fileUri");
		// timestamp of the filecreation
		timeStamp = intent.getStringExtra("timeStamp");

		Intent callUploadServiceIntent = new Intent(this, UploadService.class);
		callUploadServiceIntent.putExtra("mimeType", mimeType);
		callUploadServiceIntent.putExtra("fileUri", fileUri);
		startService(callUploadServiceIntent);
	}

	/**
	 * Performs final cleanup before an activity is destroyed
	 */
	@Override
	protected void onDestroy() {
		// unregister since the activity is about to be closed.
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mResponseReceiver);
		super.onDestroy();
	}

	/**
	 * Broadcast receiver for receiving the Google Drive fileId from the UploadService
	 * @author Markus Bayer
	 * @version 0.1 24.06.13
	 */
	public class ResponseReceiver extends BroadcastReceiver
	{
		/** Called when the BroadcastReceiver gets an Intent it's registered to receive */
		public void onReceive(Context context, Intent intent)
		{
			// get the extras
			String driveMetaData = intent.getStringExtra("driveMetaData");

			TextView mTextView = (TextView) findViewById(R.id.description);
			mTextView.setText(driveMetaData);
		}
	}

}