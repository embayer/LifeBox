package de.neungrad.lifebox.ui;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import de.neungrad.lifebox.R;
import de.neungrad.lifebox.db.DbHelper;
import de.neungrad.lifebox.helper.Constants;
import de.neungrad.lifebox.helper.Entry;

import java.util.ArrayList;

/**
 * Detail view of a single timeline entry.
 * @version 0.1 05.08.13
 * @autor Markus Bayer
 */
public class TimelineDetailActivity extends Activity implements DeleteEntryDialogFragment.NoticeDialogListener
{
	public static final String TAG = "TimelineDetailActivity";

	// databasehelper
	private DbHelper mDbHelper;

	// the extra (_id of the entry)
	private String entriesId;

	// the media entriesId of entry for the specific child Activities
	private String mediaId;
	// the type of the entry
	private String type;

	// the viewed entry
	private Entry entry;
	// the tags and hashtags stored in Lists
	private ArrayList<String> tagList;
	private ArrayList<String> hashtagList;

	// the ui elements
	private LinearLayout specificLL;
	private TextView dateTV;
	private TextView timeTV;
	private TextView titleTV;
	private TextView descriptionTV;

	private TextView hashtagsTV;

	private TableLayout tagsTL;

	// table rows to store tags
	TableRow tableRow1;
	TableRow tableRow2;
	TableRow tableRow3;

	/** Called when the Activity is first created. */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timelinedetail);

		// get the extra
		Intent intent = getIntent();
		entriesId = intent.getStringExtra(Constants.ENTRY_ID_EXTRA);

		mDbHelper = new DbHelper(getBaseContext());
		// get the Entry from the database
		entry = mDbHelper.selectEntry(entriesId);

		mediaId = String.valueOf(entry.getMediaId());

		type = entry.getType();

		// initialize the layout for the child layouts
		specificLL = (LinearLayout) findViewById(R.id.timelinedetail_specific_linearlayout);

		// initialize the ui elements with the entry fields
		dateTV = (TextView) findViewById(R.id.timelinedetail_date_textview);
		dateTV.setText(entry.getDate());

		timeTV = (TextView) findViewById(R.id.timelinedetail_time_textview);
		timeTV.setText(entry.getTime());

		titleTV = (TextView) findViewById(R.id.timelinedetail_title_textview);
		titleTV.setText(entry.getTitle());

		descriptionTV = (TextView) findViewById(R.id.timelinedetail_description_textview);
		descriptionTV.setText(entry.getDescription());

		// description is optional
		if(entry.getDescription().trim().equals(""))
		{
			descriptionTV.setVisibility(View.INVISIBLE);
		}

		hashtagsTV = (TextView) findViewById(R.id.timelinedetail_hashtags_textview);

		tagsTL = (TableLayout) findViewById(R.id.timelinedetail_tags_tablelayout);

		tableRow1 = (TableRow) findViewById(R.id.timelinedetail_tags_row1);
		tableRow2 = (TableRow) findViewById(R.id.timelinedetail_tags_row2);
		tableRow3 = (TableRow) findViewById(R.id.timelinedetail_tags_row3);

		// get the tag List
		tagList = mDbHelper.selectTags(entriesId);

		// counter to decide in which row of the table layout should be inserted
		int cnt = 0;

		// add the fetched tags
		for(String tag : tagList)
		{
			// 0 is not valid and will be ignored
			int imgId = 0;

			try
			{
				// get the entriesId to the string representation of the drawable
				imgId = R.drawable.class.getField(tag).getInt(null);
			}
			catch (IllegalAccessException e)
			{
				Log.e(TAG, e.getMessage());
			}
			catch (NoSuchFieldException e)
			{
				Log.e(TAG, e.getMessage());
			}

			ImageView iv = new ImageView(TimelineDetailActivity.this);
			iv.setBackgroundResource(imgId);

			// break the rows
			if(cnt < 10)
			{
				tableRow1.addView(iv);
			}
			if(cnt >= 10 && cnt < 20)
			{
				tableRow2.addView(iv);
			}
			if(cnt >= 20)
			{
				tableRow3.addView(iv);
			}

			cnt++;
		}

		//get the hashtag List
		hashtagList = mDbHelper.selectHashtags(entriesId);

		// wright the first without semicolon
		String hashtags = "";

		boolean first = true;

		for(String hashtag : hashtagList)
		{
			if(first)
			{
				hashtags = hashtag;
				first = false;
			}
			else
			{
				hashtags = hashtags + ", " + hashtag;
			}
		}

		hashtagsTV.setText(hashtags);
	}

	/**
	 * Called when the activity is first created.
	 * Builds the action bar menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.entrydetail, menu);

		return super.onCreateOptionsMenu(menu);
	}

	/** Called when the a item on the action bar is pressed. */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// handle presses on the action bar items
		switch (item.getItemId())
		{
			case R.id.action_delete:
				// create and show a dialog where the user must confirm (or cancel) the deletion
				DialogFragment dialog = new DeleteEntryDialogFragment();
				dialog.show(getFragmentManager(), "DeleteEntryDialogFragment");
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/** Getters */
	public Entry getEntry()
	{
		return entry;
	}

	public String getMediaId()
	{
		return mediaId;
	}

	public LinearLayout getSpecificLL()
	{
		return specificLL;
	}

	// the dialog fragment receives a reference to this Activity by the
	// Fragment.onAttach() callback, which it uses to call the following methods
	// defined by the NoticeDialogFragment.NoticeDialogListener interface

	/**
	 * Called when the user clicks 'confirm' on the dialog.
	 * @param dialog (DialogFragment) the popup dialog.
	 */
	@Override
	public void onDialogPositiveClick(DialogFragment dialog)
	{
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra(Constants.CALLER_EXTRA, Constants.CALLER_TIMELINE_DETAIL_ACTIVITY);

		boolean success = false;

		// decide by mediatype
		if(type.equals(Constants.TYPE_FILE))
		{
			success = mDbHelper.deleteFile(entriesId, mediaId);
		}
		else if(type.equals(Constants.TYPE_TEXT))
		{
			success = mDbHelper.deleteText(entriesId, mediaId);
		}
		else if(type.equals(Constants.TYPE_MUSIC))
		{
			success = mDbHelper.deleteMusic(entriesId, mediaId);
		}
		else if(type.equals(Constants.TYPE_MOVIE))
		{
			success = mDbHelper.deleteMovie(entriesId, mediaId);
		}

		// pass to main activity
		if(success)
		{
			Log.d(TAG, type + " deleted successfully.");
		}

		startActivity(intent);
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog)
	{
		// do nothing
	}
}