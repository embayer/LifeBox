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
public class SearchMovieActivity extends Activity
{
	private ResponseReceiver mResponseReceiver;

	// ArrayList to store the searchresults
	private ArrayList<Movie> movieList;

	// the movie title selected by the user
	private String selectedTitle;
	private String selectedDirector;

	// the type of media to search for
	private String mediaType;

	// the EditText search query
	private EditText queryEditText;
	// the listviews to display the searchresult
	private ListView searchResultListView;

	// the elements of the ListView entries
	private WebView thumbnailWebView;
	private TextView titleTextView;
	private TextView artistTextView;

	// the adapter intermediate between view and data
	private ArrayAdapter<Movie> mMovieAdapter;

	// the buttonlisteners
	Button.OnClickListener mSearchListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			// get the search query from the edit-text field
			queryEditText = (EditText) findViewById(R.id.in_search_media);
			String query =  queryEditText.getText().toString();
			query = query.trim();

			//check if there is a user input to fetch
			if( (null != query) && (!query.equals("") && (query.length() >= 0)) )
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

				// find the selected Movie
				for(Movie mMovie : movieList)
				{
					if(mMovie.getTitle().equals(selectedTitle) && mMovie.getDirector().equals(selectedDirector))
					{
						// set the extras
						intent.putExtra(Constants.MOVIE_TITLE_EXTRA, mMovie.getTitle());
						intent.putExtra(Constants.MOVIE_DESCRIPTION_EXTRA, mMovie.getDescription());
						intent.putExtra(Constants.MOVIE_DIRECTOR_EXTRA, mMovie.getDirector());
						intent.putExtra(Constants.MOVIE_GENRE_EXTRA, mMovie.getMovieGenre());
						intent.putExtra(Constants.MOVIE_RELEASE_DATE_EXTRA, mMovie.getReleaseDate());
						intent.putExtra(Constants.MOVIE_THUMBNAIL_URL_EXTRA, mMovie.getThumbnailUrl());
						Log.e("description",mMovie.getDescription() );
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
			TextView tv = (TextView) view.findViewById(R.id.searchresult_title);
			// and store it
			selectedTitle = tv.getText().toString();
		}

	};

	/** Called when the Activity is first created. */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchmedia);

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
		queryEditText.setHint("Search for a movie");
	}

	/**
	 * Reads a JSON-String and stores the results in a List
	 * @param json (String) returned by FetchJsonService
	 * @return (ArrayList<Movie>) containing Movies objects
	 * @throws IOException
	 */
	public ArrayList<Movie> parseMovieJsonString(String json) throws IOException
	{
		// convert the String into an InputStream
		InputStream in = new ByteArrayInputStream(json.getBytes());

		JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
		try
		{
			return parseMovieArray(reader);
		}
		finally
		{
			reader.close();
		}
	}

	/**
	 * Reads a JSON-array and stores the single JSON-objects as Movie objects.
	 * @param reader (JsonReader) the parser object
	 * @return (ArrayList<Movie>) containing the Movie objects
	 * @throws IOException
	 */
	public ArrayList<Movie> parseMovieArray(JsonReader reader) throws IOException
	{
		ArrayList<Movie> movieList = new ArrayList();

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
					movieList.add(parseMovie(reader));
				}

				reader.endArray();
			}
			else
			{
				// the JSON starts with the unneeded key-value pair 'resultCount'
				reader.skipValue();
			}
		}
		return movieList;
	}

	/**
	 * Reads a single JSON-objects and stores the relevant values as movie object.
	 * @param reader (JsonReader) the parser object
	 * @return (Movie) object storing the JSON values
	 * @throws IOException
	 */
	public Movie parseMovie(JsonReader reader) throws IOException
	{
		String director = null;
		String title = null;
		String thumbnailUrl = null;
		String releaseDate = null;
		String movieGenre = null;
		String description = null;

		reader.beginObject();
		while (reader.hasNext())
		{
			String name = reader.nextName();

			if (name.equals("artistName"))
			{
				director = reader.nextString();
			}
			else if (name.equals("trackName"))
			{
				title = reader.nextString();
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
				movieGenre = reader.nextString();
			}
			else if (name.equals("longDescription"))
			{
				description = reader.nextString();
			}
			else
			{
				reader.skipValue();
			}
		}
		reader.endObject();
		return new Movie(title, description, director, movieGenre, releaseDate, thumbnailUrl);
	}

	/**
	 * Custom Adapter class to populate the ListView.
	 * @author Markus Bayer
	 * @version 0.1 12.07.13
	 * */
	private class MovieAdapter extends ArrayAdapter<Movie>
	{
		/** Constructor */
		public MovieAdapter()
		{
			super(SearchMovieActivity.this, R.layout.movielist, movieList);
		}

		/** Called when the ListView will be populated. */
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			// ensure there is a view to work with (could be null)
			View itemView = convertView;
			if(null == itemView)
			{
				itemView = getLayoutInflater().inflate(R.layout.movielist, parent, false);
			}

			// find the current movie
			Movie currentMovie = movieList.get(position);

			// set the thumbnail image
			// TODO remove network operation from the main thread
			thumbnailWebView = (WebView)itemView.findViewById(R.id.searchresult_thumbnail);
			thumbnailWebView.loadUrl(currentMovie.getThumbnailUrl());

			// set the title
			titleTextView = (TextView)itemView.findViewById(R.id.searchresult_title);
			titleTextView.setText(currentMovie.getTitle());

			// set the director
			artistTextView = (TextView)itemView.findViewById(R.id.searchresult_artist);
			artistTextView.setText(currentMovie.getDirector());

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
			// get the extras
			if(intent.hasExtra(Constants.MEDIA_RESULT_EXTRA))
			{
				String result = intent.getStringExtra(Constants.MEDIA_RESULT_EXTRA);

				Log.e("restresult", result);

				try
				{
					// parse the result and store the movies in the specific ArrayList
					movieList = parseMovieJsonString(result);

					// set the Adapter to the ListView
					mMovieAdapter = new MovieAdapter();
					searchResultListView = (ListView) findViewById(R.id.listview_searchresults);
					searchResultListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
					searchResultListView.setOnItemClickListener(searchresultOnClickListener);
					searchResultListView.setAdapter(mMovieAdapter);

					if(movieList.isEmpty())
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