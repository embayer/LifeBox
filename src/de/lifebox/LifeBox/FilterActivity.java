package de.lifebox.LifeBox;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Activity to set up a filter in order to limit the timeline entry set by conditions.
 * @version 0.1 09.08.13
 * @autor Markus Bayer
 */
public class FilterActivity extends Activity
{
	private static final String TAG = "FilterActivity";

	// calendar object for date arithmetic
	private Calendar mCalendar = Calendar.getInstance();
	private final int THIS_YEAR = mCalendar.get(Calendar.YEAR);
	private final int THIS_MONTH = mCalendar.get(Calendar.MONTH);
	private final int THIS_DAY = mCalendar.get(Calendar.DAY_OF_MONTH);

	DbHelper mDbHelper;
	// the entry with the oldest and latest user_date
	private long firstEntryLong;
	private long lastEntryLong;

	private String FIRST_ENTRY;
	private String LAST_ENTRY;

	private boolean ownDate = false;

	// variables to hold the filter settings
	private ArrayList<String> mediaTypeList;
	private ArrayList<String> tagList;
	private ArrayList<String> hashtagList;

	private String fromDate;
	private String toDate;

	private String title;

	// ui elements
	private Button fromDateButton;
	private Button toDateButton;

	private ToggleButton allTimeTB;
	private ToggleButton thisYearTB;
	private ToggleButton thisMonthTB;
	private ToggleButton thisWeekTB;

	private ToggleButton imageTB;
	private ToggleButton videoTB;
	private ToggleButton musicTB;
	private ToggleButton movieTB;
	private ToggleButton textTB;

	private ImageButton tagsIB;
	private ImageButton hashtagsIB;

	private TextView tagsCountTV;
	private TextView hashtagsCountTV;

	private EditText titleET;

	DatePickerDialog.OnDateSetListener fromSetListener = new DatePickerDialog.OnDateSetListener()
	{

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
		{
			mCalendar.set(year, monthOfYear, dayOfMonth);
			Date d = mCalendar.getTime();
			String date = new SimpleDateFormat(Constants.DATEFORMAT).format(d);

			// set the selected date as button text
			fromDateButton.setText(date);

			mCalendar.clear();

			ownDate = true;

			allTimeTB.setChecked(false);
			thisYearTB.setChecked(false);
			thisMonthTB.setChecked(false);
			thisWeekTB.setChecked(false);
		}
	};

	DatePickerDialog.OnDateSetListener toSetListener = new DatePickerDialog.OnDateSetListener()
	{

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
		{
			mCalendar.set(year, monthOfYear, dayOfMonth);
			Date d = mCalendar.getTime();
			String date = new SimpleDateFormat(Constants.DATEFORMAT).format(d);

			// set the selected date as button text
			toDateButton.setText(date);

			mCalendar.clear();

			ownDate = true;

			allTimeTB.setChecked(false);
			thisYearTB.setChecked(false);
			thisMonthTB.setChecked(false);
			thisWeekTB.setChecked(false);
		}
	};

	// the buttonlisteners
	Button.OnClickListener fromDateListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			// initialize with the current button text
			String buttonText = (String) fromDateButton.getText();

			Timestamp ts = stringToTimestamp(buttonText);
			Date date = new Date(ts.getTime());

			int y = Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
			int m = Integer.parseInt(new SimpleDateFormat("MM").format(date));
			int d = Integer.parseInt(new SimpleDateFormat("dd").format(date));

			// the month is zero based -> -1
			DatePickerDialog dpd = new DatePickerDialog(FilterActivity.this, fromSetListener, y, m -1, d);
			dpd.show();
		}
	};

	Button.OnClickListener toDateListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			// initialize with the current button text
			String buttonText = (String) toDateButton.getText();

			Timestamp ts = stringToTimestamp(buttonText);
			Date date = new Date(ts.getTime());

			int y = Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
			int m = Integer.parseInt(new SimpleDateFormat("MM").format(date));
			int d = Integer.parseInt(new SimpleDateFormat("dd").format(date));

			// the month is zero based -> -1
			DatePickerDialog dpd = new DatePickerDialog(FilterActivity.this, toSetListener, y, m -1, d);
			dpd.show();
		}
	};

	ToggleButton.OnCheckedChangeListener allTimeListener = new ToggleButton.OnCheckedChangeListener()
	{
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		{
			if(isChecked)
			{
				// set the buttons
				fromDateButton.setText(FIRST_ENTRY);
				toDateButton.setText(LAST_ENTRY);

				// radiobutton group behavior (only one is selected)
				thisYearTB.setChecked(false);
				thisMonthTB.setChecked(false);
				thisWeekTB.setChecked(false);
			}
			// when the user clicks the button again
			else
			{
				if(!ownDate && !thisYearTB.isChecked() && !thisMonthTB.isChecked() && !thisWeekTB.isChecked())
				{
					// reset isChecked to true (do nothing)
					allTimeTB.setChecked(true);
				}
			}
		}
	};

	ToggleButton.OnCheckedChangeListener thisYearListener = new ToggleButton.OnCheckedChangeListener()
	{
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		{
			if(isChecked)
			{
				// determine 1st of this year
				Calendar from = Calendar.getInstance();
				from.set(THIS_YEAR, 0, 1);

				// determine last day of this year
				Calendar to = Calendar.getInstance();
				to.set(THIS_YEAR, 11, 31);

				// set the dates
				setDates(from, to);

				allTimeTB.setChecked(false);
				thisMonthTB.setChecked(false);
				thisWeekTB.setChecked(false);
			}
			else
			{
				if(!ownDate && !allTimeTB.isChecked() && !thisMonthTB.isChecked() && !thisWeekTB.isChecked())
				{
					thisYearTB.setChecked(true);
				}
			}
		}
	};

	ToggleButton.OnCheckedChangeListener thisMonthListener = new ToggleButton.OnCheckedChangeListener()
	{
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		{
			if(isChecked)
			{
				// determine 1st of this month
				Calendar from = Calendar.getInstance();
				from.set(THIS_YEAR, THIS_MONTH, 1);

				// determine last day of this month
				Calendar to = Calendar.getInstance();
				to.set(THIS_YEAR, THIS_MONTH, daysOfMonth(THIS_MONTH));

				// set the dates
				setDates(from, to);

				allTimeTB.setChecked(false);
				thisYearTB.setChecked(false);
				thisWeekTB.setChecked(false);
			}
			else
			{
				if(!ownDate && !allTimeTB.isChecked() && !thisYearTB.isChecked() && !thisWeekTB.isChecked())
				{
					thisMonthTB.setChecked(true);
				}
			}
		}
	};

	ToggleButton.OnCheckedChangeListener thisWeekListener = new ToggleButton.OnCheckedChangeListener()
	{
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		{
			if(isChecked)
			{
				// determine 1st day of this week
				Calendar from = Calendar.getInstance();

				// set the first day of a week
				Calendar firstDayOfWeek = Calendar.getInstance();
				firstDayOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

				from.set(THIS_YEAR, THIS_MONTH, firstDayOfWeek.get(Calendar.DATE));

				// determine last day of this week
				Calendar to = Calendar.getInstance();
				to.setFirstDayOfWeek(1);
				to.set(THIS_YEAR, THIS_MONTH, firstDayOfWeek.get(Calendar.DATE) +6);

				// set the dates
				setDates(from, to);

				allTimeTB.setChecked(false);
				thisYearTB.setChecked(false);
				thisMonthTB.setChecked(false);
			}
			else
			{
				if(!ownDate && !allTimeTB.isChecked() && !thisYearTB.isChecked() && !thisMonthTB.isChecked())
				{
					thisWeekTB.setChecked(true);
				}
			}
		}
	};

	Button.OnClickListener imageListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
		}
	};

	Button.OnClickListener videoListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
		}
	};

	Button.OnClickListener musicListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
		}
	};

	Button.OnClickListener movieListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
		}
	};

	Button.OnClickListener textListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
		}
	};

	Button.OnClickListener tagsListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent(getBaseContext(), TagsActivity.class);

			intent.putStringArrayListExtra(Constants.TAG_ARRAY_EXTRA, tagList);

			startActivityForResult(intent, Constants.ACTION_GATHER_TAGS);
		}
	};

	Button.OnClickListener hashtagsListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent(getBaseContext(), FilterHashtagsActivity.class);

			intent.putStringArrayListExtra(Constants.HASHTAG_ARRAY_EXTRA, hashtagList);

			startActivityForResult(intent, Constants.ACTION_GATHER_HASHTAGS);
		}
	};

	Button.OnClickListener saveListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			// get the date strings from the buttons
			String fromExtra = String.valueOf(stringToTimestamp(fromDateButton.getText().toString()).getTime());
			Log.e("timestmp from",  fromDateButton.getText().toString()+" "+dateStrToTimeStr(fromDateButton.getText().toString()));
			String toExtra = String.valueOf(stringToTimestamp(toDateButton.getText().toString()).getTime());
			Log.e("timestmp to", toDateButton.getText().toString()+" "+dateStrToTimeStr(toDateButton.getText().toString()));

			// get the title from the edit text
			String titleExtra =  titleET.getText().toString();

			Intent intent = new Intent(getBaseContext(), MainActivity.class);
			intent.putExtra(Constants.CALLER_EXTRA, Constants.CALLER_FILTER_ACTIVITY);
			intent.putStringArrayListExtra(Constants.TAG_ARRAY_EXTRA, tagList);
			intent.putStringArrayListExtra(Constants.HASHTAG_ARRAY_EXTRA, hashtagList);
			intent.putStringArrayListExtra(Constants.MEDIATYPE_ARRAY_EXTRA, mediaTypeList);
			intent.putExtra(Constants.FROM_DATE_EXTRA, fromExtra);
			intent.putExtra(Constants.TO_DATE_EXTRA, toExtra);
			intent.putExtra(Constants.ENTRY_TITLE_EXTRA, titleExtra);

			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			startActivity(intent);
		}
	};


	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filterform);

		mDbHelper = new DbHelper(this);
		// the entry with the oldest and latest user_date
		lastEntryLong = mDbHelper.selectLastUserDate();
		firstEntryLong = mDbHelper.selectFirstUserDate();

		FIRST_ENTRY = timestampToDateString(firstEntryLong);
		LAST_ENTRY = timestampToDateString(lastEntryLong);

		// clear the Calender object for further use
		mCalendar.clear();

		mediaTypeList = new ArrayList<String>();
		tagList = new ArrayList<String>();
		hashtagList = new ArrayList<String>();

		// initialize the ui elements
		// the title bar
		titleET = (EditText) findViewById(R.id.filterform_in_title);
		// the time constant buttons
		allTimeTB = (ToggleButton) findViewById(R.id.filterform_alltime_button);
		// allTimeTB is default
		allTimeTB.setChecked(true);
		allTimeTB.setOnCheckedChangeListener(allTimeListener);
		thisYearTB = (ToggleButton) findViewById(R.id.filterform_year_button);
		thisYearTB.setText(String.valueOf(THIS_YEAR));
		thisYearTB.setTextOff(String.valueOf(THIS_YEAR));
		thisYearTB.setTextOn(String.valueOf(THIS_YEAR));
		thisYearTB.setOnCheckedChangeListener(thisYearListener);
		thisMonthTB = (ToggleButton) findViewById(R.id.filterform_month_button);
		thisMonthTB.setOnCheckedChangeListener(thisMonthListener);
		thisWeekTB = (ToggleButton) findViewById(R.id.filterform_week_button);
		thisWeekTB.setOnCheckedChangeListener(thisWeekListener);

		// the datepicker dialog buttons
		fromDateButton = (Button) findViewById(R.id.filterform_fromdate_button);
		fromDateButton.setText(FIRST_ENTRY);
		fromDateButton.setOnClickListener(fromDateListener);
		toDateButton = (Button) findViewById(R.id.filterform_todate_button);
		toDateButton.setText(LAST_ENTRY);
		toDateButton.setOnClickListener(toDateListener);

		// the tag buttons
		tagsIB = (ImageButton) findViewById(R.id.filterform_tags_imagebutton);
		tagsIB.setOnClickListener(tagsListener);
		hashtagsIB = (ImageButton) findViewById(R.id.filterform_hashtags_imagebutton);
		hashtagsIB.setOnClickListener(hashtagsListener);

		// the counters
		tagsCountTV = (TextView) findViewById(R.id.filterform_textview_tagscount);
		hashtagsCountTV = (TextView) findViewById(R.id.filterform_textview_hashtagscount);

		// the mediatype toggle buttons
		imageTB = (ToggleButton) findViewById(R.id.filterform_image_togglebutton);
		setupButton(imageTB, Constants.TYPE_IMAGE_FILE);

		videoTB = (ToggleButton) findViewById(R.id.filterform_video_togglebutton);
		setupButton(videoTB, Constants.TYPE_VIDEO_FILE);

		musicTB = (ToggleButton) findViewById(R.id.filterform_music_togglebutton);
		setupButton(musicTB, Constants.TYPE_MUSIC);

		movieTB = (ToggleButton) findViewById(R.id.filterform_movie_togglebutton);
		setupButton(movieTB, Constants.TYPE_MOVIE);

		textTB = (ToggleButton) findViewById(R.id.filterform_text_togglebutton);
		setupButton(textTB, Constants.TYPE_TEXT);

		// the save button
		Button saveBtn = (Button) findViewById(R.id.filterform_save_meta_data);
		saveBtn.setOnClickListener(saveListener);
	}

	/**
	 * Called when the launched activity returns
	 * with the requestCode, the resultCode, and any additional data from it.
	 */
	@Override
	public void onActivityResult(final int requestCode, final int resultCode, final Intent data)
	{
		// decide by request code what to do
		switch(requestCode)
		{
			case Constants.ACTION_GATHER_TAGS:
				if(resultCode == Activity.RESULT_OK)
				{
					// get the extra
					tagList = data.getStringArrayListExtra(Constants.TAG_ARRAY_EXTRA);

					// set the counter
					if(!tagList.isEmpty())
					{
						// update the counter
						tagsCountTV.setText(String.valueOf(tagList.size()));
						// 'toggle' the button
						tagsIB.setBackgroundResource(R.drawable.button_tag);
					}
				}
				break;

			case Constants.ACTION_GATHER_HASHTAGS:
				if(resultCode == Activity.RESULT_OK)
				{
					if(data.hasExtra(Constants.HASHTAG_ARRAY_EXTRA))
					{
						// get the extras
						hashtagList = data.getStringArrayListExtra(Constants.HASHTAG_ARRAY_EXTRA);

						// set the counter
						if(!hashtagList.isEmpty())
						{
							// update the counter
							hashtagsCountTV.setText(String.valueOf(hashtagList.size()));
							// 'toggle' the button
							hashtagsIB.setBackgroundResource(R.drawable.button_hashtag);
						}
					}
				}
				break;

			default:
				break;
		}
	}

	/**
	 * Binds a String to a button state.
	 * @param state (boolean) the current button state
	 * @param tag (String) the String to bind.
	 */
	private void bindStringToButton(boolean state, String tag)
	{
		if (state == true)
		{
			// The tag is enabled
			if(!mediaTypeList.contains(tag))
			{
				mediaTypeList.add(tag);
			}
		}
		else
		{
			// The tag is disabled
			if(mediaTypeList.contains(tag))
			{
				mediaTypeList.remove(tag);
			}
		}
	}

	/**
	 * Initialize the Buttons fromDateButton and toDateButton to the given values.
	 * @param fromDate (Calendar) the beginning
	 * @param toDate (Calendar) the ending
	 * @return true if there are entries in this interval
	 */
	private void setDates(Calendar fromDate, Calendar toDate)
	{
		Date first = fromDate.getTime();
		String from = new SimpleDateFormat(Constants.DATEFORMAT).format(first);

		Date second = toDate.getTime();
		String to = new SimpleDateFormat(Constants.DATEFORMAT).format(second);

		// set the buttons to the dates
		fromDateButton.setText(from);
		toDateButton.setText(to);
	}

	/**
	 * Setup a togglebutton.
	 * @param button (ToggleButton) the button to setup.
	 * @param value (String) the value representing the buttonstate
	 */
	private void setupButton(ToggleButton button, final String value)
	{
		if(mediaTypeList.contains(value))
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

	/**
	 * Parses a String and tries to get the timestamp from.
	 * @param dateString (String) String in the format that is specified in Constants.DATEFORMAT
	 * @return (Timestamp) of the parsed String
	 */
	private Timestamp stringToTimestamp(String dateString)
	{

		SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATEFORMAT);

		Date parsedDate = null;
		try
		{
			parsedDate = sdf.parse(dateString);
		}
		catch (ParseException e)
		{
			Log.e("parse date error", e.getMessage());
		}

		Timestamp timestamp = new Timestamp(parsedDate.getTime());

		return timestamp;
	}

	/**
	 * Parses a String and tries to get the timestamp from.
	 * @param dateString (String) String in the format that is specified in Constants.DATEFORMAT
	 * @return (String) the unix timestamp as string
	 */
	private String dateStrToTimeStr(String dateString)
	{

		SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATEFORMAT);

		Date parsedDate = null;
		try
		{
			parsedDate = sdf.parse(dateString);
		}
		catch (ParseException e)
		{
			Log.e("parse date error", e.getMessage());
		}

		Timestamp timestamp = new Timestamp(parsedDate.getTime());

		// getTime() returns milliseconds -> / 1000, datestring = date at 22:00 so add to hours in seconds = 7200
		return String.valueOf(timestamp.getTime() / 1000 + 7200);
	}

	/**
	 * Converts a timestamp given as long to a date string.
	 * @return (String) the date
	 */
	private String timestampToDateString(long longTimestamp)
	{
		Timestamp timestamp = new Timestamp(longTimestamp);
		Date date = new Date(timestamp.getTime());

		return new SimpleDateFormat(Constants.DATEFORMAT).format(date);
	}

	/**
	 * Set the fromDate and toDate to default.
	 */
	private void setDatesDefault()
	{
		fromDate = timestampToDateString(firstEntryLong);
		toDate = timestampToDateString(lastEntryLong);
	}

	/**
	 * Gets the amount of days in a month.
	 * @param month (int) the requested month
	 * @return (int) the amount of days in this month
	 */
	private int daysOfMonth(int month)
	{
		// create a calendar object and set year and moth
		Calendar mycal = new GregorianCalendar(1999, month, 1);

		// get the number of days in that month
		return mycal.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
}