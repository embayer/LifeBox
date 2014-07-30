package de.neungrad.lifebox.service;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import de.neungrad.lifebox.db.LifeBoxContract;
import de.neungrad.lifebox.helper.Constants;

import java.util.ArrayList;

/**
 * IntentService to dynamically reload data from the database with filtered results.
 * @version 0.1 14.08.13
 * @autor Markus Bayer
 */
public class TimelineFilteredReloadService extends TimelineReloadService
{
	private final static String TAG = "TimelineFilteredReloadService";

	/** Creates an IntentService.  Invoked by your subclass's constructor. */
	public TimelineFilteredReloadService()
	{
		super();
	}

	/** Invoke the worker thread that runs independently from other application logic. */
	@Override
	protected void onHandleIntent(Intent intent)
	{
		// the result
		Intent localIntent = new Intent(Constants.BROADCAST_ACTION_RELOADRESPONSE);
		String[][] timelineEntryList = null;

		// get the extra
		int offset = intent.getIntExtra(Constants.OFFSET_EXTRA, 0);

		// generate the where clause
		Bundle args = intent.getBundleExtra(Constants.FILTER_BUNDLE_EXTRA);

		// request a set of filtered entries (referenced by _id)
		ArrayList<String> entryIdList = super.mDbHelper.selectFilteredEntryList
				(
						args.getString(Constants.ENTRY_TITLE_EXTRA),
						args.getString(Constants.FROM_DATE_EXTRA),
						args.getString(Constants.TO_DATE_EXTRA),
						args.getStringArrayList(Constants.TAG_ARRAY_EXTRA),
						args.getStringArrayList(Constants.HASHTAG_ARRAY_EXTRA),
						args.getStringArrayList(Constants.MEDIATYPE_ARRAY_EXTRA)
				);

		String whereClause = "";

		// if there are results
		if(!entryIdList.isEmpty())
		{
			// build a WHERE-clause from the _ids
			whereClause = " WHERE ";

			int entryAmount = entryIdList.size();
			int i = 1;

			for(String entryId : entryIdList)
			{
				whereClause += LifeBoxContract.Entries.DOT_ID + " == '" + entryId + "'";

				if(i < entryAmount)
				{
					whereClause += " OR ";
				}

				i++;
			}
		}
		else
		{
			// create a empty list to show TimelineFragment that there are no results
			timelineEntryList = new String[0][];
			Log.d(TAG, "No entries where fetched.");
		}

		// let the superclass select the right _ids
		String[][] dbEntryList = super.mDbHelper.selectEntrySet(whereClause, "DESC", super.ROWAMOUNT, offset);

		// if there where fetched filtered _ids ...
		if(null == timelineEntryList)
		{
			// ... and a actual result
			if(null != dbEntryList)
			{
				// load the data
				timelineEntryList = super.generateTimelineEntries(dbEntryList);
			}
			else
			{
				// create a empty list to show TimelineActivity that there are no results
				timelineEntryList = new String[0][];
			}

		}

		// pack the results in a bundle and return it to TimelineFragment
		Bundle bundle = new Bundle();
		bundle.putSerializable(Constants.TIMELINE_ENTRIES_ARRAY_EXTRA, timelineEntryList);

		localIntent.putExtra(Constants.TIMELINE_ENTRIES_BUNDLE_EXTRA, bundle);

		localIntent.addCategory(Intent.CATEGORY_DEFAULT);

		// broadcasts the Intent to receivers in this app
		LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
	}
}
