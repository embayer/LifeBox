package de.lifebox.LifeBox;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
	// databasehelper
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

		// initialize the databasehelper
		mDbHelper = new DbHelper(getActivity());
	}

	/** Called when the fragment is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.reporting, container, false);

		// initialize the ui elements and get the values from the database
		// entry sum
		TextView entriesTotal = (TextView) view.findViewById(R.id.reporting_entries_total_value);
		entriesTotal.setText(mDbHelper.selectCountEntries());

		// photo sum
		TextView imagesTotal = (TextView) view.findViewById(R.id.reporting_images_total_value);
		imagesTotal.setText(mDbHelper.selectCountFiles(Constants.MIME_TYPE_IMAGE));

		// video sum
		TextView videosTotal = (TextView) view.findViewById(R.id.reporting_videos_total_value);
		videosTotal.setText(mDbHelper.selectCountFiles(Constants.MIME_TYPE_VIDEO));

		// song sum
		TextView songsTotal = (TextView) view.findViewById(R.id.reporting_songs_total_value);
		songsTotal.setText(mDbHelper.selectCountMedia(Constants.TYPE_MUSIC));

		// movie sum
		TextView moviesTotal = (TextView) view.findViewById(R.id.reporting_movies_total_value);
		moviesTotal.setText(mDbHelper.selectCountMedia(Constants.TYPE_MOVIE));

		// text sum
		TextView textTotal = (TextView) view.findViewById(R.id.reporting_text_total_value);
		textTotal.setText(mDbHelper.selectCountMedia(Constants.TYPE_TEXT));

		// tags sum
		TextView tagsTotal = (TextView) view.findViewById(R.id.reporting_tags_total_value);
		tagsTotal.setText(mDbHelper.selectCount(LifeBoxContract.EntryTags._ID, LifeBoxContract.EntryTags.TABLE_NAME));

		// hashtags sum
		TextView hashtagsTotal = (TextView) view.findViewById(R.id.reporting_hashtags_total_value);
		hashtagsTotal.setText(mDbHelper.selectCount(LifeBoxContract.EntryHashtags._ID, LifeBoxContract.EntryHashtags.TABLE_NAME));

		ArrayList<HashMap<String, String>> tagList = mDbHelper.selectTagUsage(1);

		// most often used tag
		TextView tagMostPopular = (TextView) view.findViewById(R.id.reporting_tag_mostpopular_value);
		tagMostPopular.setText(tagList.get(0).get("tag").toString());

		// amount of appearance
		TextView tagMostPopularCount = (TextView) view.findViewById(R.id.reporting_tag_mostpopular_count_value);
		tagMostPopularCount.setText(tagList.get(0).get("count").toString());

		ArrayList<HashMap<String, String>> hashtagList = mDbHelper.selectHashtagUsage(1);

		// most often used hashtag
		TextView hashtagMostPopular = (TextView) view.findViewById(R.id.reporting_hashtag_mostpopular_value);
		hashtagMostPopular.setText(hashtagList.get(0).get("hashtag").toString());

		// amount of appearance
		TextView hashtagMostPopularCount = (TextView) view.findViewById(R.id.reporting_hashtag_mostpopular_count_value);
		hashtagMostPopularCount.setText(hashtagList.get(0).get("count").toString());

		// different music genres
		TextView differentMusicGenres = (TextView) view.findViewById(R.id.reporting_musicgenres_total_value);
		differentMusicGenres.setText(mDbHelper.selectCount
				(LifeBoxContract.MusicGenres.COLUMN_NAME_MUSIC_GENRE, LifeBoxContract.MusicGenres.TABLE_NAME));

		// different movie genres
		TextView differentMovieGenres = (TextView) view.findViewById(R.id.reporting_moviegenres_total_value);
		differentMovieGenres.setText(mDbHelper.selectCount
				(LifeBoxContract.Movies.COLUMN_NAME_MOVIE_GENRE, LifeBoxContract.Movies.TABLE_NAME));

		ArrayList<HashMap<String, String>> musicGenreList = mDbHelper.selectGenreUsage
				(
						LifeBoxContract.MusicGenres.COLUMN_NAME_MUSIC_GENRE, LifeBoxContract.MusicGenres.TABLE_NAME, 1
				);

		// most often used music genre
		TextView musicGenreMostPopular = (TextView) view.findViewById(R.id.reporting_musicgenres_mostpopular_value);
		musicGenreMostPopular.setText(musicGenreList.get(0).get("genre").toString());

		// amount of appearance
		TextView musicGenreMostPopularCount = (TextView) view.findViewById(R.id.reporting_musicgenres_mostpopular_count_value);
		musicGenreMostPopularCount.setText(musicGenreList.get(0).get("count").toString());

		ArrayList<HashMap<String, String>> movieGenreList = mDbHelper.selectGenreUsage
				(
						LifeBoxContract.Movies.COLUMN_NAME_MOVIE_GENRE, LifeBoxContract.Movies.TABLE_NAME, 1
				);

		// most often used movie genre
		TextView movieGenreMostPopular = (TextView) view.findViewById(R.id.reporting_moviegenres_mostpopular_value);
		movieGenreMostPopular.setText(movieGenreList.get(0).get("genre").toString());

		// amount of appearance
		TextView movieGenreMostPopularCount = (TextView) view.findViewById(R.id.reporting_moviegenres_mostpopular_count_value);
		movieGenreMostPopularCount.setText(movieGenreList.get(0).get("count").toString());

		HashMap<String, String> firstList = mDbHelper.selectExtremeTitle("MIN");

		// title of the oldest entry
		TextView firstEntry = (TextView) view.findViewById(R.id.reporting_entry_first_value);
		firstEntry.setText(firstList.get("title"));

		// date of the oldest entry
		TextView firstEntryDate = (TextView) view.findViewById(R.id.reporting_entry_first_date_value);
		firstEntryDate.setText(getDate(String.valueOf(mDbHelper.selectFirstUserDate())));

		HashMap<String, String> lastList = mDbHelper.selectExtremeTitle("MAX");

		// title of the newest entry
		TextView lastEntry = (TextView) view.findViewById(R.id.reporting_entry_last_value);
		lastEntry.setText(lastList.get("title"));

		// date of the newest entry
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
