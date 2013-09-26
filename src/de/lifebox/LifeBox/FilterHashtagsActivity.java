package de.lifebox.LifeBox;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Activity which lets the user select the hashtags for the filter.
 * @version 0.1 12.08.13
 * @autor Markus Bayer
 */
public class FilterHashtagsActivity extends HashtagsActivity
{
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		super.hashtagEditText.setHint("Search for a existing hashtag");

		super.mCreateListener = new Button.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// get the hashtag from the edit-text field
				fetchHashtag();
			}
		};

		Button createBtn = (Button) findViewById(R.id.create_hashtag);
		createBtn.setOnClickListener(mCreateListener);

		// OnKeyListener for the Enter softkey button
		super.hashtagKeyListener = new View.OnKeyListener()
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

		hashtagEditText = (EditText) findViewById(R.id.in_hashtag);
		hashtagEditText.setOnKeyListener(hashtagKeyListener);
	}

	/**
	 * Fetch the user input and add it to the hashtags if it is already in the hashtagHistoryList
	 */
	private void fetchHashtag()
	{
		// get the hashtag from the edit-text field
		String hashtag = hashtagEditText.getText().toString();
		hashtag = hashtag.trim();

		// check if there is a user input to fetch
		if(!hashtag.equals(""))
		{
			if(super.hashtagHistoryList.contains(hashtag))
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
				Toast toast = Toast.makeText(getBaseContext(), "Sorry, nothing found.", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 120);
				toast.show();
			}

		}
		else
		{
			Toast.makeText(getBaseContext(), "Please insert a hashtag.", Toast.LENGTH_SHORT).show();
		}
	}
}