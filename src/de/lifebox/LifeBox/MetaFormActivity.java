package de.lifebox.LifeBox;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.*;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
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

			intent.putStringArrayListExtra(Constants.TAG_ARRAY_EXTRA, tagList);

			startActivityForResult(intent, ACTION_GATHER_TAGS);
		}
	};

	Button.OnClickListener mHashtagListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent(getBaseContext(), HashtagsActivity.class);

			intent.putStringArrayListExtra(Constants.HASHTAG_EXTRA, hashtagList);

			startActivityForResult(intent, ACTION_GATHER_HASHTAGS);
		}
	};

	Button.OnClickListener mLocationListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			mDbHelper = new DbHelper(getBaseContext());
			SQLiteDatabase db = mDbHelper.getReadableDatabase();
			Log.d("DB Path", db.getPath());
			// /data/data/de.lifebox.LifeBox/databases/LifeBox.db
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
					insertFile();
					Log.d("entry inserted", Constants.TYPE_FILE);
				}
				else if(mediaType.equals(Constants.TYPE_TEXT))
				{
					insertText();
					Log.d("entry inserted", Constants.TYPE_TEXT);
				}
				else if(mediaType.equals(Constants.TYPE_MOVIE))
				{
					insertMovie();
					Log.d("entry inserted", Constants.TYPE_MOVIE);
				}
				else if(mediaType.equals(Constants.TYPE_MUSIC))
				{
					insertMusic();
					Log.d("entry inserted", Constants.TYPE_MUSIC);
				}

				Intent intent = new Intent(getBaseContext(), MainActivity.class);
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

		Button locationBtn = (Button) findViewById(R.id.button_location);
		locationBtn.setOnClickListener(mLocationListener);
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
					tagList = data.getStringArrayListExtra(Constants.TAG_ARRAY_EXTRA);
				}
				//TODO Fehlerbehandlung
				break;
			case ACTION_GATHER_HASHTAGS:
				if(resultCode == Activity.RESULT_OK)
				{
					if(data.hasExtra(Constants.HASHTAG_EXTRA))
					{
						hashtagList = data.getStringArrayListExtra(Constants.HASHTAG_EXTRA);
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
	 * Inserts a entry of the mediatype file.
	 */
	private void insertFile()
	{
		mDbHelper = new DbHelper(getBaseContext());
		// get the data repository in read mode
		// get the data repository in write mode
		SQLiteDatabase dbIn = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();

		// get the filetypes_id for files ------------------------------------------------------------------------------
		long filetypesId = getDbKey
				(
						LifeBoxContract.Filetypes.TABLE_NAME,
						LifeBoxContract.Filetypes.COLUMN_NAME_FILETYPE,
						LifeBoxContract.Filetypes._ID,
						mimeType
				);

		// insert into files -------------------------------------------------------------------------------------------
		long filesId = regularDbInsert
				(
						LifeBoxContract.Files.TABLE_NAME,
						false,
						new KeyValuePair(LifeBoxContract.Files.COLUMN_NAME_FILETYPES_ID, filetypesId)
				);

		// get the types_id for entries --------------------------------------------------------------------------------
		long typesId = getTypesId(mediaType);

		// insert into entries -----------------------------------------------------------------------------------------
		long entriesId = regularDbInsert
				(
						LifeBoxContract.Entries.TABLE_NAME,
						false,
						new KeyValuePair(LifeBoxContract.Entries.COLUMN_NAME_MEDIA_ID, filesId),
						new KeyValuePair(LifeBoxContract.Entries.COLUMN_NAME_TYPES_ID, typesId),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_TITLE, userTitle),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_DESCRIPTION, userDescription),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_USER_DATE, userTimestamp),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_CREATION_DATE, userTimestamp),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_MODIFICATION_DATE, userTimestamp)
				);

		// insert (file) into offline_files ----------------------------------------------------------------------------
		long offlineFilesId = regularDbInsert
				(
						LifeBoxContract.OfflineFiles.TABLE_NAME,
						false,
						new KeyValuePair(LifeBoxContract.OfflineFiles.COLUMN_NAME_PATH, fileUrl),
						new KeyValuePair(LifeBoxContract.OfflineFiles.COLUMN_NAME_FILETYPES_ID, filetypesId)
						);

		// insert (thumbnail) into offline_files -----------------------------------------------------------------------

		values.put(LifeBoxContract.OfflineFiles.COLUMN_NAME_PATH, thumbnailUrl);
		values.put(LifeBoxContract.OfflineFiles.COLUMN_NAME_FILETYPES_ID, getThumbtype(mimeType));

		long offlineFilesTmbId = dbIn.insert(LifeBoxContract.OfflineFiles.TABLE_NAME, null, values);

		values.clear();

		// insert (file) into online_files -----------------------------------------------------------------------------
		long onlineFilesId = regularDbInsert
				(
						LifeBoxContract.OnlineFiles.TABLE_NAME,
						false,
						new KeyValuePair(LifeBoxContract.OnlineFiles.COLUMN_NAME_DRIVEID, fileDriveId),
						new KeyValuePair(LifeBoxContract.OnlineFiles.COLUMN_NAME_URL, fileDownloadUrl),
						new KeyValuePair(LifeBoxContract.OnlineFiles.COLUMN_NAME_FILETYPES_ID, filetypesId)
				);

		// insert (thumbnail) into online_files ------------------------------------------------------------------------

		values.put(LifeBoxContract.OnlineFiles.COLUMN_NAME_DRIVEID, thumbDriveId);
		values.put(LifeBoxContract.OnlineFiles.COLUMN_NAME_URL, thumbDownloadUrl);
		values.put(LifeBoxContract.OnlineFiles.COLUMN_NAME_FILETYPES_ID, getThumbtype(mimeType));

		long onlineFilesTmbId = dbIn.insert(LifeBoxContract.OnlineFiles.TABLE_NAME, null, values);

		values.clear();


		// insert (file) into file_offline_files -----------------------------------------------------------------------
		long fileOfflineFilesId = regularDbInsert
				(
						LifeBoxContract.FileOfflineFiles.TABLE_NAME,
						false,
						new KeyValuePair(LifeBoxContract.FileOfflineFiles.COLUMN_NAME_FILES_ID, filesId),
						new KeyValuePair(LifeBoxContract.FileOfflineFiles.COLUMN_NAME_OFFLINE_FILES_ID, offlineFilesId)
				);

		// insert (thumbnail) into file_offline_files ------------------------------------------------------------------
		long fileOfflineFilesTmbId = regularDbInsert
				(
						LifeBoxContract.FileOfflineFiles.TABLE_NAME,
						false,
						new KeyValuePair(LifeBoxContract.FileOfflineFiles.COLUMN_NAME_FILES_ID, filesId),
						new KeyValuePair(LifeBoxContract.FileOfflineFiles.COLUMN_NAME_OFFLINE_FILES_ID, offlineFilesTmbId)
				);

		// insert (file) into file_online_files ------------------------------------------------------------------------
		long fileOnlineFilesId = regularDbInsert
				(
						LifeBoxContract.FileOnlineFiles.TABLE_NAME,
						false,
						new KeyValuePair(LifeBoxContract.FileOnlineFiles.COLUMN_NAME_FILES_ID, filesId),
						new KeyValuePair(LifeBoxContract.FileOnlineFiles.COLUMN_NAME_ONLINE_FILES_ID, onlineFilesId)
				);

		// insert (thumbnail) into file_online_files -------------------------------------------------------------------
		long fileOnlineFilesTmbId = regularDbInsert
				(
						LifeBoxContract.FileOnlineFiles.TABLE_NAME,
						false,
						new KeyValuePair(LifeBoxContract.FileOnlineFiles.COLUMN_NAME_FILES_ID, filesId),
						new KeyValuePair(LifeBoxContract.FileOnlineFiles.COLUMN_NAME_ONLINE_FILES_ID, onlineFilesTmbId)
				);

		// insert into entry_hashtags ----------------------------------------------------------------------------------
		hashtagsDbInsert(entriesId, hashtagList);

		// insert into entry_tags --------------------------------------------------------------------------------------
		entryTagsDbInsert(entriesId, tagList);

		// close the handler
		dbIn.close();
	}

	/**
	 * Insert a entry of the mediatype text.
	 */
	private void insertText()
	{
		// insert into text --------------------------------------------------------------------------------------------
		long textId = regularDbInsert
				(
						LifeBoxContract.Text.TABLE_NAME, false, new KeyValuePair(LifeBoxContract.Text.COLUMN_NAME_TEXT, text)
				);

		// get the types_id for entries --------------------------------------------------------------------------------
		long typesId = getTypesId(mediaType);

		// insert into entries -----------------------------------------------------------------------------------------
		long entriesId = regularDbInsert
				(
						LifeBoxContract.Entries.TABLE_NAME,
						false,
						new KeyValuePair(LifeBoxContract.Entries.COLUMN_NAME_MEDIA_ID, textId),
						new KeyValuePair(LifeBoxContract.Entries.COLUMN_NAME_TYPES_ID, typesId),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_TITLE, userTitle),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_DESCRIPTION, userDescription),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_USER_DATE, userTimestamp),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_CREATION_DATE, userTimestamp),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_MODIFICATION_DATE, userTimestamp)
				);

		// insert into hashtags & entry_hashtags -----------------------------------------------------------------------
		hashtagsDbInsert(entriesId, hashtagList);

		// insert into entry_tags --------------------------------------------------------------------------------------
		entryTagsDbInsert(entriesId, tagList);
	}

	/**
	 * Insert a entry of the mediatype movie
	 */
	private void insertMovie()
	{
		// insert into movies ------------------------------------------------------------------------------------------
		long moviesId = regularDbInsert
				(
						LifeBoxContract.Movies.TABLE_NAME,
						false,
						new KeyValuePair(LifeBoxContract.Movies.COLUMN_NAME_TITLE, movieTitle),
						new KeyValuePair(LifeBoxContract.Movies.COLUMN_NAME_DIRECTOR, movieDirector),
						new KeyValuePair(LifeBoxContract.Movies.COLUMN_NAME_DESCRIPTION, movieDescription),
						new KeyValuePair(LifeBoxContract.Movies.COLUMN_NAME_MOVIE_GENRE, movieGenre),
						new KeyValuePair(LifeBoxContract.Movies.COLUMN_NAME_RELEASE_DATE, movieTimestamp),
						new KeyValuePair(LifeBoxContract.Movies.COLUMN_NAME_THUMBNAIL_URL, movieThumbnailUrl)
				);

		// get the types_id for entries --------------------------------------------------------------------------------
		long typesId = getTypesId(mediaType);

		// insert into entries -----------------------------------------------------------------------------------------
		long entriesId = regularDbInsert
				(
						LifeBoxContract.Entries.TABLE_NAME,
						false,
						new KeyValuePair(LifeBoxContract.Entries.COLUMN_NAME_MEDIA_ID, moviesId),
						new KeyValuePair(LifeBoxContract.Entries.COLUMN_NAME_TYPES_ID, typesId),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_TITLE, userTitle),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_DESCRIPTION, userDescription),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_USER_DATE, userTimestamp),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_CREATION_DATE, userTimestamp),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_MODIFICATION_DATE, userTimestamp)
				);

		// insert into hashtags & entry_hashtags -----------------------------------------------------------------------
		hashtagsDbInsert(entriesId, hashtagList);

		// insert into entry_tags
		entryTagsDbInsert(entriesId, tagList);
	}

	/**
	 * Insert a entry of the mediatype music.
	 */
	private void insertMusic()
	{
		// insert into artists if not exists ---------------------------------------------------------------------------
		long artistsId = regularDbInsert
				(
					LifeBoxContract.Artists.TABLE_NAME,
					true,
					new KeyValuePair(LifeBoxContract.Artists.COLUMN_NAME_ARTIST, musicArtist)
				);

		// insert into albums ------------------------------------------------------------------------------------------
		long albumsId = regularDbInsert
				(
					LifeBoxContract.Albums.TABLE_NAME,
					true,
					new KeyValuePair(LifeBoxContract.Albums.COLUMN_NAME_ALBUM, musicAlbum),
					new KeyValuePair(LifeBoxContract.Albums.COLUMN_NAME_RELEASE_DATE, musicTimestamp),
					new KeyValuePair(LifeBoxContract.Albums.COLUMN_NAME_THUMBNAIL_URL, musicThumbnailUrl)
				);

		// insert into music -------------------------------------------------------------------------------------------
		long musicId = regularDbInsert
				(
						LifeBoxContract.Music.TABLE_NAME,
						false,
						new KeyValuePair(LifeBoxContract.Music.COLUMN_NAME_TRACK, musicTrack),
						new KeyValuePair(LifeBoxContract.Music.COLUMN_NAME_ARTISTS_ID, artistsId),
						new KeyValuePair(LifeBoxContract.Music.COLUMN_NAME_ALBUMS_ID, albumsId)
				);

		// get the types_id for entries --------------------------------------------------------------------------------
		long typesId = getTypesId(mediaType);

		// insert into entries -----------------------------------------------------------------------------------------
		long entriesId = regularDbInsert
				(
						LifeBoxContract.Entries.TABLE_NAME,
						false,
						new KeyValuePair(LifeBoxContract.Entries.COLUMN_NAME_MEDIA_ID, musicId),
						new KeyValuePair(LifeBoxContract.Entries.COLUMN_NAME_TYPES_ID, typesId),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_TITLE, userTitle),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_DESCRIPTION, userDescription),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_USER_DATE, userTimestamp),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_CREATION_DATE, userTimestamp),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_MODIFICATION_DATE, userTimestamp)
				);

		// insert into music_genres ------------------------------------------------------------------------------------
		long musicGenresId = regularDbInsert
				(
						LifeBoxContract.MusicGenres.TABLE_NAME,
						false,
						new KeyValuePair(LifeBoxContract.MusicGenres.COLUMN_NAME_MUSIC_ID, musicId),
						new KeyValuePair(LifeBoxContract.MusicGenres.COLUMN_NAME_MUSIC_GENRE, musicGenre)
				);

		// insert into hashtags & entry_hashtags -----------------------------------------------------------------------
		hashtagsDbInsert(entriesId, hashtagList);

		// insert into entry_tags --------------------------------------------------------------------------------------
		entryTagsDbInsert(entriesId, tagList);

	}

	/**
	 * Queries a database table in order to get the key matching a condition.
	 * @param table (String) the db table
	 * @param dataColumn (String) the db column to compare with the condition
	 * @param keyColumn (String) the db collumn holding the key
	 * @param condition (String) the term to compare with dataColumn
	 * @return _id (long) of the matching row or -1 if it fails.
	 */
	private long getDbKey(String table, String dataColumn, String keyColumn, String condition)
	{
		mDbHelper = new DbHelper(getBaseContext());
		SQLiteDatabase db = mDbHelper.getReadableDatabase();

		// define the projection
		String[] projection = {keyColumn};

		// define the selection
		String selection = dataColumn + " == " + "'" + condition +"'";

		// query the database to retrieve a cursor object containing the data
		Cursor c = db.query
				(
						table,
						projection,
						selection,
						null,
						null,
						null,
						null
				);

		// adjust the read position
		c.moveToFirst();

		// get the key
		long id = -1;
		try
		{
			id = c.getLong(c.getColumnIndexOrThrow(keyColumn));
		}
		catch(CursorIndexOutOfBoundsException e)
		{
			id = -1;
			Log.e("cursor out of bounds: ", e.getMessage());
		}

		// cleanup
		c.close();
		db.close();

		return id;
	}

	/**
	 * Queries the database to get the primary key of the table types, matching a given mediatype.
	 * @param mediaType (String) the mediatype that have to match
	 * @return _id (long) the primary key of the row
	 */
	private long getTypesId(String mediaType)
	{
		return getDbKey
				(
						LifeBoxContract.Types.TABLE_NAME,
						LifeBoxContract.Types.COLUMN_NAME_TYPE,
						LifeBoxContract.Types._ID,
						mediaType
				);
	}

	/**
	 * Queries the database to get the primary key of the table filetypes,
	 * in order to figure out what thumbnail-mimetype matchas a given (file-)mimeType.
	 * @param mimeType (String) the mimetype that have to match
	 * @return _id (long) the primary key of the row
	 */
	private long getThumbtype(String mimeType)
	{
		long filetypesId = -1;
		// decide what MIME-Type the thumbnail is of
		if(mimeType.equals(Constants.MIME_TYPE_IMAGE))
		{
			filetypesId = getDbKey
				(
						LifeBoxContract.Filetypes.TABLE_NAME,
						LifeBoxContract.Filetypes.COLUMN_NAME_FILETYPE,
						LifeBoxContract.Filetypes._ID,
						Constants.MIME_TYPE_IMAGE_THUMB
				);
		}
		else if(mimeType.equals(Constants.MIME_TYPE_VIDEO))
		{
			filetypesId = getDbKey
				(
						LifeBoxContract.Filetypes.TABLE_NAME,
						LifeBoxContract.Filetypes.COLUMN_NAME_FILETYPE,
						LifeBoxContract.Filetypes._ID,
						Constants.MIME_TYPE_VIDEO_THUMB
				);
		}

		return filetypesId;
	}

	/**
	 * Performs regular insert into a given table.
	 * @param table (String) the target table
	 * @param unique (boolean) if the row should be unique or not
	 * @param keyValuePair (KeyValuePair [varargs]) variable number of key value pairs,
	 *                     where column=key, insertfield=value
	 * @return _id (long) of the newly inserted row
	 */
	private long regularDbInsert(String table, boolean unique, KeyValuePair... keyValuePair)
	{
		mDbHelper = new DbHelper(getBaseContext());
		SQLiteDatabase db = mDbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();

		for(KeyValuePair kvp : keyValuePair)
		{
			if(kvp.getValueIsString() == true)
			{
				if(kvp.getStringValue().trim().equals(""))
				{
					// insert NULL
					values.putNull(kvp.getKey());
					Log.d(kvp.getKey(), "null i am stfu joda");
				}
				else
				{
					Log.d(kvp.getKey(), kvp.getStringValue());
					values.put(kvp.getKey(), kvp.getStringValue());
				}

			}
			else if(kvp.getValueIsString() == false)
			{
				if(kvp.getLongValue() == 0)
				{
					// insert NULL
					values.putNull(kvp.getKey());
					Log.d(kvp.getKey(), "null i am stfu joda");
				}
				else
				{
					Log.d(kvp.getKey(), ""+kvp.getLongValue());
					values.put(kvp.getKey(), kvp.getLongValue());
				}

			}
		}

		// get the key
		long id = -1;

		if(unique == false)
		{
			id = db.insertOrThrow(table, null, values);
		}
		else if(unique == true)
		{
			// only insert new rows, but always retrieve an id
			id = db.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_IGNORE);
		}

		// cleanup
		values.clear();
		db.close();

		return id;
	}

	/**
	 * Inserts all new hashtags of a List into the hashtags-table
	 * and inserts all hashtags that belong to a single entry.
	 * @param entriesId (long) the primary key of the entry
	 * @param hashtagList (ArrayList<String>) List containing the hashtags
	 */
	private void hashtagsDbInsert(long entriesId, ArrayList<String> hashtagList)
	{
		if(!hashtagList.isEmpty())
		{
			// initialize db object
			mDbHelper = new DbHelper(getBaseContext());
			SQLiteDatabase db = mDbHelper.getWritableDatabase();

			// for all hashtags in the List
			for(String hashtag : hashtagList)
			{
				// get the id (long > 0 || -1)
				long hashtagsId = getDbKey
						(
							LifeBoxContract.Hashtags.TABLE_NAME,
							LifeBoxContract.Hashtags.COLUMN_NAME_HASHTAG,
							LifeBoxContract.Hashtags._ID,
							hashtag
						);
				Log.d("hashtag", hashtag+" id: "+hashtagsId+" found");

				// insert if new
				if(hashtagsId == -1)
				{
					hashtagsId = regularDbInsert
							(
								LifeBoxContract.Hashtags.TABLE_NAME,
								true,
								new KeyValuePair(LifeBoxContract.Hashtags.COLUMN_NAME_HASHTAG, hashtag)
							);

					Log.d("hashtag", hashtag+" id: "+hashtagsId+" inserted into hashtags");
				}

				// always insert the hashtag in enty_hashtags
				long entryHashtagsId = regularDbInsert
						(
								LifeBoxContract.EntryHashtags.TABLE_NAME,
								false,
								new KeyValuePair(LifeBoxContract.EntryHashtags.COLUMN_NAME_ENTRIES_ID, entriesId),
								new KeyValuePair(LifeBoxContract.EntryHashtags.COLUMN_NAME_HASHTAGS_ID, hashtag)
						);
				Log.d("hashtag", hashtag+" id: "+hashtagsId+" inserted into entry_hashtags");
			}

			db.close();
		}
	}

	/**
	 * Inserts all tags that belong to a single entry.
	 * @param entriesId (long) the primary key of the entry
	 * @param tagList (ArrayList<String>) List containing the tagnames
	 */
	private void entryTagsDbInsert(long entriesId, ArrayList<String> tagList)
	{
		if(!tagList.isEmpty())
		{
			mDbHelper = new DbHelper(getBaseContext());
			SQLiteDatabase db = mDbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();

			for(String tag : tagList)
			{
				// get the db-key of the tag(name)
				long tagsId = getDbKey(LifeBoxContract.Tags.TABLE_NAME, LifeBoxContract.Tags.COLUMN_NAME_TAG, LifeBoxContract.Tags._ID, tag);

				values.put(LifeBoxContract.EntryTags.COLUMN_NAME_ENTRIES_ID, entriesId);
				values.put(LifeBoxContract.EntryTags.COLUMN_NAME_TAGS_ID, tagsId);

				long id = db.insertOrThrow(LifeBoxContract.EntryTags.TABLE_NAME, null, values);
			}

			db.close();
		}
	}





















	//			// gets the data repository in write mode
//			SQLiteDatabase db = mDbHelper.getWritableDatabase();
//
//			// get the hashtag from the edit-text field
//			historyEditText = (EditText) findViewById(R.id.in_hashtag);
//			String hashtag = historyEditText.getText().toString();
//
//			// check if there is a user input to fetch
//			if( (null != hashtag) && (!hashtag.equals("") && (hashtag.trim().length() >= 0)) )
//			{
//				// create a new map of values, where column names are the keys
//				ContentValues values = new ContentValues();
//				values.put(LifeBoxContract.Hashtags.COLUMN_NAME_NAME, hashtag);
//
//				// insert the new row, returning the primary key value of the new row
//				long newRowId;
//				newRowId = db.insert(
//						LifeBoxContract.Hashtags.TABLE_NAME,
//						null,
//						values);
//
//				db = mDbHelper.getReadableDatabase();
//				mCursor = db.query(
//						LifeBoxContract.Hashtags.TABLE_NAME,
//						projection,
//						null,
//						null,
//						null,
//						null,
//						null,
//						null
//				);
//
//				// for the cursor adapter, specify which columns go into which views
//				String[] fromColumns = {LifeBoxContract.Hashtags.COLUMN_NAME_NAME};
//				int[] toViews = {R.id.entry_history_hashtag}; // The TextView in simple_list_item_1
//
//				// refresh the view
//				mScAdapter.changeCursorAndColumns(mCursor, fromColumns, toViews);
//				mScAdapter.notifyDataSetChanged();
//
//				// clear the input field
//				historyEditText.setText("");
//
////				historyListView.requestFocusFromTouch();
////				historyListView.setSelection(1);
//
////				historyListView.setItemChecked(historyListView.getCount(), true);
////				long[] ids = historyListView.getCheckedItemIds();
////
////				long id = ids[0];
////
////				Log.e("selection", "" + "" + id);
////
////				historyListView.deferNotifyDataSetChanged();
//
//
//
//				Log.e("sql", "" + values);









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