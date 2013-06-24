package de.lifebox.LifeBox;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TimePicker;

/**
 * Activity representing the form for getting the media's meta information
 * @autor Markus Bayer
 * @version 0.1 18.06.13
 */
public class MetaForm extends FragmentActivity
{
	private TimePicker timepicker;

	/** Executed when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.metaform);
	}
}