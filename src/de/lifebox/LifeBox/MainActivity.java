package de.lifebox.LifeBox;

import android.app.ActionBar;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;


/**
 * Main activity, holding the view fragments.
 * @author Markus Bayer
 * @version 0.1 12.06.2013
 */
public class MainActivity extends FragmentActivity
{
	MainPageAdapter mPageAdapter;		// managing the different views
	ViewPager mViewPager;               // switch between views through swiping

	static final int CAPTURE_IMAGE = 1;

	private static final int ACTION_TAKE_PHOTO = 1;

	private static Uri fileUri;

	/** Executed when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// List containing the fragments
		List<Fragment> fragments = getFragments();
		mPageAdapter = new MainPageAdapter(getSupportFragmentManager(), fragments);
		mViewPager = (ViewPager)findViewById(R.id.pager);
		mViewPager.setAdapter(mPageAdapter);
		mViewPager.setOnPageChangeListener(
				new ViewPager.SimpleOnPageChangeListener()
				{
					@Override
					public void onPageSelected(int position)
					{
						// When swiping between pages, select the corresponding tab.
						getActionBar().setSelectedNavigationItem(position);
					}
				});

		final ActionBar actionBar = getActionBar();
		// put tabs into actionbar
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// called when the user interact with tabs
		ActionBar.TabListener tabListener = new ActionBar.TabListener()
		{
			/** Called when a tab enters the selected state. */
			@Override
			public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft)
			{
				// show the given tab
				// When the tab is selected, switch to the corresponding page in the ViewPager.
				mViewPager.setCurrentItem(tab.getPosition());
			}

			/** Called when a tab exits the selected state. */
			@Override
			public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft)
			{
				// unused method
			}

			/** Called when a tab that is already selected is chosen again by the user. */
			@Override
			public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft)
			{
				// unused method
			}
		};

		// add the tabs
		actionBar.addTab(actionBar.newTab().setText(getString(R.string.tab_reporting)).setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText(getString(R.string.tab_input)).setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText(getString(R.string.tab_timeline)).setTabListener(tabListener));

		mViewPager.setCurrentItem(1, true);						// display the second tab on startup
	}

	/**
	 * Getter for the fragments
	 * @return ArrayList of fragments
	 */
	private List<Fragment> getFragments()
	{
		List<Fragment> fList = new ArrayList<Fragment>();
		fList.add(ReportingFragment.newInstance());
		fList.add(SelectTypeFragment.newInstance());
		fList.add(TimelineFragment.newInstance());

		return fList;
	}
}