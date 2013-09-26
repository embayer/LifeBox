package de.lifebox.LifeBox;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;

/**
 * Activity to create and select hashtags.
 * ! API Level 11 required
 * @version 0.1 28.06.13
 * @autor Markus Bayer
 */
public class HashtagsActivity extends Activity
{
	// the listviews to hold the hashtags
	private ListView historyListView;            	// previous created hashtags
	private ListView hashtagListView;               // hashtags for the entry

	// the entries of the listviews
	protected EditText hashtagEditText;

	// the adapter intermediate between view and data
	private SimpleCursorAdapter mScAdapter;
	protected ArrayAdapter<String> mAadapter;

	// db-instance
	private SQLiteDatabase db;
	private DbHelper mDbHelper;
	// cursor to the selected db content
	private Cursor mCursor;

	// Lists for the hashtags
	protected ArrayList<String> hashtagList;
	protected ArrayList<String> hashtagHistoryList;

	// OnKeyListener for the Enter softkey button
	EditText.OnKeyListener hashtagKeyListener = new View.OnKeyListener()
	{
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event)
		{
			boolean eventConsumed = false;

			// listen to the dpad and the enter softkey
			if(keyCode ==  KeyEvent.KEYCODE_DPAD_CENTER
					|| keyCode ==  KeyEvent.KEYCODE_ENTER)
			{

				// to prevent default behavior do nothing on down but on up
				if(event.getAction() == KeyEvent.ACTION_UP)
				{
					fetchHashtag();
				}
				eventConsumed = true;

			}
			else
			{
				// other key
				eventConsumed = false;
			}

			return eventConsumed;
		}
	};


	// listener for the historyListView
	ListView.OnItemClickListener historyListener = new ListView.OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			// get the text of the clicked item
			TextView tv = (TextView) view.findViewById(R.id.entry_history_hashtag);
			String item = tv.getText().toString();

			// insert the item if it is not yet present
			if(insertItem(item, hashtagList))
			{
				// notify the adapter that the data has changed
				mAadapter.notifyDataSetChanged();
			}
		}
	};

	// listener for the hashtagListView
	ListView.OnItemClickListener hashtagListener = new ListView.OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			// get the text of the clicked item
			TextView tv = (TextView) view.findViewById(R.id.entry_hashtag);
			String item = tv.getText().toString();

			// remove the item from the ListView
			hashtagList.remove(item);

			// notify the adapter that the data has changed
			mAadapter.notifyDataSetChanged();
		}
	};

	// listener for the ok-button
	Button.OnClickListener mCreateListener = new Button.OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			// get the hashtag from the edit-text field
			fetchHashtag();
		}
	};

	// listener for the save button
	Button.OnClickListener mSaveListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			saveHashtags();
		}
	};

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hashtags);

		// create the EditText, add the listener
		hashtagEditText = (EditText) findViewById(R.id.in_hashtag);
		hashtagEditText.setOnKeyListener(hashtagKeyListener);

		// create the buttons, add the listeners
		Button createBtn = (Button) findViewById(R.id.create_hashtag);
		createBtn.setOnClickListener(mCreateListener);

		Button saveBtn = (Button) findViewById(R.id.save_hashtags);
		saveBtn.setOnClickListener(mSaveListener);

		// historyListView part
		// (
		historyListView = (ListView) findViewById(R.id.listview_history_hashtags);
		historyListView.setOnItemClickListener(historyListener);

		// query the db
		mDbHelper = new DbHelper(getBaseContext());
		db = mDbHelper.getReadableDatabase();

		String query =
				"SELECT h._id, h.hashtag FROM entry_hashtags eh " +
				"INNER JOIN hashtags h ON h._id = eh.hashtags_id " +
				"GROUP BY h.hashtag " +
				"ORDER BY COUNT(h._id) DESC;";

		mCursor = db.rawQuery(query, null);

		// for the cursor adapter, specify which columns go into which views
		String[] fromColumns = {LifeBoxContract.Hashtags.COLUMN_NAME_HASHTAG};
		int[] toViews = {R.id.entry_history_hashtag}; // The TextView in simple_list_item_1

		// create the adapter
		mScAdapter = new SimpleCursorAdapter(this,
				R.layout.hashtaghistorylist, mCursor,
				fromColumns, toViews, 0);

		// save the history hashtags
		hashtagHistoryList = new ArrayList<String>();
		if(mCursor.moveToFirst())
		{
			while(!mCursor.isAfterLast())
			{
				hashtagHistoryList.add(mCursor.getString(mCursor.getColumnIndexOrThrow(LifeBoxContract.Hashtags.COLUMN_NAME_HASHTAG)));

				mCursor.moveToNext();
			}
		}

		historyListView.setAdapter(mScAdapter);
		// )

		// hashtagListView part
		// (
		hashtagListView = (ListView) findViewById(R.id.listview_hashtags);
		hashtagListView.setOnItemClickListener(hashtagListener);

		// get the extra
		if(getIntent().hasExtra(Constants.HASHTAG_ARRAY_EXTRA))
		{
			hashtagList = getIntent().getStringArrayListExtra(Constants.HASHTAG_ARRAY_EXTRA);
		}
		else
		{
			hashtagList = new ArrayList<String>();
		}

		mAadapter = new ArrayAdapter<String>(this, R.layout.hashtaglist, R.id.entry_hashtag, hashtagList);

		hashtagListView.setAdapter(mAadapter);
		// )

		hashtagEditText = (EditText) findViewById(R.id.in_hashtag);

		// provide clear functionality by the "x" icon within the input field
		String value = "";    // pre-fill the input field

		// icon
		final Drawable x = getResources().getDrawable(R.drawable.x);
		// place it
		x.setBounds(0, 0, x.getIntrinsicWidth(), x.getIntrinsicHeight());
		hashtagEditText.setCompoundDrawables(null, null, value.equals("") ? null : x, null);

		hashtagEditText.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if (hashtagEditText.getCompoundDrawables()[2] == null)
				{
					return false;
				}
				if (event.getAction() != MotionEvent.ACTION_UP)
				{
					return false;
				}
				// when clicked
				if (event.getX() > hashtagEditText.getWidth() - hashtagEditText.getPaddingRight() - x.getIntrinsicWidth())
				{
					// clear text
					hashtagEditText.setText("");
					// remove icon
					hashtagEditText.setCompoundDrawables(null, null, null, null);
				}
				return false;
			}
		});

		// show icon when there is an input
		hashtagEditText.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				hashtagEditText.setCompoundDrawables(null, null, hashtagEditText.getText().toString().equals("") ? null : x, null);
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
	 * Inserts a item to the given ArrayList<String>,
	 * if the item is not already present.
	 * @param item (String) the hashtag to insert
	 * @param list (ArrayList<String>) the list holding the hashtags
	 * @return true if the item was inserted, otherwise false
	 */
	protected boolean insertItem(String item, ArrayList<String> list)
	{
		boolean success = false;

		if(!list.contains(item))
		{
			list.add(item);
			success = true;
		}

		return success;
	}

	/**
	 * Fetch the user input and add it to the hashtags
	 */
	private void fetchHashtag()
	{
		// get the hashtag from the edit-text field
		String hashtag = hashtagEditText.getText().toString();
		hashtag = hashtag.trim();

		// check if there is a user input to fetch
		if(!hashtag.equals(""))
		{
			// insert the item if it is not yet present
			if(insertItem(hashtag, hashtagList))
			{
				// notify the adapter that the data has changed
				mAadapter.notifyDataSetChanged();
			}

			// clear the input field
			hashtagEditText.setText("");
		}
		else
		{
			Toast.makeText(getBaseContext(), "Please insert a hashtag.", Toast.LENGTH_SHORT).show();
		}
	}

	/** Sends a list containing all selected hashtags back to the caller. */
	private void saveHashtags()
	{
		Intent returnIntent = new Intent(getBaseContext(), MetaFormActivity.class);

		returnIntent.putStringArrayListExtra(Constants.HASHTAG_ARRAY_EXTRA, hashtagList);

		setResult(RESULT_OK, returnIntent);
		finish();
	}
}