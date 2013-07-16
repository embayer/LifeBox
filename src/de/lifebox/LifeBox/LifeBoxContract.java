package de.lifebox.LifeBox;

import android.provider.BaseColumns;

/**
 * Database description class
 * @version 0.1 25.06.13
 * @autor Markus Bayer
 */
public final class LifeBoxContract
{
	/** Constructor is empty to prevent instantiating the Contract class. */
	public LifeBoxContract() {}

	// inner classes that defines the table contents
	// the entities
	/** The entries on the timeline. */
	public static abstract class Entries implements BaseColumns
	{
		public static final String TABLE_NAME = "entries";
		public static final String _ID = "_id";

		public static final String COLUMN_NAME_MEDIA_ID = "media_id";   				// (table name)
		public static final String COLUMN_NAME_TYPES_ID = "types_id";
		public static final String COlUMN_NAME_TITLE = "title";
		public static final String COlUMN_NAME_DESCRIPTION = "description";
		public static final String COlUMN_NAME_USER_DATE = "user_date";
		public static final String COlUMN_NAME_CREATION_DATE = "creation_date";
		public static final String COlUMN_NAME_MODIFICATION_DATE = "modification_date";
	}

	// the possible types
	/** The media files. (Images, Videos are of the type file). */
	public static abstract class Files implements BaseColumns
	{
		public static final String TABLE_NAME = "files";
		public static final String _ID = "_id";

		public static final String COLUMN_NAME_FILETYPES_ID = "filetypes_id";
	}

	/** The media text. */
	public static abstract class Text implements BaseColumns
	{
		public static final String TABLE_NAME = "text";
		public static final String _ID = "_id";

		public static final String COLUMN_NAME_TEXT = "text";
	}

	/** The media music. */
	public static abstract class Music implements BaseColumns
	{
		public static final String TABLE_NAME = "music";
		public static final String _ID = "_id";

		public static final String COLUMN_NAME_TRACK = "track";
		public static final String COLUMN_NAME_ARTISTS_ID = "artists_id";
		public static final String COLUMN_NAME_ALBUMS_ID = "albums_id";
	}

	/** The type movie. */
	public static abstract class Movies implements BaseColumns
	{
		public static final String TABLE_NAME = "movies";
		public static final String _ID = "_id";

		public static final String COLUMN_NAME_TITLE = "title";
		public static final String COLUMN_NAME_DIRECTOR = "director";
		public static final String COLUMN_NAME_DESCRIPTION = "description";
		public static final String COLUMN_NAME_MOVIE_GENRE = "movie_genre";
		public static final String COLUMN_NAME_RELEASE_DATE = "release_date";
		public static final String COLUMN_NAME_THUMBNAIL_URL = "thumbnail_url";
	}

	// the attributes an entity can have
	/** The tags an entry may have. */
	public static abstract class Tags implements BaseColumns
	{
		public static final String TABLE_NAME = "tags";
		public static final String _ID = "_id";

		public static final String COLUMN_NAME_TAG = "tag";
	}

	/** The hashtags an entry may have. */
	public static abstract class Hashtags implements BaseColumns
	{
		public static final String TABLE_NAME = "hashtags";
		public static final String _ID = "_id";

		public static final String COLUMN_NAME_HASHTAG = "hashtags";
	}

	/** The types an entry is of. */
	public static abstract class Types implements BaseColumns
	{
		public static final String TABLE_NAME = "types";
		public static final String _ID = "_id";

		public static final String COLUMN_NAME_TYPE = "type";
	}

	/** The filetypes a file(table) is of. */
	public static abstract class Filetypes implements BaseColumns
	{
		public static final String TABLE_NAME = "filetypes";
		public static final String _ID = "_id";

		public static final String COLUMN_NAME_FILETYPE = "filetype";
	}

	/** The offline stored files a file(table) has. */
	public static abstract class OfflineFiles implements BaseColumns
	{
		public static final String TABLE_NAME = "offline_files";
		public static final String _ID = "_id";

		public static final String COLUMN_NAME_PATH = "path";
		public static final String COLUMN_NAME_FILETYPES_ID = "filetypes_id";
	}

	/** The cloud stored files a file(table) has. */
	public static abstract class OnlineFiles implements BaseColumns
	{
		public static final String TABLE_NAME = "online_files";
		public static final String _ID = "_id";

		public static final String COLUMN_NAME_DRIVEID = "driveid";
		public static final String COLUMN_NAME_URL = "url";
		public static final String COLUMN_NAME_FILETYPES_ID = "filetypes_id";
	}

	/** The artist a music(table) is performed by. */
	public static abstract class Artists implements BaseColumns
	{
		public static final String TABLE_NAME = "artists";
		public static final String _ID = "_id";

		public static final String COLUMN_NAME_ARTIST = "artist";
	}

	/** The album a music(table) is on. */
	public static abstract class Albums implements BaseColumns
	{
		public static final String TABLE_NAME = "albums";
		public static final String _ID = "_id";

		public static final String COLUMN_NAME_ALBUM = "album";
		public static final String COLUMN_NAME_RELEASE_DATE = "release_date";
		public static final String COLUMN_NAME_THUMBNAIL_URL = "thumbnail_url";
	}

	/** The music genre a music(table) is of. */
	public static abstract class MusicGenres implements BaseColumns
	{
		public static final String TABLE_NAME = "music_genres";
		public static final String _ID = "_id";

		public static final String COLUMN_NAME_MUSIC_ID = "music_id";
		public static final String COLUMN_NAME_MUSIC_GENRE = "music_genre";
	}

	// the connectors between two tables (1 to n relations)
	/** Listing of tags an entry may have. */
	public static abstract class EntryTags implements BaseColumns
	{
		public static final String TABLE_NAME = "entry_tags";
		public static final String _ID = "_id";

		public static final String COLUMN_NAME_ENTRIES_ID = "entries_id";
		public static final String COLUMN_NAME_TAGS_ID = "tags_id";
	}

	/** Listing of hashtags an entry may have. */
	public static abstract class EntryHashtags implements BaseColumns
	{
		public static final String TABLE_NAME = "entry_hashtags";
		public static final String _ID = "_id";

		public static final String COLUMN_NAME_ENTRIES_ID = "entries_id";
		public static final String COLUMN_NAME_HASHTAGS_ID = "hashtags_id";
	}

	/** Listing of offline files a files(table) may have. */
	public static abstract class FileOfflineFiles implements BaseColumns
	{
		public static final String TABLE_NAME = "file_offline_files";
		public static final String _ID = "_id";

		public static final String COLUMN_NAME_FILES_ID = "files_id";
		public static final String COLUMN_NAME_OFFLINE_FILES_ID = "offline_files_id";
	}

	/** Listing of online files a files(table) may have. */
	public static abstract class FileOnlineFiles implements BaseColumns
	{
		public static final String TABLE_NAME = "file_online_files";
		public static final String _ID = "_id";

		public static final String COLUMN_NAME_FILES_ID = "files_id";
		public static final String COLUMN_NAME_ONLINE_FILES_ID = "online_files_id";
	}
}
