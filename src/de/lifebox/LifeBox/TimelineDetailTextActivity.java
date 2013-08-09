package de.lifebox.LifeBox;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Detailed view for entries of the type text.
 * @version 0.1 06.08.13
 * @autor Markus Bayer
 */
public class TimelineDetailTextActivity extends TimelineDetailActivity
{
	private final static String TAG = "TimelineDetailTextActivity";

	private DbHelper mDbHelper;

	// ui elements
	// the text field for the text
	private TextView textTV;

	// the textstring
	String text;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// get the text from the database
		mDbHelper = new DbHelper(getBaseContext());

		text = mDbHelper.selectSingleField
				(
						LifeBoxContract.Text.TABLE_NAME,
						new String[] {LifeBoxContract.Text._ID, LifeBoxContract.Text.COLUMN_NAME_TEXT},
						String.valueOf(super.getEntry().getMediaId()),
						true
				);

		// find the layout file for the specific part
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View childLayout = inflater.inflate(R.layout.timelinedetailtext, (ViewGroup) findViewById(R.layout.timelinedetail));

		// add it to the container
		super.getSpecificLL().addView(childLayout);

		// set the text
		textTV = (TextView) childLayout.findViewById(R.id.textdetail_text_textview);
		textTV.setText(text);
	}
}