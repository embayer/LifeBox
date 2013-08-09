package de.lifebox.LifeBox;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import com.google.api.client.util.IOUtils;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;

/**
 * Fragment for showing entries in a timeline.
 * @author Markus Bayer
 * @version 0.1 12.06.2013
 */
public class TimelineFragment extends Fragment
{
	private static final String TAG = "TimelineFragment";

	private ResponseReceiver mResponseReceiver;

	// the list storing the entries on the timeline
	private ArrayList<TimelineEntry> entryList;

	private static final int ROWAMOUNT = 10;
	private static final int COLUMNAMOUNT = 3;

	private int offset;

	private boolean unprocessed;

	private DbHelper mDbHelper;

	private TimelineAdapter mTimelilneAdapter;

	// the ui elements
	private ListView timelineLV;

	private FrameLayout imageFrame;
	private ImageView imageView;
	private WebView movieWebView;
	private WebView musicWebView;
	private TextView dateTV;
	private TextView timeTV;
	private TextView titleTV;
	private TextView firstlineTV;
	private TextView secondlineTV;

	// Listener for the timeline items
	ListView.OnItemClickListener timelineListener = new ListView.OnItemClickListener()
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

	/** Called when the fragment is first created. */
	public static final TimelineFragment newInstance()
	{
		TimelineFragment f = new TimelineFragment();
     	Bundle bdl = new Bundle(1);
     	f.setArguments(bdl);

     	return f;
     }

	/** Called when the fragment is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// the filter
		IntentFilter mStatusIntentFilter = new IntentFilter(Constants.BROADCAST_ACTION_RELOADRESPONSE);
		// Sets the filter's category to DEFAULT
		mStatusIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);

		// Instantiates a new ResponseReceiver
		mResponseReceiver = new ResponseReceiver();
		// Registers the ResponseReceiver and its intent filters
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mResponseReceiver, mStatusIntentFilter);

		mDbHelper = new DbHelper(getActivity().getBaseContext());

		entryList = new ArrayList<TimelineEntry>();

		// request data to fill the timeline
		offset = 0;
		requestEntries(offset);
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
		timelineLV = (ListView) view.findViewById(R.id.listview_timeline);
		timelineLV.setOnItemClickListener(timelineListener);

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

	@Override
	public void onResume()
	{
		super.onResume();

//		mTimelilneAdapter.notifyDataSetChanged();
//		mTimelilneAdapter.notify();
		Log.e("triggert", "onResume");
	}

	/**
	 * Passes the the offset as point of return to the TimelineReloadService in order to get entries for the timeline
	 * @param offset (int) the rowamount of already retrieved timeline entries
	 */
	private void requestEntries(int offset)
	{
		Intent intent = new Intent(getActivity(), TimelineReloadService.class);

		intent.putExtra(Constants.OFFSET_EXTRA, offset);

		getActivity().startService(intent);

	}

	private BitmapDrawable downloadBitmap(String url)
	{
		Bitmap bitmap = null;
		InputStream in = null;
		BufferedOutputStream out = null;

		try
		{

			in = new BufferedInputStream(new URL(url).openStream());


			final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
			out = new BufferedOutputStream(dataStream);
			IOUtils.copy(in, out);
			out.flush();

			final byte[] data = dataStream.toByteArray();
			BitmapFactory.Options options = new BitmapFactory.Options();
			//options.inSampleSize = 1;

			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,options);
		}
		catch (IOException e)
		{
			Log.e(TAG, "Could not load Bitmap from: " + url);
		}
		finally
		{
			try
			{
				if(null != in)
				{
					in.close();
				}
			}
			catch (IOException e)
			{
				Log.e(TAG, e.getMessage());
			}
			try
			{
				if(null != out)
				{
					out.close();
				}
			}
			catch (IOException e)
			{
				Log.e(TAG, e.getMessage());
			}
		}

		return new BitmapDrawable(getResources(), bitmap);
//		return bitmap;
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
			// ensure there is a view to work with (could be null)
			View itemView = convertView;
			if(null == itemView)
			{
				itemView = getActivity().getLayoutInflater().inflate(R.layout.timelinelist, parent, false);
			}

			// find the current movie
			TimelineEntry currentEntry = entryList.get(position);

			String type = currentEntry.getType();

			// TODO remove network operation from the main thread

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

//			image_view.getLayoutParams().height = 20;


			// set the type specific values
			// type file
			if(type.equals(Constants.TYPE_FILE))
			{
				if(currentEntry.getFiletype().equals(Constants.MIME_TYPE_IMAGE))
				{
					imageFrame.setBackgroundResource(R.color.blue);
				}
				else if(currentEntry.getFiletype().equals(Constants.MIME_TYPE_VIDEO))
				{
					imageFrame.setBackgroundResource(R.color.red);
				}

				movieWebView.setVisibility(View.INVISIBLE);
				musicWebView.setVisibility(View.INVISIBLE);
				imageView.setVisibility(View.VISIBLE);
				imageView.setImageDrawable(Drawable.createFromPath(currentEntry.getThumbnail()));
			}
			// type text
			else if(type.equals(Constants.TYPE_TEXT))
			{
				imageFrame.setBackgroundResource(R.color.green);
				movieWebView.setVisibility(View.INVISIBLE);
				musicWebView.setVisibility(View.INVISIBLE);
				imageView.setVisibility(View.VISIBLE);
				imageView.setImageDrawable(getResources().getDrawable(R.drawable.placeholder_text));
			}
			// type movie
			else if(type.equals(Constants.TYPE_MOVIE))
			{
				imageFrame.setBackgroundResource(R.color.purple);
				imageView.setVisibility(View.INVISIBLE);
				musicWebView.setVisibility(View.INVISIBLE);
				movieWebView.setVisibility(View.VISIBLE);
				movieWebView.loadUrl(currentEntry.getThumbnail());

//				imageView.setImageDrawable(downloadBitmap(currentEntry.getThumbnail()));
			}
			// type music
			else if(type.equals(Constants.TYPE_MUSIC))
			{
				imageFrame.setBackgroundResource(R.color.yellow);
				imageView.setVisibility(View.INVISIBLE);
				movieWebView.setVisibility(View.INVISIBLE);
				musicWebView.setVisibility(View.VISIBLE);
				musicWebView.loadUrl(currentEntry.getThumbnail());

//				imageView.setImageDrawable(downloadBitmap(currentEntry.getThumbnail()));
			}


			return itemView;
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
			mTimelilneAdapter = new TimelineAdapter();
			// set the Adapter to the ListView
			timelineLV.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//			timelineLV.setOnItemClickListener(searchresultOnClickListener);
			timelineLV.setAdapter(mTimelilneAdapter);

			// get the extra
			Bundle bundle = intent.getBundleExtra(Constants.TIMELINE_ENTRIES_BUNDLE_EXTRA);

			String[][] reloadResultList = (String[][]) bundle.getSerializable(Constants.TIMELINE_ENTRIES_ARRAY_EXTRA);

//			Log.e(TAG + " catched", reloadResultList[0][1]);
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

			final int resultAmount = 9;

			if(reloadResultList.length > 0)
			{
				for(int r = 0; r < reloadResultList.length; r++)
				{
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
//								Log.e(TAG, "first: "+reloadResultList[r][c]);

							case 8:
								// second text
								secondText = reloadResultList[r][c];
//								Log.e(TAG, "second: "+reloadResultList[r][c]);

							default:
								break;
						}
					}
					// save the entries
					entryList.add
							(
									new TimelineEntry(type, filetype, entryId, thumbnail,date, time, title, firstText, secondText)
							);
				}

				// request the next entries
				offset += 10;
				requestEntries(offset);
			}
		}
	}
}
