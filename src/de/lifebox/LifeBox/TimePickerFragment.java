package de.lifebox.LifeBox;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Fragment-Dialog that lets the user select a time
 * !minSdkVersion = 11
 * @version 0.1 27.06.13
 * @autor Markus Bayer
 */
public class TimePickerFragment extends DialogFragment
		implements TimePickerDialog.OnTimeSetListener
{
	private Calendar c;
	private int hour;
	private int minute;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// use the current time as the default values for the picker
		c = Calendar.getInstance();
		hour = c.get(Calendar.HOUR_OF_DAY);
		minute = c.get(Calendar.MINUTE);

		// create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, hour, minute,
				DateFormat.is24HourFormat(getActivity()));
	}

	public void onTimeSet(TimePicker view, int hourOfDay, int minute)
	{
		// set the selected time as button text
		Button mTimePickerBtn = (Button) getActivity().findViewById(R.id.timepicker);
		mTimePickerBtn.setText(hourOfDay  + ":" + minute);
	}
}
