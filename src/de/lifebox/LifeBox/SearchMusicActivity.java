package de.lifebox.LifeBox;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.JsonReader;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Activity to search for pre-defined media types.
 * Uses the Apple iTunes RESTful service
 * @version 0.1 05.07.13
 * @autor Markus Bayer
 */
public class SearchMusicActivity extends Activity
{
	private ResponseReceiver mResponseReceiver;

	// the adapter intermediate between view and data
	private ArrayAdapter<Music> mMusicAdapter;

	// ArrayList to store the searchresults
	private ArrayList<Music> musicList;

	// the music title, artist and album selected by the user
	private String selectedTitle;
	private String selectedArtist;
	private String selectedAlbum;

	// the type of media to search for
	private String mediaType;

	// the EditText search query
	private EditText queryEditText;
	// the listviews to display the searchresult
	private ListView searchResultListView;
	// the progress bar
	private ProgressBar mProgressBar;

	// the elements of the ListView entries
	private WebView thumbnailWebView;
	private TextView titleTextView;
	private TextView artistTextView;
	private TextView albumTextView;

	// OnKeyListener for the Enter softkey button
	EditText.OnKeyListener queryKeyListener = new View.OnKeyListener()
	{
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event)
		{
			boolean eventConsumed = false;

			// listen to the dpad and the enter softkey
			if(keyCode ==  KeyEvent.KEYCODE_DPAD_CENTER
					|| keyCode ==  KeyEvent.KEYCODE_ENTER)
			{

				// to prevent default behavior do nothing on down but on up
				if(event.getAction() == KeyEvent.ACTION_DOWN)
				{
					// do nothing
				}
				else if(event.getAction() == KeyEvent.ACTION_UP)
				{
					searchMedia();
				}
				eventConsumed = true;

			}
			else
			{
				// other key
				eventConsumed = false;
			}

			return eventConsumed;
		}
	};

	// the buttonlisteners
	Button.OnClickListener mSearchListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			searchMedia();
		}
	};

	// the save Button
	Button.OnClickListener mSaveListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if(null != selectedTitle)
			{
				Intent intent = new Intent(getBaseContext(), MetaFormActivity.class);

				// find the selected Music
				for(Music mMusic : musicList)
				{
					if(mMusic.getTrack().equals(selectedTitle)
							&& mMusic.getArtist().equals(selectedArtist)
							&& mMusic.getAlbum().equals(selectedAlbum))
					{
						// set the extras
						intent.putExtra(Constants.MEDIA_TYPE_EXTRA, Constants.TYPE_MUSIC);
						intent.putExtra(Constants.MUSIC_ARTIST_EXTRA, mMusic.getArtist());
						intent.putExtra(Constants.MUSIC_ALBUM_EXTRA, mMusic.getAlbum());
						intent.putExtra(Constants.MUSIC_REALEASE_DATE_EXTRA, mMusic.getReleaseDate());
						intent.putExtra(Constants.MUSIC_THUMBNAIL_URL_EXTRA, mMusic.getThumbnailUrl());
						intent.putExtra(Constants.MUSIC_TRACK_EXTRA, mMusic.getTrack());
						intent.putExtra(Constants.MUSIC_GENRE_EXTRA, mMusic.getMusicGenre());
					}
				}

				startActivity(intent);
			}
		}
	};

	// Listener for the ListView
	ListView.OnItemClickListener searchresultOnClickListener = new ListView.OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			// get the text of the clicked item
			TextView tvTitle = (TextView) view.findViewById(R.id.musiclist_title);
			TextView tvArtist = (TextView) view.findViewById(R.id.musiclist_artist);
			TextView tvAlbum = (TextView) view.findViewById(R.id.musiclist_album);
			// and store it
			selectedTitle = tvTitle.getText().toString();
			selectedArtist = tvArtist.getText().toString();
			selectedAlbum = tvAlbum.getText().toString();
		}
	};

	/** Called when the Activity is first created. */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchmedia);

		searchResultListView = (ListView) findViewById(R.id.listview_searchresults);

		// the progress bar
		mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_search_media);
		mProgressBar.setVisibility(View.INVISIBLE);

		// the buttons
		Button searchBtn = (Button) findViewById(R.id.button_search_media);
		searchBtn.setOnClickListener(mSearchListener);

		Button saveBtn = (Button) findViewById(R.id.button_save_media);
		saveBtn.setOnClickListener(mSaveListener);

		// the filter
		IntentFilter mStatusIntentFilter = new IntentFilter(Constants.BROADCAST_ACTION_SEARCHRESPONSE);

		// Sets the filter's category to DEFAULT
		mStatusIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);

		// Instantiates a new ResponseReceiver
		mResponseReceiver = new ResponseReceiver();
		// Registers the ResponseReceiver and its intent filters
		LocalBroadcastManager.getInstance(this).registerReceiver(mResponseReceiver, mStatusIntentFilter);

		// get the extra
		mediaType = getIntent().getStringExtra(Constants.SEARCH_MEDIA_TYPE_EXTRA);

		// set the input hint according to the mediaType searching for
		queryEditText = (EditText) findViewById(R.id.in_search_media);
		queryEditText.setHint("Search for a song");
		queryEditText.setOnKeyListener(queryKeyListener);
	}

	/**
	 * Fetches the user input and redirects it to the FetchJsonService.
	 */
	private void searchMedia()
	{
		// hide the ListView show the ProgressBar
		searchResultListView.setVisibility(View.INVISIBLE);
		mProgressBar.setVisibility(View.VISIBLE);
		// get the search query from the edit-text field
		String query =  queryEditText.getText().toString();
		query = query.trim();

		//check if there is a user input to fetch
		if(!query.equals(""))
		{
			// put the extras, start the service
			Intent intent = new Intent(getBaseContext(), FetchJsonService.class);
			intent.putExtra(Constants.SEARCH_MEDIA_TYPE_EXTRA, mediaType);
			intent.putExtra(Constants.SEARCH_MEDIA_QUERY_EXTRA, query);

			startService(intent);

			// clear the input field
			queryEditText.setText("");

			// remove the soft keyboard
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(queryEditText.getWindowToken(), 0);
		}
		else
		{
			Toast.makeText(getBaseContext(), "Please insert a search term.", Toast.LENGTH_SHORT);
		}
	}

	/**
	 * Reads a JSON-String and stores the results in a List
	 * @param json (String) returned by FetchJsonService
	 * @return (ArrayList<Music>) containing Music objects
	 * @throws IOException
	 */
	private ArrayList<Music> parseMusicJsonString(String json) throws IOException
	{
		// convert the String into an InputStream
		InputStream in = new ByteArrayInputStream(json.getBytes());

		JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
		try
		{
			return parseMusicArray(reader);
		}
		finally
		{
			reader.close();
		}
	}

	/**
	 * Reads a JSON-array and stores the single JSON-objects as Music objects.
	 * @param reader (JsonReader) the parser object
	 * @return (ArrayList<Movie>) containing the Movie objects
	 * @throws IOException
	 */
	private ArrayList<Music> parseMusicArray(JsonReader reader) throws IOException
	{
		ArrayList<Music> musicList = new ArrayList();

		reader.beginObject();
		while(reader.hasNext())
		{
			String name = reader.nextName();

			// 'results' contains the needed JSON-array
			if(name.equals("results"))
			{
				reader.beginArray();

				// iterate through the JSON array
				while (reader.hasNext())
				{
					// get the JSON-objects
					musicList.add(parseMusic(reader));
				}

				reader.endArray();
			}
			else
			{
				// the JSON starts with the unneeded key-value pair 'resultCount'
				reader.skipValue();
			}
		}
		return musicList;
	}

	/**
	 * Reads a single JSON-objects and stores the relevant values as Music object.
	 * @param reader (JsonReader) the parser object
	 * @return (Music) object storing the JSON values
	 * @throws IOException
	 */
	private Music parseMusic(JsonReader reader) throws IOException
	{
		String artist = null;
		String album = null;
		String track = null;
		String thumbnailUrl = null;
		String releaseDate = null;
		String musicGenre = null;

		reader.beginObject();
		while (reader.hasNext())
		{
			String name = reader.nextName();

			if (name.equals("artistName"))
			{
				artist = reader.nextString();
			}
			else if (name.equals("collectionName"))
			{
				album = reader.nextString();
			}
			else if (name.equals("trackName"))
			{
				track = reader.nextString();
			}
			else if (name.equals("artworkUrl100"))
			{
				thumbnailUrl = reader.nextString();
			}
			else if (name.equals("releaseDate"))
			{
				releaseDate = reader.nextString();
			}
			else if (name.equals("primaryGenreName"))
			{
				musicGenre = reader.nextString();
			}
			else
			{
				reader.skipValue();
			}
		}
		reader.endObject();
		return new Music(artist, album, releaseDate, thumbnailUrl, track, musicGenre);
	}

	/**
	 * Custom Adapter class to populate the ListView.
	 * @author Markus Bayer
	 * @version 0.1 12.07.13
	 * */
	private class MusicAdapter extends ArrayAdapter<Music>
	{
		/** Constructor */
		public MusicAdapter()
		{
			super(SearchMusicActivity.this, R.layout.musiclist, musicList);
		}

		/** Called when the ListView will be populated. */
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			// ensure there is a view to work with (could be null)
			View itemView = convertView;
			if(null == itemView)
			{
				itemView = getLayoutInflater().inflate(R.layout.musiclist, parent, false);
			}

			// find the current movie
			Music currentMusic = musicList.get(position);

			// set the thumbnail image
			// TODO remove network operation from the main thread
			thumbnailWebView = (WebView) itemView.findViewById(R.id.musiclist_thumbnail);
			thumbnailWebView.loadUrl(currentMusic.getThumbnailUrl());

			// set the title
			titleTextView = (TextView) itemView.findViewById(R.id.musiclist_title);
			titleTextView.setText(currentMusic.getTrack());

			// set the artist
			artistTextView = (TextView) itemView.findViewById(R.id.musiclist_artist);
			artistTextView.setText(currentMusic.getArtist());

			// set the album
			albumTextView = (TextView) itemView.findViewById(R.id.musiclist_album);
			albumTextView.setText(currentMusic.getAlbum());

			return itemView;
		}
	}

	/**
	 * BroadcastReceiver to catch the JSON Strings brought by FetchJsonService
	 * @author Markus Bayer
	 * @version 0.1 29.06.13
	 */
	public class ResponseReceiver extends BroadcastReceiver
	{
		/** Called when the BroadcastReceiver gets an Intent it's registered to receive */
		public void onReceive(Context context, Intent intent)
		{
			// remove the progress bar, enable the ListView
			mProgressBar.setVisibility(View.GONE);
			searchResultListView.setVisibility(View.VISIBLE);
			// get the extras
			if(intent.hasExtra(Constants.MEDIA_RESULT_EXTRA))
			{
				String result = intent.getStringExtra(Constants.MEDIA_RESULT_EXTRA);

				Log.e("restresult", result);

				try
				{
					// parse the result and store the movies in the specific ArrayList
					musicList = parseMusicJsonString(result);

					// set the Adapter to the ListView
					mMusicAdapter = new MusicAdapter();
					searchResultListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
					searchResultListView.setOnItemClickListener(searchresultOnClickListener);
					searchResultListView.setAdapter(mMusicAdapter);

					if(musicList.isEmpty())
					{
						Toast toast = Toast.makeText(getBaseContext(), "Sorry, nothing found", Toast.LENGTH_LONG);
						toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 120);
						toast.show();
					}
				}
				catch (IOException e)
				{
					Log.e("parseMusicJsonString: IOException", e.getMessage());
				}
			}
		}
	}
}