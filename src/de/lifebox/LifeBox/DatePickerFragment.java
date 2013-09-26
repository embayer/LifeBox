package de.lifebox.LifeBox;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Fragment-Dialog that lets the user select a time
 * !minSdkVersion = 11
 * @version 0.1 27.06.13
 * @autor Markus Bayer
 */
public class DatePickerFragment extends DialogFragment
		implements DatePickerDialog.OnDateSetListener
{
	private Calendar c;

	private int hour;
	private int minute;

	private int year;
	private int month;
	private int day;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// Use the current date as the default date in the picker
		c = Calendar.getInstance();

		hour = c.get(Calendar.HOUR_OF_DAY);
		minute = c.get(Calendar.MINUTE);

		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DATE);

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day)
	{
		// calculate the date
		c.set(year, month, day, hour, minute);
		Date d = c.getTime();
		String date = new SimpleDateFormat(Constants.SQL_DATEFORMAT).format(d);

		// set the selected date as button text
		Button mDatePickerBtn = (Button) getActivity().findViewById(R.id.datepicker);
		mDatePickerBtn.setText(date);
	}
}
