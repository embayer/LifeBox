package de.lifebox.LifeBox;

import java.util.ArrayList;

/**
 * Holds system-wide constants
 * @version 0.1 24.06.13
 * @autor Markus Bayer
 */
public final class Constants
{
	//TODO set the package name prefix for all extras
	//TODO split in extra name, extra
	// the name of the package
	public static final String PACKAGE_NAME_PREFIX = "de.lifebox.LifeBox.";
	// defines a custom Intent action for the communication from UploadService to MetaFormActivity
	public static final String BROADCAST_ACTION_UPLOADRESPONSE = PACKAGE_NAME_PREFIX + "UPLOADRESPONSE";
	public static final String BROADCAST_ACTION_SEARCHRESPONSE = PACKAGE_NAME_PREFIX + "SEARCHRESPONSE";
	public static final String BROADCAST_ACTION_RELOADRESPONSE = PACKAGE_NAME_PREFIX + "RELOADRESPONSE";

	// caller identification
	public static final int CALLER_SIGN_IN_ACTIVITY = 101;
	public static final int CALLER_META_FORM_ACTIVITY = 102;

	// codes for the different MIME-Types
	public static final String MIME_TYPE_IMAGE = "image/jpeg";
	public static final String MIME_TYPE_VIDEO = "video/mp4";

	// codes for Thumbnails
	public static final String MIME_TYPE_IMAGE_THUMB = "image/thumbnail";
	public static final String MIME_TYPE_VIDEO_THUMB = "video/thumbnail";

	// codes for media types
	public static final String TYPE_FILE = "files";
	public static final String TYPE_MOVIE = "movies";
	public static final String TYPE_MUSIC = "music";
	public static final String TYPE_TEXT = "text";

	// codes for the extras
	// caller identification
	public static final String CALLER_EXTRA = "callerExtra";
	// mediatypes
	public static final String MEDIA_TYPE_EXTRA = "mediaTypeExtra";
	// metadata of created files from SelectTypeFragment
	public static final String FILE_URL_EXTRA = "fileUriExtra";
	public static final String MIME_TYPE_EXTRA = "mimeTypeExtra";
	public static final String TIME_STAMP_EXTRA = "timeStampExtra";
	public static final String THUMBNAIL_URL_EXTRA = "thumbnailUriExtra";
	public static final String CREATION_DATE_EXTRA = "creationDateExtra";
	// metadata from UploadService
	public static final String IS_THUMB_EXTRA = "isThumbExtra";
	public static final String DRIVE_ID_EXTRA = "driveId";
	public static final String DOWNLOAD_URL_EXTRA = "downloadUrlExtra";
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
	// hashtag from HashtagActivity
	public static final String HASHTAG_EXTRA = "hashtagExtra";
	// tags from TagsActivity
	public static final String TAG_SMILEY1_EXTRA = "tagSmiley1";
	public static final String TAG_SMILEY2_EXTRA = "tagSmiley2";
	public static final String TAG_SMILEY3_EXTRA = "tagSmiley3";
	public static final String TAG_SMILEY4_EXTRA = "tagSmiley4";
	public static final String TAG_SMILEY5_EXTRA = "tagSmiley5";
	public static final String TAG_SMILEY6_EXTRA = "tagSmiley6";
	public static final String TAG_SMILEY7_EXTRA = "tagSmiley7";
	public static final String TAG_SMILEY8_EXTRA = "tagSmiley8";
	public static final String TAG_SMILEY9_EXTRA = "tagSmiley9";
	public static final String TAG_SMILEY10_EXTRA = "tagSmiley10";

	public static final String TAG_LOVE_EXTRA = "tagLove";
	public static final String TAG_STAR_EXTRA = "tagStar";
	public static final String TAG_DISLIKE_EXTRA = "tagDislike";
	public static final String TAG_ACHIEVEMENT_EXTRA = "tagAchievement";
	public static final String TAG_WORK_EXTRA = "tagWork";
	public static final String TAG_FAMILY_EXTRA = "tagFamily";
	public static final String TAG_CHILD_EXTRA = "tagChild";
	public static final String TAG_PET_EXTRA = "tagPet";
	public static final String TAG_FRIENDS_EXTRA = "tagFriends";
	public static final String TAG_PARTY_EXTRA = "tagParty";
	public static final String TAG_OUTDOOR_EXTRA = "tagOutdoor";
	public static final String TAG_HOME_EXTRA = "tagHome";
	public static final String TAG_TRIP_EXTRA = "tagTrip";
	public static final String TAG_TRAVEL_EXTRA = "tagTravel";
	public static final String TAG_EVENT_EXTRA = "tagEvent";
	public static final String TAG_HOBBY_EXTRA = "tagHobby";
	public static final String TAG_SPORT_EXTRA = "tagSport";
	public static final String TAG_FOOD_EXTRA = "tagFood";
	public static final String TAG_CLOTH_EXTRA = "tagCloth";
	public static final String TAG_SHOPPING_EXTRA = "tagShopping";

	public static final String TAG_ARRAY_EXTRA = "tagArrayExtra";

	// offset from TimelineFragment
	public static final String OFFSET_EXTRA = PACKAGE_NAME_PREFIX + "offsetExtra";
	// database Lists from TimelineFragment
	public static final String ENTRY_INDEX_ARRAY_EXTRA = PACKAGE_NAME_PREFIX + "entryIndexArrayExtra";
	public static final String MEDIA_INDEX_ARRAY_EXTRA = PACKAGE_NAME_PREFIX + "mediaIndexArrayExtra";
	public static final String TYPES_INDEX_ARRAY_EXTRA = PACKAGE_NAME_PREFIX + "typesIndexArrayExtra";

	// database Lists from TimelineReloadService
	public static final String TIMELINE_ENTRIES_ARRAY_EXTRA = PACKAGE_NAME_PREFIX + "timelineEntriesArrayExtra";
	public static final String TIMELINE_ENTRIES_BUNDLE_EXTRA = PACKAGE_NAME_PREFIX + "timelineEntriesBundleExtra";

	/** Constructor is empty to prevent instantiating the Constants class. */
	public Constants() {}
}
