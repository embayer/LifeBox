package de.lifebox.LifeBox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import java.util.ArrayList;

/**
 * Activity to select pre-defined tags.
 * @version 0.1 28.06.13
 * @autor Markus Bayer
 */
public class TagsActivity extends Activity
{
	//todo delete unneeded code
	// tag button states
	private ArrayList<String> tagList;

	// listener for the save button
	Button.OnClickListener mSaveListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			saveTags();
		}
	};

	/** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tags);

		// get the extras if there are ones
		Intent intent = getIntent();

		intent.getStringArrayExtra(Constants.TAG_ARRAY_EXTRA);

		// get the extra
		if(getIntent().hasExtra(Constants.TAG_ARRAY_EXTRA))
		{
			tagList = getIntent().getStringArrayListExtra(Constants.TAG_ARRAY_EXTRA);
		}
		else
		{
			tagList = new ArrayList<String>();
		}

		// set the listeners for the buttons and bind the states to the tags within tagList
		ToggleButton smiley1Btn = (ToggleButton) findViewById(R.id.tb_smiley1);
		setupButton(smiley1Btn, Constants.TAG_SMILEY1_EXTRA);

		ToggleButton smiley2Btn = (ToggleButton) findViewById(R.id.tb_smiley2);
		setupButton(smiley2Btn, Constants.TAG_SMILEY2_EXTRA);

		ToggleButton smiley3Btn = (ToggleButton) findViewById(R.id.tb_smiley3);
		setupButton(smiley3Btn, Constants.TAG_SMILEY3_EXTRA);

		ToggleButton smiley4Btn = (ToggleButton) findViewById(R.id.tb_smiley4);
		setupButton(smiley4Btn, Constants.TAG_SMILEY4_EXTRA);

		ToggleButton smiley5Btn = (ToggleButton) findViewById(R.id.tb_smiley5);
		setupButton(smiley5Btn, Constants.TAG_SMILEY5_EXTRA);

		ToggleButton smiley6Btn = (ToggleButton) findViewById(R.id.tb_smiley6);
		setupButton(smiley6Btn, Constants.TAG_SMILEY6_EXTRA);

		ToggleButton smiley7Btn = (ToggleButton) findViewById(R.id.tb_smiley7);
		setupButton(smiley7Btn, Constants.TAG_SMILEY7_EXTRA);

		ToggleButton smiley8Btn = (ToggleButton) findViewById(R.id.tb_smiley8);
		setupButton(smiley8Btn, Constants.TAG_SMILEY8_EXTRA);

		ToggleButton smiley9Btn = (ToggleButton) findViewById(R.id.tb_smiley9);
		setupButton(smiley9Btn, Constants.TAG_SMILEY9_EXTRA);

		ToggleButton smiley10Btn = (ToggleButton) findViewById(R.id.tb_smiley10);
		setupButton(smiley10Btn, Constants.TAG_SMILEY10_EXTRA);

		ToggleButton tag01Btn = (ToggleButton) findViewById(R.id.tb_love);
		setupButton(tag01Btn, Constants.TAG_LOVE_EXTRA);

		ToggleButton tag02Btn = (ToggleButton) findViewById(R.id.tb_star);
		setupButton(tag02Btn, Constants.TAG_STAR_EXTRA);

		ToggleButton tag03Btn = (ToggleButton) findViewById(R.id.tb_dislike);
		setupButton(tag03Btn, Constants.TAG_DISLIKE_EXTRA);

		ToggleButton tag04Btn = (ToggleButton) findViewById(R.id.tb_achievement);
		setupButton(tag04Btn, Constants.TAG_ACHIEVEMENT_EXTRA);

		ToggleButton tag05Btn = (ToggleButton) findViewById(R.id.tb_work);
		setupButton(tag05Btn, Constants.TAG_WORK_EXTRA);

		ToggleButton tag06Btn = (ToggleButton) findViewById(R.id.tb_family);
		setupButton(tag06Btn, Constants.TAG_FAMILY_EXTRA);

		ToggleButton tag07Btn = (ToggleButton) findViewById(R.id.tb_child);
		setupButton(tag07Btn, Constants.TAG_CHILD_EXTRA);

		ToggleButton tag08Btn = (ToggleButton) findViewById(R.id.tb_pet);
		setupButton(tag08Btn, Constants.TAG_PET_EXTRA);

		ToggleButton tag09Btn = (ToggleButton) findViewById(R.id.tb_friends);
		setupButton(tag09Btn, Constants.TAG_FRIENDS_EXTRA);

		ToggleButton tag10Btn = (ToggleButton) findViewById(R.id.tb_party);
		setupButton(tag10Btn, Constants.TAG_PARTY_EXTRA);

		ToggleButton tag11Btn = (ToggleButton) findViewById(R.id.tb_outdoor);
		setupButton(tag11Btn, Constants.TAG_OUTDOOR_EXTRA);

		ToggleButton tag12Btn = (ToggleButton) findViewById(R.id.tb_home);
		setupButton(tag12Btn, Constants.TAG_HOME_EXTRA);

		ToggleButton tag13Btn = (ToggleButton) findViewById(R.id.tb_trip);
		setupButton(tag13Btn, Constants.TAG_TRIP_EXTRA);

		ToggleButton tag14Btn = (ToggleButton) findViewById(R.id.tb_travel);
		setupButton(tag14Btn, Constants.TAG_TRAVEL_EXTRA);

		ToggleButton tag15Btn = (ToggleButton) findViewById(R.id.tb_event);
		setupButton(tag15Btn, Constants.TAG_EVENT_EXTRA);

		ToggleButton tag16Btn = (ToggleButton) findViewById(R.id.tb_hobby);
		setupButton(tag16Btn, Constants.TAG_HOBBY_EXTRA);

		ToggleButton tag17Btn = (ToggleButton) findViewById(R.id.tb_sport);
		setupButton(tag17Btn, Constants.TAG_SPORT_EXTRA);

		ToggleButton tag18Btn = (ToggleButton) findViewById(R.id.tb_food);
		setupButton(tag18Btn, Constants.TAG_FOOD_EXTRA);

		ToggleButton tag19Btn = (ToggleButton) findViewById(R.id.tb_cloth);
		setupButton(tag19Btn, Constants.TAG_CLOTH_EXTRA);

		ToggleButton tag20Btn = (ToggleButton) findViewById(R.id.tb_shopping);
		setupButton(tag20Btn, Constants.TAG_SHOPPING_EXTRA);

		Button saveBtn = (Button) findViewById(R.id.save_tags);
		saveBtn.setOnClickListener(mSaveListener);
	}

	/**
	 * Binds a String to a button state.
	 * @param state (boolean) the current button state
	 * @param tag (String) the String to bind.
	 */
	private void bindStringToButton(boolean state, String tag)
	{
		if(state == true)
		{
			// enable tag
			if(!tagList.contains(tag))
			{
				tagList.add(tag);
			}
		}
		else
		{
			// disabled tag
			if(tagList.contains(tag))
			{
				tagList.remove(tag);
			}
		}
	}

	/**
	 * Setup a togglebutton.
	 * @param button (ToggleButton) the button to setup.
	 * @param value (String) the value representing the buttonstate
	 */
	private void setupButton(ToggleButton button, final String value)
	{
		if(tagList.contains(value))
		{
			button.setChecked(true);
		}

		button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				bindStringToButton(isChecked, value);
			}
		});
	}

	/** Sends a list containing all selected hashtags back to the caller. */
	private void saveTags()
	{
		Intent returnIntent = new Intent(getBaseContext(), MetaFormActivity.class);

		returnIntent.putStringArrayListExtra(Constants.TAG_ARRAY_EXTRA, tagList);

		setResult(RESULT_OK, returnIntent);

		finish();
	}
}