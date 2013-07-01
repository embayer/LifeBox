package de.lifebox.LifeBox;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateUtils;
import android.widget.Button;
import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
	private int year;
	private int month;
	private int day;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// Use the current date as the default date in the picker
		c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day)
	{
//		String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(year + "-" + month + "-" + day);
//
//		Button mDatePickerBtn = (Button) getActivity().findViewById(R.id.datepicker);
//		mDatePickerBtn.setText(timeStamp);

		// set the selected date as button text
		Button mDatePickerBtn = (Button) getActivity().findViewById(R.id.datepicker);
		mDatePickerBtn.setText(year  + "-" + month + "-" + day);
		//TODO provide other date formats
	}
}
