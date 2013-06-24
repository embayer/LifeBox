package de.lifebox.LifeBox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.drive.Drive;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Fragment for choosing what kind of entry the user want to generate.
 * @author Markus Bayer
 * @version 0.1 21.06.2013
 */
public class SelectTypeFragment extends Fragment
{
	// codes for onActivityResult
	private static final int ACTION_TAKE_PHOTO = 1;
	private static final int ACTION_TAKE_PHOTO_S = 2;
	private static final int ACTION_TAKE_VIDEO = 3;

	// codes for the different MIME-Types
	private static final String MIME_TYPE_IMAGE = "image";
	private static final String MIME_TYPE_VIDEO = "video";

	// keys for image display
	private static final String BITMAP_STORAGE_KEY = "viewbitmap";
	private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
	private ImageView mImageView;
	private Bitmap mImageBitmap;

	// keys for video display
	private static final String VIDEO_STORAGE_KEY = "viewvideo";
	private static final String VIDEOVIEW_VISIBILITY_STORAGE_KEY = "videoviewvisibility";
	private VideoView mVideoView;
	private Uri mVideoUri;

	// paths to the last created media
	private String mCurrentPhotoPath;
	private String mCurrentVideoPath;

	// pre- and suffixes to assemble filenames
	private static final String FILE_PREFIX = "LB_";
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private static final String MP4_FILE_PREFIX = "VID_";
	private static final String MP4_FILE_SUFFIX = ".mp4";

	// instance of AlbumstoragedirFactory helper class to generate valid folder files
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

	// OnClickListeners for the buttons
	Button.OnClickListener mTakePicOnClickListener =
			new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					dispatchTakePictureIntent(ACTION_TAKE_PHOTO);
				}
			};

	Button.OnClickListener mTakeVidOnClickListener =
			new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					dispatchTakeVideoIntent(ACTION_TAKE_VIDEO);
				}
			};

	/**
	 * Constructor
	 * @return SelectTypeFragment instance
	 */
	public static final SelectTypeFragment newInstance()
	{
		SelectTypeFragment f = new SelectTypeFragment();
		Bundle bdl = new Bundle(1);
		f.setArguments(bdl);
		return f;
	}

	/** Called when the fragment is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mAlbumStorageDirFactory = new AlbumStorageDirFactory();
	}

	/**
	 * Called when the fragment is first created.
	 * Creates and returns the view hierarchy associated with the fragment.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.selecttype, container, false);

		mImageView = (ImageView) view.findViewById(R.id.imageview);
		mVideoView = (VideoView) view.findViewById(R.id.videoview);
		mImageBitmap = null;
		mVideoUri = null;

		// add the listeners to the buttons
		Button picBtn = (Button) view.findViewById(R.id.picture);
		setBtnListenerOrDisable(
				picBtn,
				mTakePicOnClickListener,
				MediaStore.ACTION_IMAGE_CAPTURE
		);

		Button vidBtn = (Button) view.findViewById(R.id.video);
		setBtnListenerOrDisable(
				vidBtn,
				mTakeVidOnClickListener,
				MediaStore.ACTION_VIDEO_CAPTURE
		);

		return view;
	}

	/**
	 * Called when an activity you launched exits.
	 * Giving you the requestCode you started it with,
	 * the resultCode it returned, and any additional data from it.
	 */
	@Override
	public void onActivityResult(final int requestCode, final int resultCode, final Intent data)
	{
		// decide by request code what to do
		switch(requestCode)
		{
			case ACTION_TAKE_PHOTO:
				if(resultCode == Activity.RESULT_OK)
				{
					Intent intent = new Intent(getActivity(), UploadService.class);
					intent.putExtra("imgUri", mCurrentPhotoPath);
					getActivity().startService(intent);
				}
				//TODO Fehlerbehandlung
				break;
			default:
				//TODO default case
		}
	}

	/** Lifecycle callback to let the image survive an orientation change. */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
		outState.putParcelable(VIDEO_STORAGE_KEY, mVideoUri);
		outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null) );
		outState.putBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY, (mVideoUri != null) );
		super.onSaveInstanceState(outState);
	}

//	@Override
//	protected void onRestoreInstanceState(Bundle savedInstanceState) {
//		super.onRestoreInstanceState(savedInstanceState);
//		mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
//		mVideoUri = savedInstanceState.getParcelable(VIDEO_STORAGE_KEY);
//		mImageView.setImageBitmap(mImageBitmap);
//		mImageView.setVisibility(
//				savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ?
//						ImageView.VISIBLE : ImageView.INVISIBLE
//		);
//		mVideoView.setVideoURI(mVideoUri);
//		mVideoView.setVisibility(
//				savedInstanceState.getBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY) ?
//						ImageView.VISIBLE : ImageView.INVISIBLE
//		);
//	}

	/** Set a buttonlistener or disable the button */
	private void setBtnListenerOrDisable(
			Button btn,
			Button.OnClickListener onClickListener,
			String intentName
	) {
		if (isIntentAvailable(getActivity(), intentName)) {
			btn.setOnClickListener(onClickListener);
		} else {
			btn.setText(
					getText(R.string.cannot).toString() + " " + btn.getText());
			btn.setClickable(false);
		}
	}

	/**
	 * Check if the requested intent (camera, video...) is available on the device.
	 * @param context the context to the calling activity
	 * @param action the request
	 * @return true if the requested intent is available or false if not
	 */
	public static boolean isIntentAvailable(Context context, String action)
	{
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list =
				packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	/**
	 * Getter for the storage dir.
	 * @param mimeType the MIME-Type of the file that should be stored
	 * @return storage dir File if possible or null if not
	 */
	private File getAlbumDir(String mimeType)
	{
		File storageDir = null;

		// external storage mounted?
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
		{
			// call the factory
			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(mimeType);

			// is there already the requested directory
			if (null != storageDir)
			{
				// no -> create it
				if (! storageDir.mkdirs())
				{
					// success?
					if (! storageDir.exists()){
						Log.e("LifeBox", "failed to create directory");
						//TODO makeToast
						return null;
					}
				}
			}

		}
		else
		{
			//TODO makeToast
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
		}

		return storageDir;
	}

	/**
	 * Create an image file.
	 * @return the created image File
	 */
	private File createImageFile() throws IOException
	{
		// create a timestamp
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

		// Create an image file name
		String imageFileName = FILE_PREFIX + JPEG_FILE_PREFIX + timeStamp;

		// request a storage dir
		File albumF = getAlbumDir(MIME_TYPE_IMAGE);
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);

		return imageF;
	}

	/**
	 * Create the image File, set the path to it.
	 * @return the image File
	 */
	private File setUpImageFile() throws IOException {

		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();

		return f;
	}

	/**
	 * Create an video File.
	 * @return the created video File
	 */
	private File createVideoFile() throws IOException
	{
		// create a timestamp
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

		// Create a video file name
		String videoFileName = FILE_PREFIX + MP4_FILE_PREFIX + timeStamp;

		// request a storage directory
		File albumF = getAlbumDir(MIME_TYPE_VIDEO);
		File videoF = File.createTempFile(videoFileName, MP4_FILE_SUFFIX, albumF);
		return videoF;
	}

	/**
	 * Create the image File, set the path to it.
	 * @return the video File
	 */
	private File setUpVideoFile() throws IOException
	{
		File f = createVideoFile();
		mCurrentPhotoPath = f.getAbsolutePath();

		return f;
	}

	/** Scale down the picture to devices screen resolution in order to save ram. */
	private void setPic() {

		// There isn't enough memory to open up more than a couple camera photos
		// So pre-scale the target bitmap into which the file is decoded

		/* Get the size of the ImageView */
		int targetW = mImageView.getWidth();
		int targetH = mImageView.getHeight();

		// Get the size of the image
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		// Figure out which way needs to be reduced less
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW/targetW, photoH/targetH);
		}

		// Set bitmap options to scale the image decode target
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		// Decode the JPEG file into a Bitmap
		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		// Associate the Bitmap to the ImageView
		mImageView.setImageBitmap(bitmap);
		mVideoUri = null;
		mImageView.setVisibility(View.VISIBLE);
		mVideoView.setVisibility(View.INVISIBLE);
	}

	/** Call the device media server to index the created image. */
	private void galleryAddPic() {
		Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		File f = new File(mCurrentPhotoPath);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		getActivity().sendBroadcast(mediaScanIntent);
	}

	/** Call the camera app. */
	private void dispatchTakePictureIntent(int actionCode)
	{
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		switch(actionCode) {
			case ACTION_TAKE_PHOTO:
				File f = null;

				try
				{
					f = setUpImageFile();
					mCurrentPhotoPath = f.getAbsolutePath();
					takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
				}
				catch (IOException e) {
					//TODO makeToast
					e.printStackTrace();
					f = null;
					mCurrentPhotoPath = null;
				}
				break;

			default:
				break;
		}
		startActivityForResult(takePictureIntent, actionCode);
	}

	/** Call the video app. */
	private void dispatchTakeVideoIntent(int actionCode)
	{
		Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

		switch(actionCode)
		{
			case ACTION_TAKE_VIDEO:
				File f = null;

				try
				{
					f = setUpVideoFile();
					mCurrentVideoPath = f.getAbsolutePath();
					takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
				}
				catch(IOException e)
				{
					e.printStackTrace();
					f = null;
					mCurrentVideoPath = null;
				}
				break;

			default:
				break;
		}
		startActivityForResult(takeVideoIntent, actionCode);
	}

	//START unused methods----------------------------------------------------------------------------------------------
	private void handleSmallCameraPhoto(Intent intent) {
		Bundle extras = intent.getExtras();
		mImageBitmap = (Bitmap) extras.get("data");
		mImageView.setImageBitmap(mImageBitmap);
		mVideoUri = null;
		mImageView.setVisibility(View.VISIBLE);
		mVideoView.setVisibility(View.INVISIBLE);
	}

	private void handleCameraPhoto() {

		if (mCurrentPhotoPath != null) {
			setPic();
			galleryAddPic();
			mCurrentPhotoPath = null;
		}

	}

	private void handleCameraVideo(Intent intent) {
		mVideoUri = intent.getData();
		mVideoView.setVideoURI(mVideoUri);

		mImageBitmap = null;
		mVideoView.setVisibility(View.VISIBLE);
		mImageView.setVisibility(View.INVISIBLE);
	}
	//END unused methods------------------------------------------------------------------------------------------------
}