package de.lifebox.LifeBox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

/**
 * Activity to select pre-defined tags.
 * @version 0.1 28.06.13
 * @autor Markus Bayer
 */
public class TagsActivity extends Activity
{
	// tag button states
	private boolean tagStateHobby = false;		// playing cards

	// the button listeners
//	Button.OnClickListener mTagListener = new Button.OnClickListener()
//	{
//		@Override
//		public void onClick(View v)
//		{
//			// toggle the tag state
//			tagStateHobby = !tagStateHobby;
//		}
//	};

	// listener for the save button
	Button.OnClickListener mSaveListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			Intent returnIntent = new Intent(getBaseContext(), MetaFormActivity.class);

			returnIntent.putExtra("tagHobby", tagStateHobby);


			setResult(RESULT_OK, returnIntent);
			finish();
		}
	};

	/** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tags);

		// get the extras
		tagStateHobby = getIntent().getBooleanExtra("tagHobby", false);
		if(tagStateHobby) Log.e("tag", "hobby is true");

		// set the listeners for the buttons
		ToggleButton hobbyBtn = (ToggleButton) findViewById(R.id.toggle);
		hobbyBtn.setChecked(tagStateHobby);
		hobbyBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				if (isChecked)
				{
					// The toggle is enabled
					tagStateHobby = true;
				} else
				{
					// The toggle is disabled
					tagStateHobby = false;
				}
			}
		});

		Button saveBtn = (Button) findViewById(R.id.save_tags);
		saveBtn.setOnClickListener(mSaveListener);
	}
}