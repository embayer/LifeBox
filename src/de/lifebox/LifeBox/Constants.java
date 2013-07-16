package de.lifebox.LifeBox;

/**
 * Holds system-wide constants
 * @version 0.1 24.06.13
 * @autor Markus Bayer
 */
public final class Constants
{
	// defines a custom Intent action for the communication from UploadService to MetaFormActivity
	public static final String BROADCAST_ACTION_UPLOADRESPONSE = "de.lifebox.LifeBox.UPLOADRESPONSE";
	public static final String BROADCAST_ACTION_SEARCHRESPONSE = "de.lifebox.LifeBox.SEARCHRESPONSE";

	// codes for the different MIME-Types
	public static final String MIME_TYPE_IMAGE = "image/jpeg";
	public static final String MIME_TYPE_VIDEO = "video/mp4";

	public static final String TYPE_MOVIE = "movie";
	public static final String TYPE_MUSIC = "song";

	// codes for Thumbnails
	public static final String MIME_TYPE_IMAGE_THUMB = "image/thumbnail";
	public static final String MIME_TYPE_VIDEO_THUMB = "video/thumbnail";

	// codes for the extras
	// metadata of created files from SelectTypeFragment
	public static final String FILE_URL_EXTRA = "fileUriExtra";
	public static final String MIME_TYPE_EXTRA = "mimeTypeExtra";
	public static final String TIME_STAMP_EXTRA = "timeStampExtra";
	public static final String THUMBNAIL_URI_EXTRA = "thumbnailUriExtra";
	public static final String CREATION_DATE_EXTRA = "creationDateExtra";
	// hashtag from HashtagActivity
	public static final String HASHTAG_EXTRA = "hashtagExtra";
	// search media extras from SearchMovieActivity || SearchMusicActivity
	public static final String SEARCH_MEDIA_QUERY_EXTRA = "searchMediaQueryExtra";
	public static final String SEARCH_MEDIA_TYPE_EXTRA = "searchMediaTypeExtra";
	// from FetchJsonService
	public static final String MEDIA_RESULT_EXTRA = "mediaResultExtra";
	// movie from SearchMovieActivity
	public static final String MOVIE_TITLE_EXTRA = "movieTitleExtra";
	public static final String MOVIE_DESCRIPTION_EXTRA = "movieDescriptionExtra";
	public static final String MOVIE_DIRECTOR_EXTRA = "movieDirectorExtra";
	public static final String MOVIE_GENRE_EXTRA = "movieGenreExtra";
	public static final String MOVIE_RELEASE_DATE_EXTRA = "movieReleaseDateExtra";
	public static final String MOVIE_THUMBNAIL_URL_EXTRA = "movieThumbnailExtra";
	// music from SearchMusicActivity
	public static final String MUSIC_ARTIST_EXTRA = "musicArtistExtra";
	public static final String MUSIC_ALBUM_EXTRA = "musicAlbumExtra";
	public static final String MUSIC_REALEASE_DATE_EXTRA = "musicReleaseDateExtra";
	public static final String MUSIC_THUMBNAIL_URL_EXTRA = "musicThumbnailExtra";
	public static final String MUSIC_TRACK_EXTRA = "musicTrackExtra";
	public static final String MUSIC_GENRE_EXTRA = "musciGenreExtra";
	// text from TextForm
	public static final String TEXT_EXTRA = "textExtra";

	/** Constructor is empty to prevent instantiating the Constants class. */
	public Constants() {}
}
