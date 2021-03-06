package de.neungrad.lifebox.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import de.neungrad.lifebox.db.DbHelper;
import de.neungrad.lifebox.db.LifeBoxContract;
import de.neungrad.lifebox.helper.Constants;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * IntentService to dynamically reload data from the database.
 * @version 0.1 30.07.13
 * @autor Markus Bayer
 */
public class TimelineReloadService extends IntentService
{
	private static final String TAG = "TimelineReloadService";

	protected final int ROWAMOUNT = Constants.LIMIT; 		// 10 results is a set
	private final int COLUMNAMOUNT = 6;						// each entry has 6 (View) elements

	protected DbHelper mDbHelper;

	private ArrayList<String> typesList;
	private ArrayList<String> filetypesList;

	/** Creates an IntentService.  Invoked by your subclass's constructor. */
	public TimelineReloadService()
	{
		super("TimelineReloadService");
	}

	/** Called when the service is first created. */
	@Override
	public void onCreate()
	{
		super.onCreate();

		// databasehelper
		mDbHelper = new DbHelper(getBaseContext());
	}

	/** Invoke the worker thread that runs independently from other application logic. */
	@Override
	protected void onHandleIntent(Intent intent)
	{
		// get the extra
		int offset = intent.getIntExtra(Constants.OFFSET_EXTRA, 0);

		// array of _ids
		String[][] dbEntryList = mDbHelper.selectEntrySet(null, "DESC", ROWAMOUNT, offset);
		// result array with all data
		String[][] timelineEntryList = null;

		// no results
		if(null == dbEntryList)
		{
			// return an empty array to TimelineFragment to show that there is no result
			timelineEntryList = new String[0][];
			Log.d(TAG, "No entries where fetched.");
		}
		else
		{
			// fetch the data
			timelineEntryList = generateTimelineEntries(dbEntryList);
		}

		// return to TimelineFragment
		Intent localIntent = new Intent(Constants.BROADCAST_ACTION_RELOADRESPONSE);

		Bundle bundle = new Bundle();
		bundle.putSerializable(Constants.TIMELINE_ENTRIES_ARRAY_EXTRA, timelineEntryList);

		localIntent.putExtra(Constants.TIMELINE_ENTRIES_BUNDLE_EXTRA, bundle);

		localIntent.addCategory(Intent.CATEGORY_DEFAULT);

		// broadcasts the Intent to receivers in this app
		LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
	}

	/**
	 * Generates a set of timeline entries by getting the needed information from the database.
	 * @param entrySetList (String[][]) a list of db field which is the overlap of all items.
	 * @return (String[][]) the result set as array (because an array can be passed via intent extra).
	 */
	protected String[][] generateTimelineEntries(String[][] entrySetList)
	{
		// the result list same amount of rows as the entrySetList containing db results
		final int resultAmount = 9;
		String[][] resultList = new String[entrySetList.length][resultAmount];

		// the result set in the order of insertion
		String type = null;
		String filetype = null;
		String entryId = null;
		String thumbnail = null;
		String date = null;
		String time = null;
		String title = null;
		String firstText = null;
		String secondText = null;

		// the temporary needed values
		String mediaId = null;
		String description = null;

		// iterate the given string array in order to read the fetched values
		for(int r = 0; r < entrySetList.length; r++)
		{
			Log.d(TAG, "new entry ->");
			for(int c = 0; c < COLUMNAMOUNT; c++)
			{
				switch(c)
				{
					// _id
					case 0:
						entryId = entrySetList[r][c];
						Log.d(TAG, "id: "+entrySetList[r][c]);
						break;

					// media_id
					case 1:
						mediaId = entrySetList[r][c];
						Log.d(TAG, "media_id: "+entrySetList[r][c]);
						break;

					// type
					case 2:
						type = entrySetList[r][c];
						Log.d(TAG, "type "+entrySetList[r][c]);
						break;

					// title
					case 3:
						title = entrySetList[r][c];
						Log.d(TAG, "title: "+entrySetList[r][c]);
						break;

					// description
					case 4:
						description = entrySetList[r][c];
						Log.d(TAG, "description: "+entrySetList[r][c]);
						break;

					// user_date
					case 5:
						date = getDate(entrySetList[r][c]);
						time = getTime(entrySetList[r][c]);
						Log.d(TAG, "date: "+date);
						Log.d(TAG, "time: "+time);
						break;

					default:
						break;
				}
			}

			// get the missing fields
			// lookup what (media)type the entry is of
			// mediatype file
			if(type.equals(Constants.TYPE_FILE))
			{
				String filetypesId = mDbHelper.selectSingleField
						(
								LifeBoxContract.Files.TABLE_NAME,
								new String[]{LifeBoxContract.Files._ID, LifeBoxContract.Files.COLUMN_NAME_FILETYPES_ID},
								LifeBoxContract.Files._ID + " == " + mediaId,
								false
						);

				// filetype (image/jpeg || video/mp4), only files have them
//				filetype = filetypesList.get(Integer.parseInt(filetypesId) -1);
				filetype = mDbHelper.selectFiletypesFiletype(mediaId);

				// the thumbnail of the file
				thumbnail = mDbHelper.selectFileThumbnail(mediaId);
				firstText = description;
				secondText = "";


				Log.d(TAG, "filetypes_id: " + filetypesId);
				Log.d(TAG, "filetype: " + filetype);
				Log.d(TAG, "thumbnail path: " + thumbnail);
			}
			// mediatype text
			else if(type.equals(Constants.TYPE_TEXT))
			{
				// the text
				firstText = mDbHelper.selectSingleField
						(
								LifeBoxContract.Text.TABLE_NAME,
								new String[]{LifeBoxContract.Text._ID, LifeBoxContract.Text.COLUMN_NAME_TEXT},
								mediaId,
								true
						);

				secondText = "";

				thumbnail = "";
				filetype = "";
				Log.d(TAG, "text: "+firstText);
			}
			// mediatype music
			else if(type.equals(Constants.TYPE_MUSIC))
			{
				Map<String, String> musicList = mDbHelper.selectMusicMap(mediaId);
				// the track
				firstText = musicList.get("Track");
				// the artist
				secondText = musicList.get("Artist");
				// the music thumbnail
				thumbnail = musicList.get("Music Thumbnail");
				filetype = "";

				Log.d(TAG, "track: "+firstText);
				Log.d(TAG, "artist: "+secondText);
				Log.d(TAG, "musicThumbnail: "+thumbnail);
			}
			// mediatype movie
			else if(type.equals((Constants.TYPE_MOVIE)))
			{
				Map<String, String> movieList = mDbHelper.selectMovieMap(mediaId);
				// the movie title
				firstText = movieList.get("Movie Title");
				// the director
				secondText = movieList.get("Director");
				// the movie thumbnail
				thumbnail = movieList.get("Movie Thumbnail");
				filetype = "";

				Log.d(TAG, "movieTitle: "+firstText);
				Log.d(TAG, "director: "+secondText);
				Log.d(TAG, "movieThumbnail: "+thumbnail);
			}

			// save the fetched results
			for(int col = 0; col < resultAmount; col++)
			{
				switch(col)
				{
					// type
					case 0:
						resultList[r][col] = type;
						break;

					// filetype
					case 1:
						resultList[r][col] = filetype;
						break;

					// entry_id
					case 2:
						resultList[r][col] = entryId;
						break;

					// thumbnail
					case 3:
						resultList[r][col] = thumbnail;
						break;

					// date
					case 4:
						resultList[r][col] = date;
						break;

					// time
					case 5:
						resultList[r][col] = time;
						break;

					// title
					case 6:
						resultList[r][col] = title;
						break;

					case 7:
						// first text
						resultList[r][col] = firstText;
						break;

					case 8:
						// second text
						resultList[r][col] = secondText;
						break;

					default:
						break;
				}
			}
		}

		return resultList;
	}

	/**
	 * Gets the date from a timestamp string
	 * @param timestampString (String) the timestamp
	 * @return (String) the date.
	 */
	private String getDate(String timestampString)
	{
		Timestamp timestamp = new Timestamp(Long.parseLong(timestampString));
		Date date = new Date(timestamp.getTime());

		String dateString = new SimpleDateFormat("yyyy-MM-dd").format(date);

		return dateString;
	}

	/**
	 * Gets the time from a timestamp string
	 * @param timestampString (String) the timestamp
	 * @return (String) the time.
	 */
	private String getTime(String timestampString)
	{
		Timestamp timestamp = new Timestamp(Long.parseLong(timestampString));
		Date date = new Date(timestamp.getTime());

		String timeString = new SimpleDateFormat("HH:mm").format(date);

		return timeString;
	}
}