package de.lifebox.LifeBox;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import de.lifebox.LifeBox.controller.HashtagsDbHelper;

import java.util.ArrayList;

/**
 * Activity to create and select hashtags.
 * ! API Level 11 required
 * @version 0.1 28.06.13
 * @autor Markus Bayer
 */
public class HashtagsActivity extends Activity
{
	//TODO PRIVATE
	private HashtagsDbHelper mDbHelper;

	// the listviews to hold the hashtags
	private ListView historyListView;            	// previous created hashtags
	private ListView hashtagListView;               // hashtags for the entry

	// the entries of the listviews
	private EditText historyEditText;
	private EditText hashtagEditText;

	// the adapter intermediate between view and data
	private SimpleCursorAdapter mScAdapter;
	private ArrayAdapter<String> mAadapter;

	// db-instance
	private SQLiteDatabase db;
	// pointer to the selected db content
	private Cursor mCursor;
	// db-columns
	private String[] projection = {LifeBoxContract.Hashtags._ID, LifeBoxContract.Hashtags.COLUMN_NAME_NAME};

	private ArrayList<String> mHashtags;

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
			if(insertItem(item, mHashtags))
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
			mHashtags.remove(item);

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
			historyEditText = (EditText) findViewById(R.id.in_hashtag);
			String hashtag = historyEditText.getText().toString();
			hashtag = hashtag.trim();

			// check if there is a user input to fetch
			if( (null != hashtag) && (!hashtag.equals("") && (hashtag.length() >= 0)) )
			{
				// insert the item if it is not yet present
				if(insertItem(hashtag, mHashtags))
				{
					// notify the adapter that the data has changed
					mAadapter.notifyDataSetChanged();
				}

				// clear the input field
				historyEditText.setText("");
			}
			else
			{
				Toast.makeText(getBaseContext(), "Please insert a hashtag.", Toast.LENGTH_SHORT);
			}
		}
	};

	// listener for the save button
	Button.OnClickListener mSaveListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			Intent returnIntent = new Intent(getBaseContext(), MetaFormActivity.class);

			returnIntent.putStringArrayListExtra(Constants.HASHTAG_EXTRA, mHashtags);

			setResult(RESULT_OK, returnIntent);
			finish();

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
//			}
//			else
//			{
//				Toast.makeText(getBaseContext(), "Please insert a hashtag.", Toast.LENGTH_SHORT);
//			}
		}
	};

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hashtags);

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
		mDbHelper = new HashtagsDbHelper(getBaseContext());
		db = mDbHelper.getReadableDatabase();
		mCursor = db.query(
				LifeBoxContract.Hashtags.TABLE_NAME,
				projection,
				null,
				null,
				null,
				null,
				null,
				null
		);

		// for the cursor adapter, specify which columns go into which views
		String[] fromColumns = {LifeBoxContract.Hashtags.COLUMN_NAME_NAME};
		int[] toViews = {R.id.entry_history_hashtag}; // The TextView in simple_list_item_1

		// create the adapter
		mScAdapter = new SimpleCursorAdapter(this,
				R.layout.hashtaghistorylist, mCursor,
				fromColumns, toViews, 0);

		historyListView.setAdapter(mScAdapter);
		// )

		// hashtagListView part
		// (
		hashtagListView = (ListView) findViewById(R.id.listview_hashtags);
		hashtagListView.setOnItemClickListener(hashtagListener);

		// get the extra
		if(getIntent().hasExtra(Constants.HASHTAG_EXTRA))
		{
			mHashtags = getIntent().getStringArrayListExtra(Constants.HASHTAG_EXTRA);
		}
		else
		{
			mHashtags = new ArrayList<String>();
		}

		mAadapter = new ArrayAdapter<String>(this, R.layout.hashtaglist, R.id.entry_hashtag, mHashtags);

		hashtagListView.setAdapter(mAadapter);
		// )

		//##############################################################################################################
		historyEditText = (EditText) findViewById(R.id.in_hashtag);
		// provide clear functionality by the "x" icon within the input field
		String value = "";    // pre-fill the input field

		// icon
		final Drawable x = getResources().getDrawable(R.drawable.x);
		// place it
		x.setBounds(0, 0, x.getIntrinsicWidth(), x.getIntrinsicHeight());
		historyEditText.setCompoundDrawables(null, null, value.equals("") ? null : x, null);

		historyEditText.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if (historyEditText.getCompoundDrawables()[2] == null)
				{
					return false;
				}
				if (event.getAction() != MotionEvent.ACTION_UP)
				{
					return false;
				}
				// when clicked
				if (event.getX() > historyEditText.getWidth() - historyEditText.getPaddingRight() - x.getIntrinsicWidth())
				{
					// clear text
					historyEditText.setText("");
					// remove icon
					historyEditText.setCompoundDrawables(null, null, null, null);
				}
				return false;
			}
		});

		// show icon when there is an input
		historyEditText.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				historyEditText.setCompoundDrawables(null, null, historyEditText.getText().toString().equals("") ? null : x, null);
			}

			// unneeded methods
			@Override
			public void afterTextChanged(Editable arg0) {}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		});
		//##############################################################################################################





		// Prepare the loader.  Either re-connect with an existing one,
		// or start a new one.
//		getLoaderManager().initLoader(0, null, this);
	}

	/**
	 * Inserts a item to the given ArrayList<String>,
	 * if the item is not already present.
	 * @param item (String) the hashtag to insert
	 * @param list (ArrayList<String>) the list holding the hashtags
	 * @return true if the item was inserted, otherwise false
	 */
	public boolean insertItem(String item, ArrayList<String> list)
	{
		boolean success = false;

		if(!list.contains(item))
		{
			list.add(item);
			success = true;
		}

		return success;
	}

//	// Called when a new Loader needs to be created
//	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//		// Now create and return a CursorLoader that will take care of
//		// creating a Cursor for the data being displayed.
//		return new CursorLoader(this, ,
//				projection, null, null, null);
//	}
//
//	// Called when a previously created loader has finished loading
//	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//		// Swap the new cursor in.  (The framework will take care of closing the
//		// old cursor once we return.)
//		mScAdapter.swapCursor(data);
//	}
//
//	// Called when a previously created loader is reset, making the data unavailable
//	public void onLoaderReset(Loader<Cursor> loader) {
//		// This is called when the last Cursor provided to onLoadFinished()
//		// above is about to be closed.  We need to make sure we are no
//		// longer using it.
//		mScAdapter.swapCursor(null);
//	}

//	public void onItemClick(AdapterView parent, View view, int position, long id)
//	{
//		final String text = ((TextView)view).getText();
//	}

//	public void onListItemClick(ListView l, View v, int position, long id) {
//		// Do something when a list item is clicked
//		Log.e("lv", "clicked");
//	}
}