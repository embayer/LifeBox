package de.lifebox.LifeBox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Fragment for choosing what kind of entry the user want to generate.
 * @author Markus Bayer
 * @version 0.1 21.06.2013
 */
public class InputFragment extends Fragment
{
	private static String TAG = "InputFragment";

	// codes for onActivityResult
	private static final int ACTION_TAKE_PHOTO = 1;
	private static final int ACTION_TAKE_PHOTO_S = 2;
	private static final int ACTION_TAKE_VIDEO = 3;
	private static final int ACTION_PICK_PHOTO = 4;

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

	// path to the temporary image that will be deleted
	private String mPhotoToDeletePath;

	// timestamp (NOT unix timestamp) of the last created file
	private String mCurrentTimeStamp;

	// pre- and suffixes to assemble filenames
	private static final String FILE_PREFIX = "LB_";
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private static final String MP4_FILE_PREFIX = "VID_";
	private static final String MP4_FILE_SUFFIX = ".mp4";
	private static final String THUMBNAIL_SUFFIX = "_THM";

	// instance of AlbumstoragedirFactory helper class to generate valid folder files
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

	// OnClickListeners for the buttons
	Button.OnClickListener mTakePicOnClickListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			dispatchTakePictureIntent(ACTION_TAKE_PHOTO);
		}
	};

	Button.OnClickListener mTakeVidOnClickListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			dispatchTakeVideoIntent(ACTION_TAKE_VIDEO);
		}
	};

	Button.OnClickListener mTextFormOnClickListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent(getActivity(), TextFormActivity.class);
			startActivity(intent);
		}
	};

	Button.OnClickListener mListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent(getActivity(), MetaFormActivity.class);
			startActivity(intent);
		}
	};

	Button.OnClickListener mSearchMoviesListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent(getActivity(), SearchMovieActivity.class);
			intent.putExtra(Constants.SEARCH_MEDIA_TYPE_EXTRA, Constants.TYPE_MOVIE);

			startActivity(intent);
		}
	};

	Button.OnClickListener mSearchMusicListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent(getActivity(), SearchMusicActivity.class);
			intent.putExtra(Constants.SEARCH_MEDIA_TYPE_EXTRA, Constants.TYPE_MUSIC);

			startActivity(intent);
		}
	};

	Button.OnClickListener mPickImageOnClickListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			dispatchPickImageIntent(ACTION_PICK_PHOTO);
		}
	};

	Button.OnClickListener mDbBackupOnClickListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent(getActivity().getBaseContext(), BackupDbService.class);
			getActivity().startService(intent);
		}
	};

	/**
	 * Constructor
	 * @return InputFragment instance
	 */
	public static final InputFragment newInstance()
	{
		InputFragment f = new InputFragment();
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
		View view = inflater.inflate(R.layout.input, container, false);

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

		Button textBtn = (Button) view.findViewById(R.id.text);
		textBtn.setOnClickListener(mTextFormOnClickListener);

//		Button fileBtn = (Button) view.findViewById(R.id.file);
//		fileBtn.setOnClickListener(mListener);

		Button movieBtn = (Button) view.findViewById(R.id.movie);
		movieBtn.setOnClickListener(mSearchMoviesListener);

		Button musicBtn = (Button) view.findViewById(R.id.music);
		musicBtn.setOnClickListener(mSearchMusicListener);

		Button fileBtn = (Button) view.findViewById(R.id.file);
		setBtnListenerOrDisable(fileBtn,
				mPickImageOnClickListener,
				Intent.ACTION_PICK);

		return view;
	}

	/**
	 * Called when the activity you launched returns
	 * with the requestCode, the resultCode, and any additional data from it.
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
					// remember the old files path in order to delete it later
					mPhotoToDeletePath = mCurrentPhotoPath;

					// reduce the image size
					mCurrentPhotoPath = scale(mPhotoToDeletePath);

					// delete the temporary file
					File file = new File(mPhotoToDeletePath);
					file.delete();

					// create a thumbnail of the image file
					String imageThumbnail = createImageThumbnail(mCurrentPhotoPath);

					// start an intent to navigate to the MetaFormActivity
					Intent intent = new Intent(getActivity(), MetaFormActivity.class);
					Log.e("path", mCurrentPhotoPath);
					intent.putExtra(Constants.MEDIA_TYPE_EXTRA, Constants.TYPE_FILE);
					intent.putExtra(Constants.FILE_URL_EXTRA, mCurrentPhotoPath);
					intent.putExtra(Constants.MIME_TYPE_EXTRA, Constants.MIME_TYPE_IMAGE);
					intent.putExtra(Constants.CREATION_DATE_EXTRA, mCurrentTimeStamp);
					intent.putExtra(Constants.THUMBNAIL_URL_EXTRA, imageThumbnail);
					getActivity().startActivity(intent);
				}
				//TODO Fehlerbehandlung
				break;
			case ACTION_TAKE_VIDEO:
				if(resultCode == Activity.RESULT_OK)
				{
					// create a thumbnail of the video file
					String videoThumbnail = createVideoThumbnail(mCurrentVideoPath);

					// start an intent to navigate to the MetaFormActivity
					Intent intent = new Intent(getActivity(), MetaFormActivity.class);
					intent.putExtra(Constants.MEDIA_TYPE_EXTRA, Constants.TYPE_FILE);
					intent.putExtra(Constants.FILE_URL_EXTRA, mCurrentVideoPath);
					intent.putExtra(Constants.MIME_TYPE_EXTRA, Constants.MIME_TYPE_VIDEO);
					intent.putExtra(Constants.CREATION_DATE_EXTRA, mCurrentTimeStamp);
					intent.putExtra(Constants.THUMBNAIL_URL_EXTRA, videoThumbnail);
					getActivity().startActivity(intent);
				}
				break;
			case ACTION_PICK_PHOTO:
				if(resultCode == Activity.RESULT_OK)
				{
					// get the path to the selected image
					Uri selectedImage = data.getData();
					String[] filePathColumn = { MediaStore.Images.Media.DATA };

					Cursor cursor = getActivity().getContentResolver().query(selectedImage,
							filePathColumn, null, null, null);
					cursor.moveToFirst();

					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					// String picturePath contains the path of selected Image
					String picturePath = cursor.getString(columnIndex);
					cursor.close();

					// get the original timestamp from the picturePath (which is like: '/storage/emulated/0/DCIM/Camera/IMG_20130712_112032.jpg')
					String creationDate = picturePath.substring(picturePath.length() - 19, picturePath.length() - 4);

					// set the current timestamp
					String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
					mCurrentTimeStamp = timeStamp;

					// scale the selected image
					mCurrentPhotoPath = scale(picturePath);

					// create a thumbnail for the image
					String imageThumbnail = createImageThumbnail(picturePath);

					// start an intent to navigate to the MetaFormActivity
					Intent intent = new Intent(getActivity(), MetaFormActivity.class);

					// set the extras
					intent.putExtra(Constants.MEDIA_TYPE_EXTRA, Constants.TYPE_FILE);
					intent.putExtra(Constants.FILE_URL_EXTRA, mCurrentPhotoPath);
					intent.putExtra(Constants.MIME_TYPE_EXTRA, Constants.MIME_TYPE_IMAGE);
					intent.putExtra(Constants.CREATION_DATE_EXTRA, creationDate);
					intent.putExtra(Constants.THUMBNAIL_URL_EXTRA, imageThumbnail);
					intent.putExtra(Constants.TIME_STAMP_EXTRA, mCurrentTimeStamp);
					getActivity().startActivity(intent);
				}
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

	/**
	 * Set a buttonlistener or disable the button.
	 * @param btn (Button) the button to setup
	 * @param onClickListener (Button.OnClickListener) the listener
	 * @param intentName (String) the intent to perform
	 */
	private void setBtnListenerOrDisable(
			Button btn,
			Button.OnClickListener onClickListener,
			String intentName)
	{
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
		mCurrentTimeStamp = timeStamp;

		// Create an image file name
		String imageFileName = FILE_PREFIX + JPEG_FILE_PREFIX + timeStamp;

		// request a storage dir
		File albumF = getAlbumDir(Constants.MIME_TYPE_IMAGE);
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
		mCurrentTimeStamp = timeStamp;

		// Create a video file name
		String videoFileName = FILE_PREFIX + MP4_FILE_PREFIX + timeStamp;

		// request a storage directory
		File albumF = getAlbumDir(Constants.MIME_TYPE_VIDEO);
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

	/**
	 * Write a bitmap to a JPEG-image
	 * @param sourceBitmap (Bitmap) the Bitmap used as source
	 * @param targetFile (File) the output File
	 * @return (String) path to the output File
	 */
	private String bitmapToJPEG(Bitmap sourceBitmap, File targetFile)
	{
		// wright the file
		OutputStream out = null;
		try
		{
			out = new BufferedOutputStream(new FileOutputStream(targetFile));
			sourceBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
		}
		catch(IOException e)
		{
			Toast.makeText(getActivity(), "An error occured while creating the image file. Please try again.", Toast.LENGTH_LONG);
		}
		finally
		{
			if(null != out)
			{
				try
				{
					// close the OutputStream
					out.close();
				}
				catch (IOException e)
				{
					Toast.makeText(getActivity(), "An error occurred while creating the image file. Please try again.", Toast.LENGTH_LONG);
				}
			}
		}

		return targetFile.getAbsolutePath();
	}

	/**
	 * Creates a new scaled down image of the image given in the parameter in order to reduce up- and downloadtime.
	 * @param path (String) to the picture that should be scaled.
	 * @return newPath (String) to the newly created image.
	 */
	private String scale(String path)
	{
		// get the devices display size in pixels
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int deviceWidth = size.x;
		int deviceHeight = size.y;

		// get the size of the image
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, bmOptions);
		int photoWidth = bmOptions.outWidth;
		int photoHeight = bmOptions.outHeight;

		// figure out which way needs to be reduced less
		int scaleFactor = 1;
		if((deviceWidth > 0) || (deviceHeight > 0))
		{
			scaleFactor = Math.min(photoWidth/deviceWidth, photoHeight/deviceHeight);
		}

		// set bitmap options to scale the image decode target
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		// decode the JPEG file into a Bitmap
		Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);

		// get the folder where the new file should be stored
		File albumF = getAlbumDir(Constants.MIME_TYPE_IMAGE);

		// set the new filename
		String imageFileName = FILE_PREFIX + JPEG_FILE_PREFIX + mCurrentTimeStamp + JPEG_FILE_SUFFIX;

		// create the new file
		File file = new File(albumF, imageFileName);

		// wright the file
		String resultPath = bitmapToJPEG(bitmap, file);

		return resultPath;
	}

	/**
	 * Create a thumbnail image from a given image.
	 * @param path (String) to the image of which the thumbnail should be created
	 * @return (String) path to the created thumbnail image
	 */
	private String createImageThumbnail(String path)
	{
		// get a bitmap for the specified file
		Bitmap bitmap = BitmapFactory.decodeFile(path);

		// get the folder where the new file should be stored
		File albumF = getAlbumDir(Constants.MIME_TYPE_IMAGE_THUMB);

		// set the new filename
		String imageFileName = FILE_PREFIX + JPEG_FILE_PREFIX + mCurrentTimeStamp + THUMBNAIL_SUFFIX + JPEG_FILE_SUFFIX;

		// create the new file
		File file = new File(albumF, imageFileName);

		// create the thumbnail
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, 96, 96);

		String resultPath = bitmapToJPEG(bitmap, file);

		return resultPath;
	}

	/**
	 * Create a thumbnail image from a given image.
	 * @param path (String) to the image of which the thumbnail should be created
	 * @return (String) path to the created thumbnail image
	 */
	private String createVideoThumbnail(String path)
	{
		// get the folder where the new file should be stored
		File albumF = getAlbumDir(Constants.MIME_TYPE_VIDEO_THUMB);

		// set the new filename
		String imageFileName = FILE_PREFIX + MP4_FILE_PREFIX + mCurrentTimeStamp + THUMBNAIL_SUFFIX + JPEG_FILE_SUFFIX;

		// create the new file
		File file = new File(albumF, imageFileName);

		Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MICRO_KIND);

		// wright the file
		String resultPath = bitmapToJPEG(bitmap, file);

		return resultPath;
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
		// setup the intent
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		switch(actionCode)
		{
			case ACTION_TAKE_PHOTO:
				File f = null;

				try
				{
					f = setUpImageFile();
					mCurrentPhotoPath = f.getAbsolutePath();
					takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
				}
				catch (IOException e)
				{
					Log.e(TAG, e.getMessage());

					// free variables
					f = null;
					mCurrentPhotoPath = null;
					mCurrentTimeStamp = null;
				}
				break;

			default:
				break;
		}

		// call the camera app
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
					Log.e(TAG, e.getMessage());
					// free variables
					f = null;
					mCurrentVideoPath = null;
					mCurrentTimeStamp = null;
				}
				break;

			default:
				break;
		}

		// call the camera
		startActivityForResult(takeVideoIntent, actionCode);
	}

	/** Call the ImagePicker dialog. */
	private void dispatchPickImageIntent(int actionCode)
	{
		Intent pickImageIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(pickImageIntent, actionCode);
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