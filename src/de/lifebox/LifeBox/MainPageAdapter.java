package de.lifebox.LifeBox;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Helper class which holds the ArrayList of fragments.
 * @author Markus Bayer
 * @version 0.1 12.06.2013
 */
public class MainPageAdapter extends FragmentPagerAdapter
{
	private List<Fragment> fragments;		//ArrayList of fragments

	// constructor
	public MainPageAdapter(FragmentManager fm, List<Fragment> fragments)
	{
		super(fm);
		this.fragments = fragments;
	}

	/**
	 * Getter for fragment items
	 * @param position the position in the ArrayList
	 * @return fragment at the specified position
	 */
	@Override
	public Fragment getItem(int position)
	{
		return this.fragments.get(position);
	}

	/**
	 * Getter for the ArrayList size
	 * @return size of the ArrayList
	 */
	@Override
	public int getCount()
	{
		return this.fragments.size();
	}
}
