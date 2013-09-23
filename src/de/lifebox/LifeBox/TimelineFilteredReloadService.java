package de.lifebox.LifeBox;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

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
		// get the extra
		int offset = intent.getIntExtra(Constants.OFFSET_EXTRA, 0);

		// generate the where clause
		Bundle args = intent.getBundleExtra(Constants.FILTER_BUNDLE_EXTRA);

		ArrayList<String> entryIdList = super.mDbHelper.selectFilteredEntryList
				(
						args.getString(Constants.ENTRY_TITLE_EXTRA),
						args.getString(Constants.FROM_DATE_EXTRA),
						args.getString(Constants.TO_DATE_EXTRA),
						args.getStringArrayList(Constants.TAG_ARRAY_EXTRA),
						args.getStringArrayList(Constants.HASHTAG_ARRAY_EXTRA),
						args.getStringArrayList(Constants.MEDIATYPE_ARRAY_EXTRA)
				);

		Log.e("querycnt", ""+entryIdList.size());

		String whereClause = "";

		if(!entryIdList.isEmpty())
		{
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

		String[][] dbEntryList = super.mDbHelper.selectEntrySet(whereClause, "DESC", super.ROWAMOUNT, offset);
		String[][] timelineEntryList = null;

		if(null == dbEntryList)
		{
			timelineEntryList = new String[0][];
			Log.d(TAG, "No entries where fetched.");
		}
		else
		{
			timelineEntryList = super.generateTimelineEntries(dbEntryList);
		}

		Intent localIntent = new Intent(Constants.BROADCAST_ACTION_RELOADRESPONSE);

		Bundle bundle = new Bundle();
		bundle.putSerializable(Constants.TIMELINE_ENTRIES_ARRAY_EXTRA, timelineEntryList);

		localIntent.putExtra(Constants.TIMELINE_ENTRIES_BUNDLE_EXTRA, bundle);

		localIntent.addCategory(Intent.CATEGORY_DEFAULT);

		// broadcasts the Intent to receivers in this app
		LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
	}
}
