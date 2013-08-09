package de.lifebox.LifeBox;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * Detailed view for entries of the type mediafile (video).
 * @version 0.1 06.08.13
 * @autor Markus Bayer
 */
public class TimelineDetailFileVideoActivity extends TimelineDetailActivity
{
	private final static String TAG = "TimelineDetailFileVideoActivity";

	private DbHelper mDbHelper;

	// the mediafile
	private MediaFile video;

	// the ui elements
	private ImageView thumbnailIV;;
	private ImageButton playIB;

	// play Button
	Button.OnClickListener mPlayListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			// the gallery activity expects the prefix 'file://'
			Uri uri = Uri.parse("file://" + video.getOfflinePathFile());
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			intent.setDataAndType(uri, video.getFiletype());
			startActivity(intent);
		}
	};

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// get the mediafile from the database
		mDbHelper = new DbHelper(getBaseContext());

		video = mDbHelper.selectMediaFile(super.getMediaId());

		// find the layout file for the specific part
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View childLayout = inflater.inflate(R.layout.timelinedetailvideofile, (ViewGroup) findViewById(R.layout.timelinedetail));

		// add it to the parent container
		super.getSpecificLL().addView(childLayout);

		// initialize the video
		thumbnailIV = (ImageView) childLayout.findViewById(R.id.videofiledetail_imageview);
		thumbnailIV.setImageDrawable(Drawable.createFromPath(video.getOfflinePathThumbnail()));

		// setup the play button
		playIB = (ImageButton) childLayout.findViewById(R.id.videofiledetail_play_imagebutton);
		playIB.setOnClickListener(mPlayListener);
	}
}