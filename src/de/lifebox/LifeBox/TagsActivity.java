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

	private boolean tagSmiley1 = false;
	private boolean tagSmiley2 = false;
	private boolean tagSmiley3 = false;
	private boolean tagSmiley4 = false;
	private boolean tagSmiley5 = false;

	private boolean tagLove = false;
	private boolean tagStar = false;
	private boolean tagAchievement = false;
	private boolean tagWork = false;
	private boolean tagFamily = false;
	private boolean tagChild = false;
	private boolean tagPet = false;
	private boolean tagFriends = false;
	private boolean tagParty = false;
	private boolean tagOutdoor = false;
	private boolean tagHome = false;
	private boolean tagTrip = false;
	private boolean tagTravel = false;
	private boolean tagEvent = false;
	private boolean tagHobby = false;
	private boolean tagSport = false;
	private boolean tagFood = false;
	private boolean tagCloth = false;
	private boolean tagShopping = false;

	// the button listeners
//	Button.OnClickListener mTagListener = new Button.OnClickListener()
//	{
//		@Override
//		public void onClick(View v)
//		{
//			// bindStringToButoon the tag state
//			tagStateHobby = !tagStateHobby;
//		}
//	};

	// listener for the save button
	Button.OnClickListener mSaveListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			// set the extra
			Intent returnIntent = new Intent(getBaseContext(), MetaFormActivity.class);

			returnIntent.putStringArrayListExtra(Constants.TAG_ARRAY_EXTRA, tagList);

//			returnIntent.putExtra(Constants.TAG_SMILEY1_EXTRA, tagSmiley1);
//			returnIntent.putExtra(Constants.TAG_SMILEY2_EXTRA, tagSmiley2);
//			returnIntent.putExtra(Constants.TAG_SMILEY3_EXTRA, tagSmiley3);
//			returnIntent.putExtra(Constants.TAG_SMILEY4_EXTRA, tagSmiley4);
//			returnIntent.putExtra(Constants.TAG_SMILEY5_EXTRA, tagSmiley5);
//			returnIntent.putExtra(Constants.TAG_SMILEY6_EXTRA, tagSmiley6);
//			returnIntent.putExtra(Constants.TAG_SMILEY7_EXTRA, tagSmiley7);
//			returnIntent.putExtra(Constants.TAG_SMILEY8_EXTRA, tagSmiley8);
//			returnIntent.putExtra(Constants.TAG_SMILEY9_EXTRA, tagSmiley9);
//			returnIntent.putExtra(Constants.TAG_SMILEY10_EXTRA, tagSmiley10);
//
//			returnIntent.putExtra(Constants.TAG_LOVE_EXTRA);
//			returnIntent.putExtra(Constants.TAG_STAR_EXTRA);
//			returnIntent.putExtra(Constants.TAG_DISLIKE_EXTRA);
//			returnIntent.putExtra(Constants.TAG_ACHIEVEMENT_EXTRA);
//			returnIntent.putExtra(Constants.TAG_WORK_EXTRA);
//			returnIntent.putExtra(Constants.TAG_FAMILY_EXTRA);
//			returnIntent.putExtra(Constants.TAG_CHILD_EXTRA);
//			returnIntent.putExtra(Constants.TAG_PET_EXTRA);
//			returnIntent.putExtra(Constants.TAG_FRIENDS_EXTRA);
//			returnIntent.putExtra(Constants.TAG_PARTY_EXTRA);
//			returnIntent.putExtra(Constants.TAG_OUTDOOR_EXTRA);
//			returnIntent.putExtra(Constants.TAG_HOME_EXTRA);
//			returnIntent.putExtra(Constants.TAG_TRIP_EXTRA);
//			returnIntent.putExtra(Constants.TAG_TRAVEL_EXTRA);
//			returnIntent.putExtra(Constants.TAG_EVENT_EXTRA);
//			returnIntent.putExtra(Constants.TAG_HOBBY_EXTRA);
//			returnIntent.putExtra(Constants.TAG_SPORT_EXTRA);
//			returnIntent.putExtra(Constants.TAG_FOOD_EXTRA);
//			returnIntent.putExtra(Constants.TAG_CLOTH_EXTRA);
//			returnIntent.putExtra(Constants.TAG_SHOPPING_EXTRA);
	setResult(RESULT_OK, returnIntent);
			finish();
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
//
//		if(tagList.contains(Constants.TAG_SMILEY1_EXTRA))
//		{
//			smiley1Btn.setChecked(true);
//		}
//
//		smiley1Btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//		{
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//			{
//				bindStringToButoon(isChecked, Constants.TAG_SMILEY1_EXTRA);
//			}
//		});
//
//		ToggleButton smiley2Btn = (ToggleButton) findViewById(R.id.tb_smiley2);
//
//		if(tagList.contains(Constants.TAG_SMILEY2_EXTRA))
//		{
//			smiley2Btn.setChecked(true);
//		}
//
//		smiley2Btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//		{
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//			{
//				bindStringToButoon(isChecked, Constants.TAG_SMILEY2_EXTRA);
//			}
//		});
//
//		ToggleButton smiley3Btn = (ToggleButton) findViewById(R.id.tb_smiley3);
//
//		if(tagList.contains(Constants.TAG_SMILEY3_EXTRA))
//		{
//			smiley3Btn.setChecked(true);
//		}
//
//		smiley3Btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//		{
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//			{
//				bindStringToButoon(isChecked, Constants.TAG_SMILEY3_EXTRA);
//			}
//		});
//
//		ToggleButton smiley4Btn = (ToggleButton) findViewById(R.id.tb_smiley4);
//
//		if(tagList.contains(Constants.TAG_SMILEY4_EXTRA))
//		{
//			smiley4Btn.setChecked(true);
//		}
//
//		smiley4Btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//		{
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//			{
//				bindStringToButoon(isChecked, Constants.TAG_SMILEY4_EXTRA);
//			}
//		});
//
//		ToggleButton smiley5Btn = (ToggleButton) findViewById(R.id.tb_smiley5);
//
//		if(tagList.contains(Constants.TAG_SMILEY5_EXTRA))
//		{
//			smiley5Btn.setChecked(true);
//		}
//
//		smiley5Btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//		{
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//			{
//				bindStringToButoon(isChecked, Constants.TAG_SMILEY5_EXTRA);
//			}
//		});
//
//		ToggleButton smiley6Btn = (ToggleButton) findViewById(R.id.tb_smiley6);
//
//		if(tagList.contains(Constants.TAG_SMILEY6_EXTRA))
//		{
//			smiley6Btn.setChecked(true);
//		}
//
//		smiley6Btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//		{
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//			{
//				bindStringToButoon(isChecked, Constants.TAG_SMILEY6_EXTRA);
//			}
//		});
//
//		ToggleButton smiley7Btn = (ToggleButton) findViewById(R.id.tb_smiley7);
//
//		if(tagList.contains(Constants.TAG_SMILEY7_EXTRA))
//		{
//			smiley7Btn.setChecked(true);
//		}
//
//		smiley7Btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//		{
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//			{
//				bindStringToButoon(isChecked, Constants.TAG_SMILEY7_EXTRA);
//			}
//		});
//
//		ToggleButton smiley8Btn = (ToggleButton) findViewById(R.id.tb_smiley8);
//
//		if(tagList.contains(Constants.TAG_SMILEY8_EXTRA))
//		{
//			smiley8Btn.setChecked(true);
//		}
//
//		smiley8Btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//		{
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//			{
//				bindStringToButoon(isChecked, Constants.TAG_SMILEY8_EXTRA);
//			}
//		});
//
//		ToggleButton smiley9Btn = (ToggleButton) findViewById(R.id.tb_smiley9);
//
//		if(tagList.contains(Constants.TAG_SMILEY9_EXTRA))
//		{
//			smiley9Btn.setChecked(true);
//		}
//
//		smiley9Btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//		{
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//			{
//				bindStringToButoon(isChecked, Constants.TAG_SMILEY9_EXTRA);
//			}
//		});
//
//		ToggleButton smiley10Btn = (ToggleButton) findViewById(R.id.tb_smiley10);
//
//		if(tagList.contains(Constants.TAG_SMILEY10_EXTRA))
//		{
//			smiley10Btn.setChecked(true);
//		}
//
//		smiley10Btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//		{
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//			{
//				bindStringToButoon(isChecked, Constants.TAG_SMILEY10_EXTRA);
//			}
//		});
//
//		ToggleButton loveBtn = (ToggleButton) findViewById(R.id.tb_love);
//
//		if(tagList.contains(Constants.TAG_LOVE_EXTRA))
//		{
//			loveBtn.setChecked(true);
//		}
//
//		loveBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//		{
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//			{
//				bindStringToButoon(isChecked, Constants.TAG_LOVE_EXTRA);
//			}
//		});
//
//		ToggleButton starBtn = (ToggleButton) findViewById(R.id.tb_star);
//
//		if(tagList.contains(Constants.TAG_STAR_EXTRA))
//		{
//			starBtn.setChecked(true);
//		}
//
//		starBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//		{
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//			{
//				bindStringToButoon(isChecked, Constants.TAG_STAR_EXTRA);
//			}
//		});
//
//		ToggleButton dislikeBtn = (ToggleButton) findViewById(R.id.tb_dislike);
//
//		if(tagList.contains(Constants.TAG_DISLIKE_EXTRA))
//		{
//			dislikeBtn.setChecked(true);
//		}
//
//		dislikeBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//		{
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//			{
//				bindStringToButoon(isChecked, Constants.TAG_DISLIKE_EXTRA);
//			}
//		});
//
//		ToggleButton achievementBtn = (ToggleButton) findViewById(R.id.tb_achievement);
//
//		if(tagList.contains(Constants.TAG_ACHIEVEMENT_EXTRA))
//		{
//			achievementBtn.setChecked(true);
//		}
//
//		achievementBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//		{
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//			{
//				bindStringToButoon(isChecked, Constants.TAG_ACHIEVEMENT_EXTRA);
//			}
//		});
//
//		ToggleButton workBtn = (ToggleButton) findViewById(R.id.tb_work);
//
//		if(tagList.contains(Constants.TAG_WORK_EXTRA))
//		{
//			workBtn.setChecked(true);
//		}
//
//		workBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//		{
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//			{
//				bindStringToButoon(isChecked, Constants.TAG_WORK_EXTRA);
//			}
//		});
//
//		ToggleButton familyBtn = (ToggleButton) findViewById(R.id.tb_family);
//
//		if(tagList.contains(Constants.TAG_FAMILY_EXTRA))
//		{
//			familyBtn.setChecked(true);
//		}
//
//		familyBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//		{
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//			{
//				bindStringToButoon(isChecked, Constants.TAG_FAMILY_EXTRA);
//			}
//		});
//
//		ToggleButton childBtn = (ToggleButton) findViewById(R.id.tb_child);
//
//		if(tagList.contains(Constants.TAG_CHILD_EXTRA))
//		{
//			childBtn.setChecked(true);
//		}
//
//		childBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//		{
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//			{
//				bindStringToButoon(isChecked, Constants.TAG_CHILD_EXTRA);
//			}
//		});
//
//		ToggleButton petBtn = (ToggleButton) findViewById(R.id.tb_pet);
//
//		if(tagList.contains(Constants.TAG_PET_EXTRA))
//		{
//			petBtn.setChecked(true);
//		}
//
//		petBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//		{
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//			{
//				bindStringToButoon(isChecked, Constants.TAG_PET_EXTRA);
//			}
//		});
//
//		ToggleButton friendsBtn = (ToggleButton) findViewById(R.id.tb_friends);
//
//		if(tagList.contains(Constants.TAG_FRIENDS_EXTRA))
//		{
//			friendsBtn.setChecked(true);
//		}
//
//		friendsBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//		{
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//			{
//				bindStringToButoon(isChecked, Constants.TAG_FRIENDS_EXTRA);
//			}
//		});
//
//		ToggleButton partyBtn = (ToggleButton) findViewById(R.id.tb_party);
//
//		if(tagList.contains(Constants.TAG_PARTY_EXTRA))
//		{
//			partyBtn.setChecked(true);
//		}
//
//		partyBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//		{
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//			{
//				bindStringToButoon(isChecked, Constants.TAG_PARTY_EXTRA);
//			}
//		});
//
//		ToggleButton outdoorBtn = (ToggleButton) findViewById(R.id.tb_outdoor);
//
//		if(tagList.contains(Constants.TAG_OUTDOOR_EXTRA))
//		{
//			outdoorBtn.setChecked(true);
//		}
//
//		outdoorBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//		{
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//			{
//				bindStringToButoon(isChecked, Constants.TAG_OUTDOOR_EXTRA);
//			}
//		});
//
//		ToggleButton homeBtn = (ToggleButton) findViewById(R.id.tb_home);
//
//		if(tagList.contains(Constants.TAG_HOME_EXTRA))
//		{
//			homeBtn.setChecked(true);
//		}
//
//		homeBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//		{
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//			{
//				bindStringToButoon(isChecked, Constants.TAG_HOME_EXTRA);
//			}
//		});
//
//		ToggleButton tripBtn = (ToggleButton) findViewById(R.id.tb_trip);
//
//		if(tagList.contains(Constants.TAG_TRIP_EXTRA))
//		{
//			tripBtn.setChecked(true);
//		}
//
//		tripBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//		{
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//			{
//				bindStringToButoon(isChecked, Constants.TAG_TRIP_EXTRA);
//			}
//		});
//
//		ToggleButton travelBtn = (ToggleButton) findViewById(R.id.tb_travel);
//
//		if(tagList.contains(Constants.TAG_TRAVEL_EXTRA))
//		{
//			travelBtn.setChecked(true);
//		}
//
//		travelBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//		{
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//			{
//				bindStringToButoon(isChecked, Constants.TAG_TRAVEL_EXTRA);
//			}
//		});
//
//		ToggleButton eventBtn = (ToggleButton) findViewById(R.id.tb_event);
//
//		if(tagList.contains(Constants.TAG_EVENT_EXTRA))
//		{
//			eventBtn.setChecked(true);
//		}
//
//		eventBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//		{
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//			{
//				bindStringToButoon(isChecked, Constants.TAG_EVENT_EXTRA);
//			}
//		});
//
//		ToggleButton hobbyBtn = (ToggleButton) findViewById(R.id.tb_hobby);
//
//		if(tagList.contains(Constants.TAG_HOBBY_EXTRA))
//		{
//			hobbyBtn.setChecked(true);
//		}
//
//		hobbyBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//		{
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//			{
//				bindStringToButoon(isChecked, Constants.TAG_HOBBY_EXTRA);
//			}
//		});
//
//		ToggleButton sportBtn = (ToggleButton) findViewById(R.id.tb_sport);
//
//		if(tagList.contains(Constants.TAG_SPORT_EXTRA))
//		{
//			sportBtn.setChecked(true);
//		}
//
//		sportBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//		{
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//			{
//				bindStringToButoon(isChecked, Constants.TAG_SPORT_EXTRA);
//			}
//		});
//
//		ToggleButton foodBtn = (ToggleButton) findViewById(R.id.tb_food);
//
//		if(tagList.contains(Constants.TAG_FOOD_EXTRA))
//		{
//			foodBtn.setChecked(true);
//		}
//
//		foodBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//		{
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//			{
//				bindStringToButoon(isChecked, Constants.TAG_FOOD_EXTRA);
//			}
//		});
//
//		ToggleButton clothBtn = (ToggleButton) findViewById(R.id.tb_cloth);
//
//		if(tagList.contains(Constants.TAG_CLOTH_EXTRA))
//		{
//			clothBtn.setChecked(true);
//		}
//
//		clothBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//		{
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//			{
//				bindStringToButoon(isChecked, Constants.TAG_CLOTH_EXTRA);
//			}
//		});
//
//		ToggleButton shoppingBtn = (ToggleButton) findViewById(R.id.tb_shopping);
//
//		if(tagList.contains(Constants.TAG_SHOPPING_EXTRA))
//		{
//			shoppingBtn.setChecked(true);
//		}
//
//		shoppingBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//		{
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//			{
//				bindStringToButoon(isChecked, Constants.TAG_SHOPPING_EXTRA);
//			}
//		});

		Button saveBtn = (Button) findViewById(R.id.save_tags);
		saveBtn.setOnClickListener(mSaveListener);
	}

	/**
	 * Binds a String to a button state.
	 * @param state (boolean) the current button state
	 * @param tag (String) the String to bind.
	 */
	private void bindStringToButoon(boolean state, String tag)
	{
		if (state == true)
		{
			// The tag is enabled
			if(!tagList.contains(tag))
			{
				tagList.add(tag);
			}
		}
		else
		{
			// The tag is disabled
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
	public void setupButton(ToggleButton button, final String value)
	{
		if(tagList.contains(value))
		{
			button.setChecked(true);
		}

		button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				bindStringToButoon(isChecked, value);
			}
		});
	}
}