package de.lifebox.LifeBox;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import com.google.api.services.drive.model.File;

/**
 * Detailed view for entries of the type mediafile (image).
 * @version 0.1 06.08.13
 * @autor Markus Bayer
 */
public class TimelineDetailFileImageActivity extends TimelineDetailActivity
{
	private final static String TAG = "TimelineDetailFileImageActivity";

	private DbHelper mDbHelper;

	// the mediafile
	private MediaFile image;

	// the ui elements
	private ImageButton portraitImageIB;
	private ImageButton landscapeImageIB;

	// ImageButtons
	Button.OnClickListener mImageButtonListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			// the gallery activity expects the prefix 'file://'
			Uri uri = Uri.parse("file://" + image.getOfflinePathFile());
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			intent.setDataAndType(uri, image.getFiletype());
			startActivity(intent);


		}
	};

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// get the mediafile from the database
		mDbHelper = new DbHelper(getBaseContext());

		image = mDbHelper.selectMediaFile(super.getMediaId());

		// find the layout file for the specific part
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View childLayout = inflater.inflate(R.layout.timelinedetailimagefile, (ViewGroup) findViewById(R.layout.timelinedetail));

		// add it to the parent container
		super.getSpecificLL().addView(childLayout);

		// initialize the image
		portraitImageIB = (ImageButton) childLayout.findViewById(R.id.imagefiledetail_portrait_imagebutton);
		portraitImageIB.setOnClickListener(mImageButtonListener);
		landscapeImageIB = (ImageButton) childLayout.findViewById(R.id.imagefiledetail_landscape_imagebutton);
		landscapeImageIB.setOnClickListener(mImageButtonListener);

		// check if the image orientation is portrait or landscape
		Drawable drawable = Drawable.createFromPath(image.getOfflinePathFile());
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();

		if(width < height)
		{
			portraitImageIB.setVisibility(View.VISIBLE);
			landscapeImageIB.setVisibility(View.INVISIBLE);
			portraitImageIB.setImageDrawable(Drawable.createFromPath(image.getOfflinePathFile()));
		}
		else
		{
			landscapeImageIB.setVisibility(View.VISIBLE);
			portraitImageIB.setVisibility(View.INVISIBLE);
			landscapeImageIB.setImageDrawable(drawable);
		}
	}
}