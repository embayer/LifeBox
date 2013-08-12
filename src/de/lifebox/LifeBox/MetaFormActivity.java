package de.lifebox.LifeBox;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.*;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Timestamp;
import java.text.ParseException;
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
	// instance of my DbHelper class
	DbHelper mDbHelper;
	// will be inserted into the database
	// (
	// user statements
	private String userTitle;
	private String userDescription;
	private long userTimestamp;
	// the extra brought by all intents
	private String mediaType;

	// the extras brought by SelectTypeFragment
	private String fileUrl;
	private String mimeType;
	private String thumbnailUrl;
	private String creationDate;
	private String timeStamp;

	// the extras brought by UploadService
	private String thumbDriveId;
	private String thumbDownloadUrl;
	private String fileDriveId;
	private String fileDownloadUrl;

	// the extra brought by TextFormActivity
	private String text;

	// the extras brought by SearchMovieActivity
	private String movieTitle;
	private String movieDescription;
	private String movieDirector;
	private String movieGenre;
	private String movieReleaseDate;
	private long movieTimestamp;
	private String movieThumbnailUrl;

	// the extras brought by SearchMusicActivity
	private String musicArtist;
	private String musicAlbum;
	private String musicReleaseDate;
	private long musicTimestamp;
	private String musicThumbnailUrl;
	private String musicTrack;
	private String musicGenre;

	// the extras brought by TagsActivity
	private ArrayList<String> tagList;

	// the extra brought by HashtagsActivity
	private ArrayList<String> hashtagList;
	// )

	private boolean uploadComplete;

	private ResponseReceiver mResponseReceiver;

	// the counters
	private TextView tagsCount;
	private TextView hashtagsCount;

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

			intent.putStringArrayListExtra(Constants.TAG_ARRAY_EXTRA, tagList);

			startActivityForResult(intent, Constants.ACTION_GATHER_TAGS);
		}
	};

	Button.OnClickListener mHashtagListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent(getBaseContext(), HashtagsActivity.class);

			intent.putStringArrayListExtra(Constants.HASHTAG_ARRAY_EXTRA, hashtagList);

			startActivityForResult(intent, Constants.ACTION_GATHER_HASHTAGS);
		}
	};

	Button.OnClickListener mSaveListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			boolean userdataComplete = saveUserStatements();

			if(userdataComplete == true)
			{
				if( (mediaType.equals(Constants.TYPE_FILE)) && (uploadComplete == true) )
				{
					mDbHelper.insertFile(mimeType, mediaType, userTitle, userDescription, userTimestamp, fileUrl,
						thumbnailUrl, fileDriveId, fileDownloadUrl, thumbDriveId, thumbDownloadUrl, hashtagList, tagList);
					Log.d("entry inserted", Constants.TYPE_FILE);
				}
				else if(mediaType.equals(Constants.TYPE_TEXT))
				{
					mDbHelper.insertText(text, mediaType, userTitle, userDescription, userTimestamp, hashtagList, tagList);
					Log.d("entry inserted", Constants.TYPE_TEXT);
				}
				else if(mediaType.equals(Constants.TYPE_MOVIE))
				{
					mDbHelper.insertMovie(movieTitle, movieDirector, movieDescription, movieGenre, movieTimestamp,
							movieThumbnailUrl, mediaType, userTitle, userDescription, userTimestamp, hashtagList, tagList);
					Log.d("entry inserted", Constants.TYPE_MOVIE);
				}
				else if(mediaType.equals(Constants.TYPE_MUSIC))
				{
					mDbHelper.insertMusic(musicArtist, musicAlbum, musicTimestamp, musicThumbnailUrl, musicTrack,
							musicGenre, mediaType, userTitle, userDescription, userTimestamp, hashtagList, tagList);
					Log.d("entry inserted", Constants.TYPE_MUSIC);
				}

				// pass to MainActivity
				Intent intent = new Intent(getBaseContext(), MainActivity.class);
				intent.putExtra(Constants.CALLER_EXTRA, Constants.CALLER_META_FORM_ACTIVITY);
				startActivity(intent);
			};
		}
	};

	/** Executed when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.metaform);

		// the filter
		IntentFilter mStatusIntentFilter = new IntentFilter(Constants.BROADCAST_ACTION_UPLOADRESPONSE);
		// Adds a data filter for the HTTP scheme
//		mStatusIntentFilter.addDataScheme("http");

		// Sets the filter's category to DEFAULT
		mStatusIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);

		mDbHelper = new DbHelper(getBaseContext());

		// Instantiates a new ResponseReceiver
		mResponseReceiver = new ResponseReceiver();
		// Registers the ResponseReceiver and its intent filters
		LocalBroadcastManager.getInstance(this).registerReceiver(mResponseReceiver, mStatusIntentFilter);

		// initialize the variables
		uploadComplete = false;
		hashtagList = new ArrayList<String>();
		tagList = new ArrayList<String>();

		// the current system time
		// TODO init with the creationdate
		String time = new SimpleDateFormat("HH:mm").format(new Date());
		// the current date
		String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

		// get the extras
		Intent intent = getIntent();

		mediaType = "";
		mimeType = "";
		if(intent.hasExtra(Constants.MEDIA_TYPE_EXTRA))
		{
			mediaType = intent.getStringExtra(Constants.MEDIA_TYPE_EXTRA);
			Log.d("type", mediaType);
		}

		// determine what to do with the specific media
		// get the extras, upload the file if neccessary
		if(mediaType.equals(Constants.TYPE_FILE))
		{
			// path to the local file
			fileUrl = intent.getStringExtra(Constants.FILE_URL_EXTRA);
			// MIME-Type of file
			mimeType = intent.getStringExtra(Constants.MIME_TYPE_EXTRA);
			// path to the thumbnail uri
			thumbnailUrl = intent.getStringExtra(Constants.THUMBNAIL_URL_EXTRA);
			// date of the filecreation
			creationDate = intent.getStringExtra(Constants.CREATION_DATE_EXTRA);

			// upoad the thumbnail
			upload(thumbnailUrl, Constants.MIME_TYPE_IMAGE, true);
		}
		else if(mediaType.equals(Constants.TYPE_TEXT))
		{
			text = intent.getStringExtra(Constants.TEXT_EXTRA);
		}
		else if(mediaType.equals(Constants.TYPE_MOVIE))
		{
			movieTitle = intent.getStringExtra(Constants.MOVIE_TITLE_EXTRA);
			movieDescription = intent.getStringExtra(Constants.MOVIE_DESCRIPTION_EXTRA);
			movieDirector = intent.getStringExtra(Constants.MOVIE_DIRECTOR_EXTRA);
			movieGenre = intent.getStringExtra(Constants.MOVIE_GENRE_EXTRA);
			movieReleaseDate = intent.getStringExtra(Constants.MOVIE_RELEASE_DATE_EXTRA);
			// generate the timestamp
			movieTimestamp = stringToTimestamp(movieReleaseDate).getTime();
			movieThumbnailUrl = intent.getStringExtra(Constants.MOVIE_THUMBNAIL_URL_EXTRA);
		}
		else if(mediaType.equals(Constants.TYPE_MUSIC))
		{
			musicArtist = intent.getStringExtra(Constants.MUSIC_ARTIST_EXTRA);
			musicAlbum = intent.getStringExtra(Constants.MUSIC_ALBUM_EXTRA);
			musicReleaseDate = intent.getStringExtra(Constants.MUSIC_REALEASE_DATE_EXTRA);
			// generate the timestamp
			musicTimestamp = stringToTimestamp(musicReleaseDate).getTime();
			musicThumbnailUrl = intent.getStringExtra(Constants.MUSIC_THUMBNAIL_URL_EXTRA);
			musicTrack = intent.getStringExtra(Constants.MUSIC_TRACK_EXTRA);
			musicGenre = intent.getStringExtra(Constants.MUSIC_GENRE_EXTRA);
		}

		// the counters
		tagsCount = (TextView) findViewById(R.id.textview_tagscount);
		hashtagsCount = (TextView) findViewById(R.id.textview_hashtagscount);


		// set the listeners to the buttons
		Button tpBtn = (Button) findViewById(R.id.timepicker);
		tpBtn.setOnClickListener(mTimePickerListener);
		tpBtn.setText(time);

		Button dpBtn = (Button) findViewById(R.id.datepicker);
		dpBtn.setOnClickListener(mDatePickerListener);
		dpBtn.setText(date);

		Button tagBtn = (Button) findViewById(R.id.button_tags);
		tagBtn.setOnClickListener(mTagListener);

		Button hashtagBtn = (Button) findViewById(R.id.button_hashtags);
		hashtagBtn.setOnClickListener(mHashtagListener);

		Button saveBtn = (Button) findViewById(R.id.save_meta_data);
		saveBtn.setOnClickListener(mSaveListener);

//		Button locationBtn = (Button) findViewById(R.id.button_location);
//		locationBtn.setOnClickListener(mLocationListener);
	}

	/**
	 * Called when the launched activity returns
	 * with the requestCode, the resultCode, and any additional data from it.
	 */
	@Override
	public void onActivityResult(final int requestCode, final int resultCode, final Intent data)
	{
		// decide by request code what to do
		switch(requestCode)
		{
			case Constants.ACTION_GATHER_TAGS:
				if(resultCode == Activity.RESULT_OK)
				{
					// get the extra
					tagList = data.getStringArrayListExtra(Constants.TAG_ARRAY_EXTRA);

					// set the counter
					if(!tagList.isEmpty())
					{
						tagsCount.setText(""+tagList.size());
					}
				}
				//TODO Fehlerbehandlung
				break;
			case Constants.ACTION_GATHER_HASHTAGS:
				if(resultCode == Activity.RESULT_OK)
				{
					if(data.hasExtra(Constants.HASHTAG_ARRAY_EXTRA))
					{
						// get the extras
						hashtagList = data.getStringArrayListExtra(Constants.HASHTAG_ARRAY_EXTRA);

						// set the counter
						if(!hashtagList.isEmpty())
						{
							hashtagsCount.setText(""+hashtagList.size());
						}
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
	 * Upload files to google drive.
	 * @param fileUrl (String) the URL to the file which should be uploaded.
	 * @param mimeType (String) the MIME-Type of the file which should be uploaded.
	 * @param isThumb (boolean) if the file is a thumbnail or not.
	 */
	private void upload(String fileUrl, String mimeType, boolean isThumb)
	{
		Intent callUploadServiceIntent = new Intent(this, UploadService.class);
		callUploadServiceIntent.putExtra(Constants.FILE_URL_EXTRA, fileUrl);
		callUploadServiceIntent.putExtra(Constants.MIME_TYPE_EXTRA, mimeType);
		callUploadServiceIntent.putExtra(Constants.IS_THUMB_EXTRA, isThumb);

		startService(callUploadServiceIntent);
	}

	/**
	 * Saves the user statements.
	 * @return true if everything is ok. Otherwise false if there was a problem (needed input field empty...)
	 */
	private boolean saveUserStatements()
	{
		boolean success = true;

		userTitle = "";
		userDescription = "";
		userTimestamp = 0;
		String time = "";
		String date = "";

		// get the title
		EditText titleET = (EditText) findViewById(R.id.in_title);
		userTitle = titleET.getText().toString();

		// title has to be set
		if( ( userTitle.trim().equals("")) || (null == userTitle) )
		{
			success = false;
		}

		// get the description
		EditText descriptionET = (EditText) findViewById(R.id.in_description);
		userDescription = descriptionET.getText().toString();

		// get the time
		Button timeBtn = (Button) findViewById(R.id.timepicker);
		time = timeBtn.getText().toString();

		// get the date
		Button dateBtn = (Button) findViewById(R.id.datepicker);
		date = dateBtn.getText().toString();

		// date and time string
		String dts = date + " " + time;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date parsedDate = null;
		try
		{
			parsedDate = sdf.parse(dts);
		}
		catch (ParseException e)
		{
			success = false;
			Log.e("parse date error", e.getMessage());
		}
		Timestamp timestamp = new Timestamp(parsedDate.getTime());
		userTimestamp = timestamp.getTime();

		return success;
	}

	/**
	 * Parses a String and tries to get the timestamp from.
	 * @param string (String) String in the format: 'yyyy-MM-dd'T'HH:mm:ssZ'
	 * @return (Timestamp) of the parsed String
	 */
	private Timestamp stringToTimestamp(String string)
	{
		// yyyy-MM-dd'T'HH:mm:ss:SSS'Z' -> yyyy-MM-dd HH:mm
		string = string.substring(0, 16);
		string = string.replace('T', ' ');

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		Date parsedDate = null;
		try
		{
			parsedDate = sdf.parse(string);
		}
		catch (ParseException e)
		{
			Log.e("parse date error", e.getMessage());
		}
		Timestamp timestamp = new Timestamp(parsedDate.getTime());

		return timestamp;
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
			// if the uploaded file was a thumbnail
			if(intent.getBooleanExtra(Constants.IS_THUMB_EXTRA, false) == true)
			{
				// upload the real file
				upload(fileUrl, mimeType, false);

				// show the thumbnail
				ImageView mImageView = (ImageView) findViewById(R.id.iv_thumbnail);
				mImageView.setImageDrawable(Drawable.createFromPath(thumbnailUrl));
				//todo show in the beginning

				// get the extras
				thumbDriveId = intent.getStringExtra(Constants.DRIVE_ID_EXTRA);
				thumbDownloadUrl = intent.getStringExtra(Constants.DOWNLOAD_URL_EXTRA);
			}
			else
			{
				// get the extras
				fileDriveId = intent.getStringExtra(Constants.DRIVE_ID_EXTRA);
				fileDownloadUrl = intent.getStringExtra(Constants.DOWNLOAD_URL_EXTRA);

				// commit the upload by setting the complete flag
				uploadComplete = true;
			}

			//todo zwischen thumb und file unterscheiden
			// get the extras
			String driveMetaData = intent.getStringExtra("driveMetaData");

			TextView mTextView = (TextView) findViewById(R.id.in_description);
			mTextView.setText(driveMetaData);
		}
	}

}