package de.lifebox.LifeBox;

import android.app.IntentService;
import android.content.Intent;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.JsonReader;
import android.util.Log;
import com.google.api.services.drive.model.User;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * IntentService for fetching JSON data from the iTunes RESTful service
 * and return the data as StringExtra
 * @version 0.1 05.07.13
 * @autor Markus Bayer
 * https://itunes.apple.com/search?term=pulp+fiction&limit=10&country=de&entity=movie
 */
public class SearchService extends IntentService
{
	private String TAG = "SearchService";

	/** Creates an IntentService.  Invoked by your subclass's constructor. */
	public SearchService()
	{
		super("SearchService");
	}

	/** Called when the service is first created. */
	@Override
	public void onCreate()
	{
		super.onCreate();
	}

	/** Invoke the worker thread that runs independently from other application logic. */
	@Override
	protected void onHandleIntent(Intent intent)
	{
		String baseURL = "https://itunes.apple.com/search";
		// get the extras
		String query = intent.getStringExtra(Constants.SEARCH_MEDIA_QUERY_EXTRA);
		String mediaType = intent.getStringExtra(Constants.SEARCH_MEDIA_TYPE_EXTRA);

		String entity = "";

		// the Apple-REST-Entity for music is song
		if(mediaType.equals(Constants.TYPE_MUSIC))
		{
			mediaType = "song";
			entity = "musicTrack";
		}
		else if(mediaType.equals(Constants.TYPE_MOVIE))
		{
			mediaType = "movie";
			entity = "movie";
		}

		// encode the query using the format required by application/x-www-form-urlencoded MIME content type
		try
		{
			URLEncoder.encode(query, "UTF-8");
		} catch (UnsupportedEncodingException e)
		{
			Log.e(TAG, e.getMessage());
		}

		// set up a key-value list with the parameter entities
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("term", query));
		postParameters.add(new BasicNameValuePair("limit", "10"));
//		postParameters.add(new BasicNameValuePair("country", "de"));
		postParameters.add(new BasicNameValuePair("media", mediaType));
		postParameters.add(new BasicNameValuePair("entity", entity));

		String result = loadURL(baseURL, postParameters);

		if(null != result)
		{
			Intent localIntent = new Intent(Constants.BROADCAST_ACTION_SEARCHRESPONSE);
			localIntent.putExtra(Constants.MEDIA_RESULT_EXTRA, result);
			localIntent.addCategory(Intent.CATEGORY_DEFAULT);

			// broadcasts the Intent to receivers in this app
			LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

			Log.d(TAG, result);
		}
	}

	/**
	 * Loads a website and returns the content as String
	 * @param baseUrl (String) the website to load
	 * @return result (String) or null if an error occurred
	 */
	private String loadURL(String baseUrl, ArrayList<NameValuePair> postParameters)
	{
		// append the post parameters to the baseurl
		try
		{
			baseUrl += getQuery(postParameters);
		}
		catch (UnsupportedEncodingException e)
		{
			Log.e(TAG, "unsupported encoding " + e.getMessage());
		}

		// load the site
		HttpURLConnection urlConnection = null;
		try
		{
			URL url = new URL(baseUrl);
			urlConnection = (HttpURLConnection) url.openConnection();

			InputStream result = new BufferedInputStream(urlConnection.getInputStream());

			// read the stream, return it as String
			return readStream(result);
		}
		catch (MalformedURLException e)
		{
			Log.e(TAG, "malformed url " + e.getMessage());
		}
		catch (IOException e)
		{
			Log.e(TAG, "io exception " + e.getMessage());
		}
		// clean up
		finally
		{
		if( null != urlConnection )
		{
			urlConnection.disconnect();
		}
	}

	return null;
	}

	/**
	 * Builds a post parameter String from a key value list.
	 * An post parameter String should look like '?term=die+verurteilten&limit=10&country=de&entity=movie'
	 * @param postParameters (List<NameValuePair>) holding the entities and their names
	 * @return a string with the parameters
	 * @throws UnsupportedEncodingException
	 */
	private String getQuery(List<NameValuePair> postParameters) throws UnsupportedEncodingException
	{
		StringBuilder result = new StringBuilder();
		boolean first = true;

		for(NameValuePair pair : postParameters)
		{
			if(first)
			{
				result.append("?");
				first = false;
			}
			else
			{
				result.append("&");
			}

			result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
		}

		Log.d(TAG, "mediaurl"+result.toString());

		return result.toString();
	}

	/**
	 * Converts a stream into a String
	 * @param in (InputStream) holding the data to convert
	 * @return result (String) from the input
	 * @throws IOException
	 */
	private String readStream(InputStream in) throws IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(in), 4096);
		StringBuilder result = new StringBuilder();

		String line;
		while ((line = reader.readLine()) != null)
		{
			result.append(line);
		}
		reader.close();

		return result.toString();
	}
}