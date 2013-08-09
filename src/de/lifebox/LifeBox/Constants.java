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
	public static final int CALLER_TIMELINE_DETAIL_ACTIVITY = 103;

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
	public static final String TAG_SMILEY1_EXTRA = "tag_smiley1";
	public static final String TAG_SMILEY2_EXTRA = "tag_smiley2";
	public static final String TAG_SMILEY3_EXTRA = "tag_smiley3";
	public static final String TAG_SMILEY4_EXTRA = "tag_smiley4";
	public static final String TAG_SMILEY5_EXTRA = "tag_smiley5";
	public static final String TAG_SMILEY6_EXTRA = "tag_smiley6";
	public static final String TAG_SMILEY7_EXTRA = "tag_smiley7";
	public static final String TAG_SMILEY8_EXTRA = "tag_smiley8";
	public static final String TAG_SMILEY9_EXTRA = "tag_smiley9";
	public static final String TAG_SMILEY10_EXTRA = "tag_smiley10";

	public static final String TAG_LOVE_EXTRA = "tag_love";
	public static final String TAG_STAR_EXTRA = "tag_star";
	public static final String TAG_DISLIKE_EXTRA = "tag_dislike";
	public static final String TAG_ACHIEVEMENT_EXTRA = "tag_achievement";
	public static final String TAG_WORK_EXTRA = "tag_work";
	public static final String TAG_FAMILY_EXTRA = "tag_family";
	public static final String TAG_CHILD_EXTRA = "tag_child";
	public static final String TAG_PET_EXTRA = "tag_pet";
	public static final String TAG_FRIENDS_EXTRA = "tag_friends";
	public static final String TAG_PARTY_EXTRA = "tag_party";
	public static final String TAG_OUTDOOR_EXTRA = "tag_outdoor";
	public static final String TAG_HOME_EXTRA = "tag_home";
	public static final String TAG_TRIP_EXTRA = "tag_trip";
	public static final String TAG_TRAVEL_EXTRA = "tag_travel";
	public static final String TAG_EVENT_EXTRA = "tag_event";
	public static final String TAG_HOBBY_EXTRA = "tag_hobby";
	public static final String TAG_SPORT_EXTRA = "tag_sport";
	public static final String TAG_FOOD_EXTRA = "tag_food";
	public static final String TAG_CLOTH_EXTRA = "tag_cloth";
	public static final String TAG_SHOPPING_EXTRA = "tag_shopping";

	public static final String TAG_ARRAY_EXTRA = "tagArrayExtra";

	// offset from TimelineFragment
	public static final String OFFSET_EXTRA = PACKAGE_NAME_PREFIX + "offsetExtra";
	// entryId from TimelineFragment
	public static final String ENTRY_ID_EXTRA = PACKAGE_NAME_PREFIX + "entryIdExtra";
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
