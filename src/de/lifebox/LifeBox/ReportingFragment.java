package de.lifebox.LifeBox;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Fragment for reporting user statistics.
 * @author Markus Bayer
 * @version 0.1 12.06.2013
 */
public class ReportingFragment extends Fragment
{
	DbHelper mDbHelper;

	/**
	 * Constructor
	 * @return ReportingFragment instance
	 */
	public static final ReportingFragment newInstance()
	{
		ReportingFragment f = new ReportingFragment();
        Bundle bdl = new Bundle(1);
		f.setArguments(bdl);

		return f;
	}

	/** Called when the fragment is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// initialize the DbHelper
		mDbHelper = new DbHelper(getActivity());
	}

	/** Called when the fragment is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.reporting, container, false);


		// initialize the ui elements
		TextView entriesTotal = (TextView) view.findViewById(R.id.reporting_entries_total_value);
		entriesTotal.setText(mDbHelper.selectCountEntries());
		return view;
	}
}
