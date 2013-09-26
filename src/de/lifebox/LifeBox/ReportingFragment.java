package de.lifebox.LifeBox;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Fragment for reporting user statistics.
 * @author Markus Bayer
 * @version 0.1 12.06.2013
 */
public class ReportingFragment extends Fragment
{
	DbHelper mDbHelper;

	/**
	 * Constructor
	 * @return ReportingFragment instance
	 */
	public static final ReportingFragment newInstance()
	{
		ReportingFragment f = new ReportingFragment();
        Bundle bdl = new Bundle(1);
		f.setArguments(bdl);

		return f;
	}

	/** Called when the fragment is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// initialize the DbHelper
		mDbHelper = new DbHelper(getActivity());
	}

	/** Called when the fragment is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.reporting, container, false);


		// initialize the ui elements
		TextView entriesTotal = (TextView) view.findViewById(R.id.reporting_entries_total_value);
		entriesTotal.setText(mDbHelper.selectCountEntries());

		TextView imagesTotal = (TextView) view.findViewById(R.id.reporting_images_total_value);
		imagesTotal.setText(mDbHelper.selectCountFiles(Constants.MIME_TYPE_IMAGE));

		TextView videosTotal = (TextView) view.findViewById(R.id.reporting_videos_total_value);
		videosTotal.setText(mDbHelper.selectCountFiles(Constants.MIME_TYPE_VIDEO));

		TextView songsTotal = (TextView) view.findViewById(R.id.reporting_songs_total_value);
		songsTotal.setText(mDbHelper.selectCountMedia(Constants.TYPE_MUSIC));

		TextView moviesTotal = (TextView) view.findViewById(R.id.reporting_movies_total_value);
		moviesTotal.setText(mDbHelper.selectCountMedia(Constants.TYPE_MOVIE));

		TextView textTotal = (TextView) view.findViewById(R.id.reporting_text_total_value);
		textTotal.setText(mDbHelper.selectCountMedia(Constants.TYPE_TEXT));

		TextView tagsTotal = (TextView) view.findViewById(R.id.reporting_tags_total_value);
		tagsTotal.setText(mDbHelper.selectCount(LifeBoxContract.EntryTags._ID, LifeBoxContract.EntryTags.TABLE_NAME));

		TextView hashtagsTotal = (TextView) view.findViewById(R.id.reporting_hashtags_total_value);
		hashtagsTotal.setText(mDbHelper.selectCount(LifeBoxContract.EntryHashtags._ID, LifeBoxContract.EntryHashtags.TABLE_NAME));

		ArrayList<HashMap<String, String>> tagList = mDbHelper.selectTagUsage(1);

		TextView tagMostPopular = (TextView) view.findViewById(R.id.reporting_tag_mostpopular_value);
		tagMostPopular.setText(tagList.get(0).get("tag").toString());

		TextView tagMostPopularCount = (TextView) view.findViewById(R.id.reporting_tag_mostpopular_count_value);
		tagMostPopularCount.setText(tagList.get(0).get("count").toString());

		ArrayList<HashMap<String, String>> hashtagList = mDbHelper.selectHashtagUsage(1);

		TextView hashtagMostPopular = (TextView) view.findViewById(R.id.reporting_hashtag_mostpopular_value);
		hashtagMostPopular.setText(hashtagList.get(0).get("hashtag").toString());

		TextView hashtagMostPopularCount = (TextView) view.findViewById(R.id.reporting_hashtag_mostpopular_count_value);
		hashtagMostPopularCount.setText(hashtagList.get(0).get("count").toString());

		TextView differentMusicGenres = (TextView) view.findViewById(R.id.reporting_musicgenres_total_value);
		differentMusicGenres.setText(mDbHelper.selectCount
				(LifeBoxContract.MusicGenres.COLUMN_NAME_MUSIC_GENRE, LifeBoxContract.MusicGenres.TABLE_NAME));

		TextView differentMovieGenres = (TextView) view.findViewById(R.id.reporting_moviegenres_total_value);
		differentMovieGenres.setText(mDbHelper.selectCount
				(LifeBoxContract.Movies.COLUMN_NAME_MOVIE_GENRE, LifeBoxContract.Movies.TABLE_NAME));

		ArrayList<HashMap<String, String>> musicGenreList = mDbHelper.selectGenreUsage
				(
						LifeBoxContract.MusicGenres.COLUMN_NAME_MUSIC_GENRE, LifeBoxContract.MusicGenres.TABLE_NAME, 1
				);

		TextView musicGenreMostPopular = (TextView) view.findViewById(R.id.reporting_musicgenres_mostpopular_value);
		musicGenreMostPopular.setText(musicGenreList.get(0).get("genre").toString());

		TextView musicGenreMostPopularCount = (TextView) view.findViewById(R.id.reporting_musicgenres_mostpopular_count_value);
		musicGenreMostPopularCount.setText(musicGenreList.get(0).get("count").toString());

		ArrayList<HashMap<String, String>> movieGenreList = mDbHelper.selectGenreUsage
				(
						LifeBoxContract.Movies.COLUMN_NAME_MOVIE_GENRE, LifeBoxContract.Movies.TABLE_NAME, 1
				);

		TextView movieGenreMostPopular = (TextView) view.findViewById(R.id.reporting_moviegenres_mostpopular_value);
		movieGenreMostPopular.setText(movieGenreList.get(0).get("genre").toString());

		TextView movieGenreMostPopularCount = (TextView) view.findViewById(R.id.reporting_moviegenres_mostpopular_count_value);
		movieGenreMostPopularCount.setText(movieGenreList.get(0).get("count").toString());

		HashMap<String, String> firstList = mDbHelper.selectExtremeTitle("MIN");

		TextView firstEntry = (TextView) view.findViewById(R.id.reporting_entry_first_value);
		firstEntry.setText(firstList.get("title"));

		TextView firstEntryDate = (TextView) view.findViewById(R.id.reporting_entry_first_date_value);
		firstEntryDate.setText(getDate(String.valueOf(mDbHelper.selectFirstUserDate())));

		HashMap<String, String> lastList = mDbHelper.selectExtremeTitle("MAX");

		TextView lastEntry = (TextView) view.findViewById(R.id.reporting_entry_last_value);
		lastEntry.setText(lastList.get("title"));

		TextView lastEntryDate = (TextView) view.findViewById(R.id.reporting_entry_last_date_value);
		lastEntryDate.setText(getDate(String.valueOf(mDbHelper.selectLastUserDate())));

		return view;
	}

	/**
	 * Gets the date from a timestamp string
	 * @param timestampString (String) the timestamp
	 * @return (String) the date.
	 */
	private String getDate(String timestampString)
	{
		Timestamp timestamp = new Timestamp(Long.parseLong(timestampString));
		Date date = new Date(timestamp.getTime());

		String dateString = new SimpleDateFormat("yyyy-MM-dd").format(date);

		return dateString;
	}
}
