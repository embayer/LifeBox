package de.lifebox.LifeBox;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.*;

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
	public final static String TAG = "MetaFormActivity";
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

	// the extras brought by InputFragment
	private String fileUrl;
	private String mimeType;
	private String thumbnailUrl;
	private String creationDate;

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

	// the ui elements
	private EditText titleET;
	
	// the counters
	private TextView tagsCount;
	private TextView hashtagsCount;

	private ProgressBar progressBar;
	private WebView musicView;
	private WebView movieView;
	private ImageView imageView;

	private TextView uploadText;

	private ImageButton tagBtn;
	private ImageButton hashtagBtn;

	// the button listeners
	// time listener
	Button.OnClickListener mTimePickerListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			// call a picker
			DialogFragment newFragment = new TimePickerFragment();
			newFragment.show(getFragmentManager(), "timePicker");
		}
	};

	// date listener
	Button.OnClickListener mDatePickerListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			// call a picker
			DialogFragment newFragment = new DatePickerFragment();
			newFragment.show(getFragmentManager(), "datePicker");
		}
	};

	// tag listener
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

	// hashtag listener
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

	// save entry listener
	Button.OnClickListener mSaveListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			// try to save the user statements
			boolean userdataComplete = saveUserStatements();

			// all necessary data collected?
			if(userdataComplete == true)
			{
				// insert a file
				if( (mediaType.equals(Constants.TYPE_FILE)) && (uploadComplete == true) )
				{
					mDbHelper.insertFile(mimeType, mediaType, userTitle, userDescription, userTimestamp, fileUrl,
						thumbnailUrl, fileDriveId, fileDownloadUrl, thumbDriveId, thumbDownloadUrl, hashtagList, tagList);
					Log.d(TAG, "entry inserted: " + Constants.TYPE_FILE);
				}
				// insert a text
				else if(mediaType.equals(Constants.TYPE_TEXT))
				{
					mDbHelper.insertText(text, mediaType, userTitle, userDescription, userTimestamp, hashtagList, tagList);
					Log.d(TAG, "entry inserted: " + Constants.TYPE_TEXT);
				}
				// insert a movie
				else if(mediaType.equals(Constants.TYPE_MOVIE))
				{
					mDbHelper.insertMovie(movieTitle, movieDirector, movieDescription, movieGenre, movieTimestamp,
							movieThumbnailUrl, mediaType, userTitle, userDescription, userTimestamp, hashtagList, tagList);
					Log.d(TAG, "entry inserted:" + Constants.TYPE_MOVIE);
				}
				// insert a song
				else if(mediaType.equals(Constants.TYPE_MUSIC))
				{
					mDbHelper.insertMusic(musicArtist, musicAlbum, musicTimestamp, musicThumbnailUrl, musicTrack,
							musicGenre, mediaType, userTitle, userDescription, userTimestamp, hashtagList, tagList);
					Log.d(TAG, "entry inserted" + Constants.TYPE_MUSIC);
				}

				// pass to MainActivity
				Intent intent = new Intent(getBaseContext(), MainActivity.class);
				// clear the backstack navigation
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra(Constants.CALLER_EXTRA, Constants.CALLER_META_FORM_ACTIVITY);
				startActivity(intent);
			}
		}
	};

	/** Executed when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.metaform);

		// setup the filter
		IntentFilter mStatusIntentFilter = new IntentFilter(Constants.BROADCAST_ACTION_UPLOADRESPONSE);

		// set the filter's category to DEFAULT
		mStatusIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);

		mDbHelper = new DbHelper(getBaseContext());

		// instantiates a new ResponseReceiver
		mResponseReceiver = new ResponseReceiver();
		// Registers the ResponseReceiver and its intent filters
		LocalBroadcastManager.getInstance(this).registerReceiver(mResponseReceiver, mStatusIntentFilter);

		// initialize the variables
		uploadComplete = true; 						// only needed by files

		// Lists for tags and hashtags
		hashtagList = new ArrayList<String>();
		tagList = new ArrayList<String>();

		// init ui elements
		progressBar = (ProgressBar) findViewById(R.id.progress_bar_upload);
		progressBar.setVisibility(View.INVISIBLE);

		uploadText = (TextView) findViewById(R.id.header_upload);

		movieView = (WebView) findViewById(R.id.wv_movie);
		movieView.setVisibility(View.INVISIBLE);
		musicView = (WebView) findViewById(R.id.wv_music);
		musicView.setVisibility(View.INVISIBLE);
		imageView = (ImageView) findViewById(R.id.iv_thumbnail);

		// the current system time
		String time = new SimpleDateFormat(Constants.TIMEFORMAT).format(new Date());
		// the current date
		String date = new SimpleDateFormat(Constants.DATEFORMAT).format(new Date());

		// get the extras
		Intent intent = getIntent();

		mediaType = "";
		mimeType = "";
		if(intent.hasExtra(Constants.MEDIA_TYPE_EXTRA))
		{
			mediaType = intent.getStringExtra(Constants.MEDIA_TYPE_EXTRA);
			Log.d(TAG, "type: " + mediaType);
		}

		// determine what to do with the specific media
		// get the extras, upload the file if neccessary
		// mediatype file
		if(mediaType.equals(Constants.TYPE_FILE))
		{
			// show the upload process
			progressBar.setVisibility(View.VISIBLE);
			uploadText.setBackgroundResource(R.color.androidblue);

			// path to the local file
			fileUrl = intent.getStringExtra(Constants.FILE_URL_EXTRA);
			// MIME-Type of file
			mimeType = intent.getStringExtra(Constants.MIME_TYPE_EXTRA);
			// path to the thumbnail uri
			thumbnailUrl = intent.getStringExtra(Constants.THUMBNAIL_URL_EXTRA);
			// date of the filecreation
			creationDate = intent.getStringExtra(Constants.CREATION_DATE_EXTRA);

			// upoad the thumbnail
			uploadComplete = false;
			upload(thumbnailUrl, Constants.MIME_TYPE_IMAGE, true);
		}
		// mediatype text
		else if(mediaType.equals(Constants.TYPE_TEXT))
		{
			text = intent.getStringExtra(Constants.TEXT_EXTRA);

			// show preview
			imageView.setImageResource(R.drawable.button_text);
		}
		// mediatype movie
		else if(mediaType.equals(Constants.TYPE_MOVIE))
		{
			movieTitle = intent.getStringExtra(Constants.MOVIE_TITLE_EXTRA);
			movieDescription = intent.getStringExtra(Constants.MOVIE_DESCRIPTION_EXTRA);
			movieDirector = intent.getStringExtra(Constants.MOVIE_DIRECTOR_EXTRA);
			movieGenre = intent.getStringExtra(Constants.MOVIE_GENRE_EXTRA);
			movieReleaseDate = intent.getStringExtra(Constants.MOVIE_RELEASE_DATE_EXTRA);
			// generate the timestamp
			// getTime() returns milliseconds, time is 22:00 so + 2h in seconds = 7200
			movieTimestamp = stringToTimestamp(movieReleaseDate).getTime();
			movieThumbnailUrl = intent.getStringExtra(Constants.MOVIE_THUMBNAIL_URL_EXTRA);

			// show preview
			imageView.setVisibility(View.INVISIBLE);
			movieView.setVisibility(View.VISIBLE);
			movieView.loadUrl(movieThumbnailUrl);
		}
		// mediatype music
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

			// show preview
			imageView.setVisibility(View.INVISIBLE);
			musicView.setVisibility(View.VISIBLE);
			musicView.loadUrl(musicThumbnailUrl);
		}

		// unspecific ui elements
		// the counters
		tagsCount = (TextView) findViewById(R.id.textview_tagscount);
		hashtagsCount = (TextView) findViewById(R.id.textview_hashtagscount);

		titleET = (EditText) findViewById(R.id.in_title);

		// set the listeners to the buttons
		Button tpBtn = (Button) findViewById(R.id.timepicker);
		tpBtn.setOnClickListener(mTimePickerListener);
		tpBtn.setText(time);

		Button dpBtn = (Button) findViewById(R.id.datepicker);
		dpBtn.setOnClickListener(mDatePickerListener);
		dpBtn.setText(date);

		tagBtn = (ImageButton) findViewById(R.id.button_tags);
		tagBtn.setOnClickListener(mTagListener);

		hashtagBtn = (ImageButton) findViewById(R.id.button_hashtags);
		hashtagBtn.setOnClickListener(mHashtagListener);

		Button saveBtn = (Button) findViewById(R.id.save_meta_data);
		saveBtn.setOnClickListener(mSaveListener);

		// provide clear functionality by the "x" icon within the input field
		String value = "";    // pre-fill the input field

		// icon
		final Drawable x = getResources().getDrawable(R.drawable.x);
		// place it
		x.setBounds(0, 0, x.getIntrinsicWidth(), x.getIntrinsicHeight());
		titleET.setCompoundDrawables(null, null, value.equals("") ? null : x, null);

		titleET.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if (titleET.getCompoundDrawables()[2] == null)
				{
					return false;
				}
				if (event.getAction() != MotionEvent.ACTION_UP)
				{
					return false;
				}
				// when clicked
				if (event.getX() > titleET.getWidth() - titleET.getPaddingRight() - x.getIntrinsicWidth())
				{
					// clear text
					titleET.setText("");
					// remove icon
					titleET.setCompoundDrawables(null, null, null, null);
				}
				return false;
			}
		});

		// show icon when there is an input
		titleET.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				titleET.setCompoundDrawables(null, null, titleET.getText().toString().equals("") ? null : x, null);
			}

			// unneeded methods
			@Override
			public void afterTextChanged(Editable arg0)
			{
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
			}
		});
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
			// returns from collecting tags
			case Constants.ACTION_GATHER_TAGS:
				if(resultCode == Activity.RESULT_OK)
				{
					// get the extra
					tagList = data.getStringArrayListExtra(Constants.TAG_ARRAY_EXTRA);

					// set the counter
					if(!tagList.isEmpty())
					{
						tagsCount.setText(""+tagList.size());
						tagBtn.setBackgroundResource(R.drawable.button_tag);
					}
				}
				break;
			// returns from collecting hashtags
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
							hashtagBtn.setBackgroundResource(R.drawable.button_hashtag);
						}
					}
				}
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
	 * @return true if everything is ok. Otherwise false if there was a problem.
	 */
	private boolean saveUserStatements()
	{
		boolean success = uploadComplete;

		// necessary data
		userTitle = "";
		userDescription = "";
		userTimestamp = 0;

		// get the time
		Button timeBtn = (Button) findViewById(R.id.timepicker);
		String time = timeBtn.getText().toString();

		// get the date
		Button dateBtn = (Button) findViewById(R.id.datepicker);
		String date = dateBtn.getText().toString();

		// get the title
		userTitle = titleET.getText().toString();

		// title has to be set
		if(userTitle.equals(""))
		{
			// no title is set
			success = false;

			// inform the user
			Toast.makeText(getBaseContext(), "Insert a title to continue.", Toast.LENGTH_SHORT).show();
		}
		else
		{
			// title set but upload still in progress
			if(uploadComplete == false)
			{

				// inform the user
				Toast.makeText(getBaseContext(), "Upload in progress.", Toast.LENGTH_SHORT).show();
			}
		}

		// get the description
		EditText descriptionET = (EditText) findViewById(R.id.in_description);
		userDescription = descriptionET.getText().toString();

		// date and time string
		String dts = date + " " + time;

		SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_TIME);
		Date parsedDate = null;
		try
		{
			parsedDate = sdf.parse(dts);
		}
		catch (ParseException e)
		{
			success = false;
			Log.e(TAG, "parse date error" + e.getMessage());
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

		SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_TIME);

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
				imageView.setImageDrawable(Drawable.createFromPath(thumbnailUrl));

				// get the extras
				thumbDriveId = intent.getStringExtra(Constants.DRIVE_ID_EXTRA);
				thumbDownloadUrl = intent.getStringExtra(Constants.DOWNLOAD_URL_EXTRA);
			}
			// if the upload was the file itself (not just the thumbnail) -> done
			else
			{
				// get the extras
				fileDriveId = intent.getStringExtra(Constants.DRIVE_ID_EXTRA);
				fileDownloadUrl = intent.getStringExtra(Constants.DOWNLOAD_URL_EXTRA);

				// commit the upload by setting the complete flag
				uploadComplete = true;

				// hide the upload progress bar
				progressBar.setVisibility(View.INVISIBLE);
				uploadText.setBackgroundResource(R.color.gray);
			}
		}
	}

}