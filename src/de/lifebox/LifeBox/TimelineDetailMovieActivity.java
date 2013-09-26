package de.lifebox.LifeBox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * Detailed view for entries of the type movie.
 * @version 0.1 06.08.13
 * @autor Markus Bayer
 */
public class TimelineDetailMovieActivity extends TimelineDetailActivity
{
	private final static String TAG = "TimelineDetailMovieActivity";

	// databasehelper
	private DbHelper mDbHelper;

	// the movie
	Movie movie;

	// ui elements
	private WebView coverWW;
	private TextView titleTV;
	private TextView directorTV;
	private TextView releaseDateTV;
	private TextView genreTV;
	private TextView descriptionTV;

	/** Called when the Activity is first created. */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// get the movie from the database
		mDbHelper = new DbHelper(getBaseContext());

		movie = mDbHelper.selectMovie(super.getMediaId());

		// find the layout file for the specific part
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View childLayout = inflater.inflate(R.layout.timelinedetailmovie, (ViewGroup) findViewById(R.layout.timelinedetail));

		// add it to the parent container
		super.getSpecificLL().addView(childLayout);

		// initialize the ui elements with the movie fields
		coverWW = (WebView) childLayout.findViewById(R.id.moviedetail_webview);
		coverWW.loadUrl(movie.getThumbnailUrl());

		titleTV = (TextView) childLayout.findViewById(R.id.moviedetail_title_textview);
		titleTV.setText(movie.getTitle());

		directorTV = (TextView) childLayout.findViewById(R.id.moviedetail_director_textview);
		directorTV.setText(movie.getDirector());

		releaseDateTV = (TextView) childLayout.findViewById(R.id.moviedetail_releasedate_textview);
		releaseDateTV.setText(movie.getDate());

		genreTV = (TextView) childLayout.findViewById(R.id.moviedetail_genre_textview);
		genreTV.setText(movie.getMovieGenre());

		descriptionTV = (TextView) childLayout.findViewById(R.id.moviedetail_description_textview);
		descriptionTV.setText(movie.getDescription());
	}
}