package de.lifebox.LifeBox;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

/**
 * Activity for selecting what type of media should be used to generate a entry from.
 * Child activity of MainActivity (SelectType fragment).
 * @author Markus Bayer
 * @version 0.1 12.06.2013
 */
public class SelectFileTypeActivity extends Activity
{
	/** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectfiletype);

		// add up navigation to the parent-activity
		getActionBar().setDisplayHomeAsUpEnabled(true);
    }

	/** Called when the actionbar symbol is clicked.
	 * Navigate back to the parent-activity.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) {
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}