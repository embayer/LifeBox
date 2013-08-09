package de.lifebox.LifeBox;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * Detailed view for entries of the type music.
 *
 * @version 0.1 06.08.13
 * @autor Markus Bayer
 */
public class TimelineDetailMusicActivity extends TimelineDetailActivity
{
	private final static String TAG = "TimelineDetailMusicActivity";

	private DbHelper mDbHelper;

	// the music
	private Music music;

	// ui elements
	private WebView coverWW;
	private TextView trackTV;
	private TextView artistTV;
	private TextView albumTV;
	private TextView releaseDateTV;
	private TextView genreTV;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// get the music from the database
		mDbHelper = new DbHelper(getBaseContext());

		music = mDbHelper.selectMusic(super.getMediaId());

		// find the layout file for the specific part
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View childLayout = inflater.inflate(R.layout.timelinedetailmusic, (ViewGroup) findViewById(R.layout.timelinedetail));

		// add it to the parent container
		super.getSpecificLL().addView(childLayout);

		// initialize the ui elements with the music fields
		coverWW = (WebView) childLayout.findViewById(R.id.musicdetail_webview);
		coverWW.loadUrl(music.getThumbnailUrl());

		trackTV = (TextView) childLayout.findViewById(R.id.musicdetail_track_textview);
		trackTV.setText(music.getTrack());

		artistTV = (TextView) childLayout.findViewById(R.id.musicdetail_artist_textview);
		artistTV.setText(music.getArtist());

		albumTV = (TextView) childLayout.findViewById(R.id.musicdetail_album_textview);
		albumTV.setText(music.getAlbum());

		releaseDateTV = (TextView) childLayout.findViewById(R.id.musicdetail_releasedate_textview);
		releaseDateTV.setText(music.getDate());

		genreTV = (TextView) childLayout.findViewById(R.id.musicdetail_genre_textview);
		genreTV.setText(music.getMusicGenre());
	}
}