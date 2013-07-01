package de.lifebox.LifeBox;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Activity to create and select hashtags.
 * @version 0.1 28.06.13
 * @autor Markus Bayer
 */
public class HashTagsActivity extends Activity
{
	// listener for the button
	Button.OnClickListener mSendListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			HashtagsDbHelper mDbHelper = new HashtagsDbHelper(getBaseContext());

			// gets the data repository in write mode
			SQLiteDatabase db = mDbHelper.getWritableDatabase();

			// get the hashtag from the edit-text field
			EditText editText = (EditText) findViewById(R.id.in_hashtag);
			String hashtag = editText.getText().toString();

			// create a new map of values, where column names are the keys
			ContentValues values = new ContentValues();
			values.put(LifeBoxContract.Hashtags.COLUMN_NAME_HASHTAG, hashtag);

			// insert the new row, returning the primary key value of the new row
			long newRowId;
			newRowId = db.insert(
					LifeBoxContract.Hashtags.TABLE_NAME,
					null,
					values);

			Log.e("sql", ""+values);
		}
	};

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hashtags);

		Button sendBtn = (Button) findViewById(R.id.send_hashtag);
		sendBtn.setOnClickListener(mSendListener);
	}
}