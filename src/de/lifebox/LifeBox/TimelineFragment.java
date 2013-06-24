package de.lifebox.LifeBox;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment for showing entries in a timeline.
 * @author Markus Bayer
 * @version 0.1 12.06.2013
 */
public class TimelineFragment extends Fragment
{
	/**
	 * Constructor
	 * @return TimeLineFragment instance
	 */
	public static final TimelineFragment newInstance()
	{
		TimelineFragment f = new TimelineFragment();
     	Bundle bdl = new Bundle(1);
     	f.setArguments(bdl);

     	return f;
     }

	/** Called when the fragment is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
        View view = inflater.inflate(R.layout.timeline, container, false);

		return view;

	}

}
