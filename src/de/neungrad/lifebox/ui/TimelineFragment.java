package de.neungrad.lifebox.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.*;
import de.neungrad.lifebox.R;
import de.neungrad.lifebox.db.DbHelper;
import de.neungrad.lifebox.helper.Constants;
import de.neungrad.lifebox.helper.TimelineEntry;
import de.neungrad.lifebox.service.TimelineFilteredReloadService;
import de.neungrad.lifebox.service.TimelineReloadService;

import java.util.ArrayList;

/**
 * Fragment for showing entries in a timeline.
 * @author Markus Bayer
 * @version 0.1 12.06.2013
 */
public class TimelineFragment extends Fragment
{
	private static final String TAG = "TimelineFragment";

	// local broadcast manager
	private ResponseReceiver mResponseReceiver;

	// the list storing the entries on the timeline
	private ArrayList<TimelineEntry> entryList;

	// the current row position at the entries table
	private int offset;
	// the filter data
	private Bundle conditions;

	// flags
	// processed is true when the timeline is fully loaded
	private boolean processed = false;
	// empty result
	private boolean noResult = false;

	// databasehelper
	private DbHelper mDbHelper;

	// Adapter
	private TimelineAdapter mTimelilneAdapter;

	// the ui elements
	private ListView timelineLV;
	private RelativeLayout noEtnriesRL;

	private FrameLayout imageFrame;
	private ImageView imageView;
	private WebView movieWebView;
	private WebView musicWebView;
	private TextView dateTV;
	private TextView timeTV;
	private TextView titleTV;
	private TextView firstlineTV;
	private TextView secondlineTV;

	Button disableFilterBtn;

	// Buttonlistener
	Button.OnClickListener mDisableFilterListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			// clear all parameters
			noEtnriesRL.setVisibility(View.INVISIBLE);
			timelineLV.setVisibility(View.VISIBLE);
			noResult = false;
			processed = false;
			offset = 0;
			conditions.clear();

			// and request unfiltered entries
			requestEntries(offset);
		}
	};

	// Listener for the timeline items
	ListView.OnItemClickListener mOnItemClickListener = new ListView.OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			Intent intent = null;

			// decide what type the clicked entry is of
			// the position of the listview == the position in the entryList
			String type = entryList.get(position).getType();

			// text
			if(type.equals(Constants.TYPE_TEXT))
			{
				intent = new Intent(getActivity(), TimelineDetailTextActivity.class);
			}
			// movie
			else if(type.equals(Constants.TYPE_MOVIE))
			{
				intent = new Intent(getActivity(), TimelineDetailMovieActivity.class);
			}
			// music
			else if(type.equals(Constants.TYPE_MUSIC))
			{
				intent = new Intent(getActivity(), TimelineDetailMusicActivity.class);
			}
			// file
			else if(type.equals(Constants.TYPE_FILE))
			{
				String filetype = entryList.get(position).getFiletype();

				// image
				if(filetype.equals(Constants.MIME_TYPE_IMAGE))
				{
					intent = new Intent(getActivity(), TimelineDetailFileImageActivity.class);
				}
				// video
				if(filetype.equals(Constants.MIME_TYPE_VIDEO))
				{
					intent = new Intent(getActivity(), TimelineDetailFileVideoActivity.class);
				}
			}

			// get the entryId and set it as extra
			intent.putExtra(Constants.ENTRY_ID_EXTRA, entryList.get(position).getEntryId());

			startActivity(intent);
		}
	};

	// Listener that reload more entries if necessary
	ListView.OnScrollListener mScrollListener = new ListView.OnScrollListener()
	{
		// unneeded method
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState)	{}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
		{
			// if there are more entries to fetch
			if(processed == false)
			{
				// reached the end of the ListView?
				if(!entryList.isEmpty() && (entryList.size() == (firstVisibleItem + visibleItemCount)))
				{
					// fetch the next 10 items
					offset += Constants.LIMIT;

					// check for conditions
					if(conditions.isEmpty())
					{
						// request entries without condition
						requestEntries(offset);
					}
					else
					{
						// request entries with condition
						requestFilteredEntries(offset, conditions);
					}
				}
			}
		}
	};

	/** Called when the fragment is first created. */
	public static final TimelineFragment newInstance()
	{
		TimelineFragment f = new TimelineFragment();
     	// pack the bundle
		Bundle bdl = new Bundle(1);
     	f.setArguments(bdl);

     	return f;
     }

	/** Called when the fragment is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		entryList = new ArrayList<TimelineEntry>();

		// the filter
		IntentFilter mStatusIntentFilter = new IntentFilter(Constants.BROADCAST_ACTION_RELOADRESPONSE);
		// Sets the filter's category to DEFAULT
		mStatusIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);

		// Instantiates a new ResponseReceiver
		mResponseReceiver = new ResponseReceiver();
		// Registers the ResponseReceiver and its intent filters
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mResponseReceiver, mStatusIntentFilter);

		mDbHelper = new DbHelper(getActivity().getBaseContext());

		// request data to fill the timeline
		offset = 0;

		// get the bundle of conditions
		conditions = this.getArguments();

		// if the timeline is not fully loaded
		if(processed == false)
		{
			// no conditions
			if(conditions.isEmpty())
			{
				// request entries without condition
				requestEntries(offset);
			}
			else
			{
				// request entries with condition
				requestFilteredEntries(offset, conditions);
			}
		}
	}

	/**
	 * Called when the fragment is first created.
	 * Creates and returns the view hierarchy associated with the fragment.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
        View view = inflater.inflate(R.layout.timeline, container, false);

		// initialize the ui elements
		noEtnriesRL = (RelativeLayout) view.findViewById(R.id.timeline_noentries);
		noEtnriesRL.setVisibility(View.INVISIBLE);
		disableFilterBtn = (Button) view.findViewById(R.id.timeline_disablefilter);
		disableFilterBtn.setOnClickListener(mDisableFilterListener);

		timelineLV = (ListView) view.findViewById(R.id.listview_timeline);
		timelineLV.setOnItemClickListener(mOnItemClickListener);
		timelineLV.setOnScrollListener(mScrollListener);

		imageFrame = (FrameLayout) view.findViewById(R.id.image_frame);
		imageView = (ImageView) view.findViewById(R.id.thumbnail_imageview);
		movieWebView = (WebView) view.findViewById(R.id.movie_webview);
		dateTV = (TextView) view.findViewById(R.id.timeline_date_textview);
		timeTV = (TextView) view.findViewById(R.id.timeline_time_textview);
		titleTV = (TextView) view.findViewById(R.id.timeline_title_textview);
		firstlineTV = (TextView) view.findViewById(R.id.timeline_firstline_textview);
		secondlineTV = (TextView) view.findViewById(R.id.timeline_secondline_textview);

		return view;
	}

	/** Called when the user navigate between the fragments. */
	@Override
	public void onResume()
	{
		super.onResume();

		if(null == timelineLV.getAdapter())
		{
			// restore the no result view if necessary
			if(noResult)
			{
				noEtnriesRL.refreshDrawableState();
				noEtnriesRL.setVisibility(View.VISIBLE);
				timelineLV.setVisibility(View.INVISIBLE);
			}

			// re-set the adapter if the binding was lost in onStop()
			mTimelilneAdapter = new TimelineAdapter();
			timelineLV.setAdapter(mTimelilneAdapter);
		}
	}

	/**
	 * Passes the the offset as point of return to the TimelineReloadService in order to get entries for the timeline
	 * @param offset (int) the rowamount of already retrieved timeline entries
	 */
	private void requestEntries(int offset)
	{
		Log.d(TAG, "request entry set");
		Intent intent = new Intent(getActivity(), TimelineReloadService.class);

		intent.putExtra(Constants.OFFSET_EXTRA, offset);

		getActivity().startService(intent);
	}

	/**
	 * Passes the the offset and a bundle of conditions  TimelineReloadService in order to get entries for the timeline
	 * @param offset (int) the rowamount of already retrieved timeline entries
	 * @param args (Bundle) a bundle holding the conditions
	 */
	private void requestFilteredEntries(int offset, Bundle args)
	{
		Log.d(TAG, "request filtered entry set");
		Intent intent = new Intent(getActivity(), TimelineFilteredReloadService.class);

		intent.putExtra(Constants.OFFSET_EXTRA, offset);
		intent.putExtra(Constants.FILTER_BUNDLE_EXTRA, args);

		getActivity().startService(intent);
	}

	/**
	 * Custom Adapter class to populate the ListView.
	 * @author Markus Bayer
	 * @version 0.1 31.07.13
	 * */
	private class TimelineAdapter extends ArrayAdapter<TimelineEntry>
	{
		/** Constructor */
		public TimelineAdapter()
		{
			super(TimelineFragment.this.getActivity(), R.layout.timelinelist, entryList);
		}

		/** Called when the ListView will be populated. */
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			// save index and top position
			int index = timelineLV.getFirstVisiblePosition();
			View v = timelineLV.getChildAt(0);
			int top = (v == null) ? 0 : v.getTop();

			// ensure there is a view to work with (could be null)
			View itemView = convertView;

			if(null == itemView)
			{
				itemView = getActivity().getLayoutInflater().inflate(R.layout.timelinelist, parent, false);
			}

			// find the current entry
			TimelineEntry currentEntry = entryList.get(position);

			String type = currentEntry.getType();

			// initialize the ui elements
			imageFrame = (FrameLayout) itemView.findViewById(R.id.image_frame);

			imageView = (ImageView) itemView.findViewById(R.id.thumbnail_imageview);

			dateTV = (TextView) itemView.findViewById(R.id.timeline_date_textview);
			dateTV.setText(currentEntry.getDate());

			timeTV = (TextView) itemView.findViewById(R.id.timeline_time_textview);
			timeTV.setText(currentEntry.getTime());

			titleTV = (TextView) itemView.findViewById(R.id.timeline_title_textview);
			titleTV.setText(currentEntry.getTitle());

			firstlineTV = (TextView) itemView.findViewById(R.id.timeline_firstline_textview);
			firstlineTV.setText(currentEntry.getFirstText());

			secondlineTV = (TextView) itemView.findViewById(R.id.timeline_secondline_textview);
			secondlineTV.setText(currentEntry.getSecondText());

			movieWebView = (WebView) itemView.findViewById(R.id.movie_webview);

			musicWebView = (WebView) itemView.findViewById(R.id.music_webview);

			// set the type specific values
			// type file
			if(type.equals(Constants.TYPE_FILE))
			{
				if(currentEntry.getFiletype().equals(Constants.MIME_TYPE_IMAGE))
				{
					// set the type-specific color
					imageFrame.setBackgroundResource(R.color.playgreen);
				}
				else if(currentEntry.getFiletype().equals(Constants.MIME_TYPE_VIDEO))
				{
					imageFrame.setBackgroundResource(R.color.playred);
				}

				// hide the unneeded elements
				movieWebView.setVisibility(View.INVISIBLE);
				musicWebView.setVisibility(View.INVISIBLE);
				// show the required element
				imageView.setVisibility(View.VISIBLE);
				// set the image
				imageView.setImageDrawable(Drawable.createFromPath(currentEntry.getThumbnail()));
			}
			// type text
			else if(type.equals(Constants.TYPE_TEXT))
			{
				imageFrame.setBackgroundResource(R.color.playblue);
				movieWebView.setVisibility(View.INVISIBLE);
				musicWebView.setVisibility(View.INVISIBLE);
				imageView.setVisibility(View.VISIBLE);
				imageView.setImageDrawable(getResources().getDrawable(R.drawable.placeholder_text));
			}
			// type movie
			else if(type.equals(Constants.TYPE_MOVIE))
			{
				imageFrame.setBackgroundResource(R.color.playcyan);
				imageView.setVisibility(View.INVISIBLE);
				musicWebView.setVisibility(View.INVISIBLE);
				movieWebView.setVisibility(View.VISIBLE);
				movieWebView.loadUrl(currentEntry.getThumbnail());
			}
			// type music
			else if(type.equals(Constants.TYPE_MUSIC))
			{
				imageFrame.setBackgroundResource(R.color.playorange);
				imageView.setVisibility(View.INVISIBLE);
				movieWebView.setVisibility(View.INVISIBLE);
				musicWebView.setVisibility(View.VISIBLE);
				musicWebView.loadUrl(currentEntry.getThumbnail());
			}

			// restore the position
			timelineLV.setSelectionFromTop(index, top);
			timelineLV.setVerticalScrollbarPosition(position);

			return itemView;
		}

		/**
		 * Called when new entries where added.
		 * Updates the ListView.
		 * @param entries (ArrayList<TimelineEntry>) the new entries
		 */
		private void updateListView(ArrayList<TimelineEntry> entries)
		{
			// the entries
			entryList.addAll(entries);

			// inform the adapter
			notifyDataSetChanged();
		}
	}

	/**
	 * BroadcastReceiver to catch t
	 * @author Markus Bayer
	 * @version 0.1 31.07.13
	 */
	public class ResponseReceiver extends BroadcastReceiver
	{
		/** Called when the BroadcastReceiver gets an Intent it's registered to receive */
		public void onReceive(Context context, Intent intent)
		{
			// set the Adapter to the ListView
			timelineLV.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

			// get the extra
			Bundle bundle = intent.getBundleExtra(Constants.TIMELINE_ENTRIES_BUNDLE_EXTRA);

			String[][] reloadResultList = (String[][]) bundle.getSerializable(Constants.TIMELINE_ENTRIES_ARRAY_EXTRA);

			// save the fetched results
			String type = null;
			String filetype = null;
			String entryId = null;
			String thumbnail = null;
			String date = null;
			String time = null;
			String title = null;
			String firstText = null;
			String secondText = null;

			// type + filetype + entryId... = 9
			final int resultAmount = 9;

			// resultlist
			ArrayList<TimelineEntry> fetchedEntries = new ArrayList<TimelineEntry>();

			// check if there are entries in the result
			if(reloadResultList.length > 0)
			{
				// the entries ...
				for(int r = 0; r < reloadResultList.length; r++)
				{
					// ... their variables
					for(int c = 0; c < resultAmount; c++)
					{
						switch(c)
						{
							// type
							case 0:
								type = reloadResultList[r][c];
								break;

							// filetype
							case 1:
								filetype = reloadResultList[r][c];
								break;

							// entry_id
							case 2:
								entryId = reloadResultList[r][c];
								break;

							// thumbnail
							case 3:
								thumbnail = reloadResultList[r][c];
								break;

							// date
							case 4:
								date = reloadResultList[r][c];
								break;

							// time
							case 5:
								time = reloadResultList[r][c];
								break;

							// title
							case 6:
								title = reloadResultList[r][c];
								break;

							case 7:
								// first text
								firstText = reloadResultList[r][c];

							case 8:
								// second text
								secondText = reloadResultList[r][c];

							default:
								break;
						}
					}

					// save the entries
					fetchedEntries.add
					(
						new TimelineEntry(type, filetype, entryId, thumbnail,date, time, title, firstText, secondText)
					);
				}
			}
			// empty result
			else
			{
				// no more entries to fetch
				processed = true;

				// requested entries but no result -> no entries at all OR filter returned zero entries
				if(entryList.isEmpty())
				{
					noResult = true;

					// show the no-entries view
					noEtnriesRL.setVisibility(View.VISIBLE);
					timelineLV.setVisibility(View.INVISIBLE);
					disableFilterBtn.setOnClickListener(mDisableFilterListener);
				}
			}

			// commit the changes to the adapter
			if(null == timelineLV.getAdapter())
			{
				// if there is no adapter create one and set it
				mTimelilneAdapter = new TimelineAdapter();
				timelineLV.setAdapter(mTimelilneAdapter);
			}
			else
			{
				// adapter already exists, update the ListView
				((TimelineAdapter) timelineLV.getAdapter()).updateListView(fetchedEntries);
			}
		}
	}
}
