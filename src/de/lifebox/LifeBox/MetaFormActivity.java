package de.lifebox.LifeBox;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Activity representing the form for getting the media's meta information
 * @autor Markus Bayer
 * @version 0.1 18.06.13
 */
public class MetaFormActivity extends Activity
{
	// will be inserted into the database
	// (
	// the extras brought by SelectTypeFragment
	private String mimeType;
	private String fileUri;
	private String thumbnailUri;
	private String timeStamp;

	// the extras brought by TagsActivity
	private boolean tagStateHobby = false;		// playing cards
	// )

	// the extra brought by HashTagsActivity
	private ArrayList<String> mHashtags;

	// codes for onActivityResult
	private static final int ACTION_GATHER_TAGS = 1;
	private static final int ACTION_GATHER_HASHTAGS = 2;

	private ResponseReceiver mResponseReceiver;

	Button.OnClickListener mTimePickerListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			DialogFragment newFragment = new TimePickerFragment();
			newFragment.show(getFragmentManager(), "timePicker");
		}
	};

	Button.OnClickListener mDatePickerListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			DialogFragment newFragment = new DatePickerFragment();
			newFragment.show(getFragmentManager(), "datePicker");
		}
	};

	Button.OnClickListener mTagListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent(getBaseContext(), TagsActivity.class);

			intent.putExtra("tagHobby", tagStateHobby);

			startActivityForResult(intent, ACTION_GATHER_TAGS);
		}
	};

	Button.OnClickListener mHashTagListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent(getBaseContext(), HashTagsActivity.class);

			intent.putStringArrayListExtra(Constants.HASHTAG_EXTRA, mHashtags);

			startActivityForResult(intent, ACTION_GATHER_HASHTAGS);
		}
	};

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

		mHashtags = new ArrayList<String>();

		// get the extras
		// MIME-Type of file
		Intent intent = getIntent();
		mimeType = intent.getStringExtra("mimeType");
		// path to the local file that was uploaded
		fileUri = intent.getStringExtra("fileUri");
		// path to the thumbnail uri
		thumbnailUri = intent.getStringExtra("thumbnailUri");
		// timestamp of the filecreation
		timeStamp = intent.getStringExtra("timeStamp");

		Intent callUploadServiceIntent = new Intent(this, UploadService.class);
		callUploadServiceIntent.putExtra("mimeType", mimeType);
		callUploadServiceIntent.putExtra("fileUri", fileUri);
		// TODO enable
		startService(callUploadServiceIntent);

		// the current system time
		String time = new SimpleDateFormat("HH:mm").format(new Date());
		// the current date
		String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

		// set the listeners to the buttons
		Button tpBtn = (Button) findViewById(R.id.timepicker);
		tpBtn.setOnClickListener(mTimePickerListener);
		tpBtn.setText(time);

		Button dpBtn = (Button) findViewById(R.id.datepicker);
		dpBtn.setOnClickListener(mDatePickerListener);
		dpBtn.setText(date);

		Button tagBtn = (Button) findViewById(R.id.button_tags);
		tagBtn.setOnClickListener(mTagListener);

		Button hashTagBtn = (Button) findViewById(R.id.button_hashtags);
		hashTagBtn.setOnClickListener(mHashTagListener);
	}

	/**
	 * Called when the activity you launched returns
	 * with the requestCode, the resultCode, and any additional data from it.
	 */
	@Override
	public void onActivityResult(final int requestCode, final int resultCode, final Intent data)
	{
		// decide by request code what to do
		switch(requestCode)
		{
			case ACTION_GATHER_TAGS:
				if(resultCode == Activity.RESULT_OK)
				{
					tagStateHobby = data.getBooleanExtra("tagHobby", false);
				}
				//TODO Fehlerbehandlung
				break;
			case ACTION_GATHER_HASHTAGS:
				if(resultCode == Activity.RESULT_OK)
				{
					if(data.hasExtra(Constants.HASHTAG_EXTRA))
					{
						mHashtags = data.getStringArrayListExtra(Constants.HASHTAG_EXTRA);
					}
				}
				//TODO Fehlerbehandlung
				break;
			default:
				break;
		}
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

			ImageView mImageView = (ImageView) findViewById(R.id.iv_thumbnail);
			mImageView.setImageDrawable(Drawable.createFromPath(thumbnailUri));
		}
	}

}