package de.neungrad.lifebox.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import de.neungrad.lifebox.R;
import de.neungrad.lifebox.helper.Constants;
import de.neungrad.lifebox.service.BackupDbService;

import java.util.ArrayList;
import java.util.List;


/**
 * Main activity, holding the view fragments:
 * 	-ReportingFragment
 * 	-InputFragment
 * 	-TimelineFragment
 * @author Markus Bayer
 * @version 0.1 12.06.2013
 */
public class MainActivity extends FragmentActivity
{
	String TAG = "MainActivity";

	MainPageAdapter mPageAdapter;		// managing the different views
	ViewPager mViewPager;               // switch between views through swiping

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

		// ActionBar object
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

			// unused methods
			/** Called when a tab exits the selected state. */
			@Override
			public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft){}

			/** Called when a tab that is already selected is chosen again by the user. */
			@Override
			public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft){}
		};

		// add the tabs
		actionBar.addTab(actionBar.newTab().setText(getString(R.string.tab_reporting)).setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText(getString(R.string.tab_input)).setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText(getString(R.string.tab_timeline)).setTabListener(tabListener));

	    // get the intent
		Intent intent = getIntent();
		int callerId = intent.getIntExtra(Constants.CALLER_EXTRA, 100);

		// show the correct fragment by caller
		// fresh start
		if(callerId == Constants.CALLER_SIGN_IN_ACTIVITY)
		{
			// display the inputmethod on startup
			mViewPager.setCurrentItem(1, true);
		}
		// new entry was added or a entry detail was viewed
		else if(callerId == Constants.CALLER_META_FORM_ACTIVITY
				|| callerId == Constants.CALLER_TIMELINE_DETAIL_ACTIVITY)
		{
			// display the timeline
			mViewPager.setCurrentItem(2, true);
		}
		//
		else if(callerId == Constants.CALLER_FILTER_ACTIVITY)
		{
			// get the extras and put them as bundle
			Bundle args = new Bundle();
			args.putString(Constants.CALLER_EXTRA, intent.getStringExtra(Constants.CALLER_EXTRA));
			args.putStringArrayList(Constants.TAG_ARRAY_EXTRA, intent.getStringArrayListExtra(Constants.TAG_ARRAY_EXTRA));
			args.putStringArrayList(Constants.HASHTAG_ARRAY_EXTRA, intent.getStringArrayListExtra(Constants.HASHTAG_ARRAY_EXTRA));
			args.putStringArrayList(Constants.MEDIATYPE_ARRAY_EXTRA, intent.getStringArrayListExtra(Constants.MEDIATYPE_ARRAY_EXTRA));
			args.putString(Constants.FROM_DATE_EXTRA, intent.getStringExtra(Constants.FROM_DATE_EXTRA));
			args.putString(Constants.TO_DATE_EXTRA, intent.getStringExtra(Constants.TO_DATE_EXTRA));
			args.putString(Constants.ENTRY_TITLE_EXTRA, intent.getStringExtra(Constants.ENTRY_TITLE_EXTRA));

			// set the bundle to TimelineFragment
			fragments.get(2).setArguments(args);

			// display the timeline
			mViewPager.setCurrentItem(2, true);
		}
		// default case
		else if(callerId == 100)
		{
			mViewPager.setCurrentItem(1, true);
			Log.e(TAG, "No caller");
		}
	}

	/**
	 * Getter for the fragments
	 * @return ArrayList of fragments
	 */
	private List<Fragment> getFragments()
	{
		List<Fragment> fList = new ArrayList<Fragment>();
		fList.add(ReportingFragment.newInstance());
		fList.add(InputFragment.newInstance());
		fList.add(TimelineFragment.newInstance());

		return fList;
	}

	/**
	 * Called when the activity is first created.
	 * Builds the action bar menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		return super.onCreateOptionsMenu(menu);
	}

	/** Called when the a item on the action bar is pressed. */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// handle presses on the action bar items
		switch (item.getItemId())
		{
			case R.id.action_filter:
				// call FilterActivity
				Intent filterIntent = new Intent(this, FilterActivity.class);
				startActivity(filterIntent);
				return true;
			case R.id.action_backupdb:
				// call BackupDbService in order to backup the database
				Intent backupIntent = new Intent(this, BackupDbService.class);
				startService(backupIntent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}