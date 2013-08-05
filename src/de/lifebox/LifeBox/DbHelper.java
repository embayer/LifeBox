package de.lifebox.LifeBox;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Base-Class for all DbHelper classes
 * @version 0.1 09.07.13
 * @autor Markus Bayer
 */
public class DbHelper extends SQLiteOpenHelper
{
	// if the database schema has changed, the database version must be increased.
	public static final int DATABASE_VERSION = 18;
	public static final String DATABASE_NAME = "LifeBox.db";

	// data types
	private static final String TEXT_TYPE = " TEXT";
	private static final String INT_TYPE = " INTEGER";
	// constraints
	// keys
	private static final String PRIMARY_KEY_TYPE = " INTEGER PRIMARY KEY AUTOINCREMENT";
	// not null
	private static final String NOT_NULL = " NOT NULL";
	// unique
	private static final String UNIQUE = " UNIQUE ";
	// chars
	private static final String COMMA = ", ";
	// begin and end (to wrap multiple db-operations)
	private static final String SQL_BEGIN = "BEGIN IMMEDIATE TRANSACTION";
	private static final String SQL_COMMIT = "COMMIT TRANSACTION";

	// ArrayLists for predefined rows
	private static ArrayList<String> tags;
	private static ArrayList<String> types;
	private static ArrayList<String> filetypes;

	// SQL Strings

	// entries ---------------------------------------------------------------------------------------------------------
	// CREATE TABLE entries(_id INTEGER PRIMARY KEY, media_id INTEGER,
	private static final String SQL_CREATE_ENTRIES =
			"CREATE TABLE " + LifeBoxContract.Entries.TABLE_NAME +
			"(" +
				LifeBoxContract.Entries._ID + PRIMARY_KEY_TYPE + COMMA +
				LifeBoxContract.Entries.COLUMN_NAME_MEDIA_ID + INT_TYPE + NOT_NULL + COMMA +
				LifeBoxContract.Entries.COLUMN_NAME_TYPES_ID + INT_TYPE + NOT_NULL + COMMA +
				LifeBoxContract.Entries.COlUMN_NAME_TITLE + TEXT_TYPE + NOT_NULL + COMMA +
				LifeBoxContract.Entries.COlUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA + 									// NULL
				LifeBoxContract.Entries.COlUMN_NAME_USER_DATE + INT_TYPE + NOT_NULL + COMMA +
				LifeBoxContract.Entries.COlUMN_NAME_CREATION_DATE + INT_TYPE + NOT_NULL + COMMA +
				LifeBoxContract.Entries.COlUMN_NAME_MODIFICATION_DATE + INT_TYPE + NOT_NULL + COMMA +

				// !media_id is a foreign key without foreign key constraint
				" FOREIGN KEY(" + LifeBoxContract.Entries.COLUMN_NAME_TYPES_ID + ") " +
				" REFERENCES " + LifeBoxContract.Types.TABLE_NAME + "(" + LifeBoxContract.Types._ID + ")" +
			");";

	private static final String SQL_DROP_ENTRIES =
			"DROP TABLE IF EXISTS " + LifeBoxContract.Entries.TABLE_NAME + ";";

	// files -----------------------------------------------------------------------------------------------------------
	private static final String SQL_CREATE_FILES =
			"CREATE TABLE " + LifeBoxContract.Files.TABLE_NAME +
			"(" +
				LifeBoxContract.Files._ID + PRIMARY_KEY_TYPE + COMMA +
				LifeBoxContract.Files.COLUMN_NAME_FILETYPES_ID + INT_TYPE + NOT_NULL + COMMA +

				" FOREIGN KEY(" + LifeBoxContract.Files.COLUMN_NAME_FILETYPES_ID + ") " +
				" REFERENCES " + LifeBoxContract.Filetypes.TABLE_NAME + "(" + LifeBoxContract.Filetypes._ID + ")" +
			");";

	private static final String SQL_DROP_FILES =
			"DROP TABLE IF EXISTS " + LifeBoxContract.Files.TABLE_NAME + ";";

	// text ------------------------------------------------------------------------------------------------------------
	private static final String SQL_CREATE_TEXT =
			"CREATE TABLE " + LifeBoxContract.Text.TABLE_NAME +
			"(" +
				LifeBoxContract.Text._ID + PRIMARY_KEY_TYPE + COMMA +
				LifeBoxContract.Text.COLUMN_NAME_TEXT + TEXT_TYPE + NOT_NULL +
			");";

	private static final String SQL_DROP_TEXT =
			"DROP TABLE IF EXISTS " + LifeBoxContract.Text.TABLE_NAME + ";";

	// music -----------------------------------------------------------------------------------------------------------
	private static final String SQL_CREATE_MUSIC =
			"CREATE TABLE " + LifeBoxContract.Music.TABLE_NAME +
			"(" +
				LifeBoxContract.Music._ID + PRIMARY_KEY_TYPE + COMMA +
				LifeBoxContract.Music.COLUMN_NAME_TRACK + TEXT_TYPE + NOT_NULL + COMMA +
				LifeBoxContract.Music.COLUMN_NAME_ARTISTS_ID + INT_TYPE + NOT_NULL + COMMA +
				LifeBoxContract.Music.COLUMN_NAME_ALBUMS_ID + INT_TYPE + NOT_NULL + COMMA +

				" FOREIGN KEY(" + LifeBoxContract.Music.COLUMN_NAME_ARTISTS_ID + ") " +
				" REFERENCES " + LifeBoxContract.Artists.TABLE_NAME + "(" + LifeBoxContract.Artists._ID + ")" + COMMA +
				" FOREIGN KEY(" + LifeBoxContract.Music.COLUMN_NAME_ALBUMS_ID + ") " +
				" REFERENCES " + LifeBoxContract.Albums.TABLE_NAME + "(" + LifeBoxContract.Albums._ID + ")" +
			" );";

	private static final String SQL_DROP_MUSIC =
			"DROP TABLE IF EXISTS " + LifeBoxContract.Music.TABLE_NAME + ";";

	// movie -----------------------------------------------------------------------------------------------------------
	private static final String SQL_CREATE_MOVIES =
			"CREATE TABLE " + LifeBoxContract.Movies.TABLE_NAME +
			"(" +
				LifeBoxContract.Movies._ID + PRIMARY_KEY_TYPE + COMMA +
				LifeBoxContract.Movies.COLUMN_NAME_TITLE + TEXT_TYPE + NOT_NULL + COMMA +
				LifeBoxContract.Movies.COLUMN_NAME_DIRECTOR + TEXT_TYPE + NOT_NULL + COMMA +
				LifeBoxContract.Movies.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA + 									// Null
				LifeBoxContract.Movies.COLUMN_NAME_MOVIE_GENRE + TEXT_TYPE + COMMA +                                    // Null
				LifeBoxContract.Movies.COLUMN_NAME_RELEASE_DATE + INT_TYPE + COMMA +									// NULL
				LifeBoxContract.Movies.COLUMN_NAME_THUMBNAIL_URL + TEXT_TYPE +					                        // Null
			");";

	private static final String SQL_DROP_MOVIES =
			"DROP TABLE IF EXISTS " + LifeBoxContract.Movies.TABLE_NAME + ";";

	// tags ------------------------------------------------------------------------------------------------------------
	private static final String SQL_CREATE_TAGS =
			"CREATE TABLE " + LifeBoxContract.Tags.TABLE_NAME +
			"(" +
				LifeBoxContract.Tags._ID + PRIMARY_KEY_TYPE + COMMA +
				LifeBoxContract.Tags.COLUMN_NAME_TAG + TEXT_TYPE + UNIQUE + NOT_NULL +
			");";

	private static final String SQL_DROP_TAGS =
			"DROP TABLE IF EXISTS " + LifeBoxContract.Tags.TABLE_NAME + ";";

	// hashtags --------------------------------------------------------------------------------------------------------
	private static final String SQL_CREATE_HASHTAGS =
			"CREATE TABLE " + LifeBoxContract.Hashtags.TABLE_NAME +
			"(" +
				LifeBoxContract.Hashtags._ID + PRIMARY_KEY_TYPE + COMMA +
				LifeBoxContract.Hashtags.COLUMN_NAME_HASHTAG + TEXT_TYPE + UNIQUE + NOT_NULL +
			");";

	private static final String SQL_DROP_HASHTAGS =
			"DROP TABLE IF EXISTS " + LifeBoxContract.Hashtags.TABLE_NAME + ";";

	// types ----------------------------------------------------------------------------------------------------------
	private static final String SQL_CREATE_TYPES =
			"CREATE TABLE " + LifeBoxContract.Types.TABLE_NAME +
			" (" +
				LifeBoxContract.Types._ID + PRIMARY_KEY_TYPE + COMMA +
				LifeBoxContract.Types.COLUMN_NAME_TYPE + TEXT_TYPE + UNIQUE + NOT_NULL +
			");";

	private static final String SQL_DROP_TYPES =
			"DROP TABLE IF EXISTS " + LifeBoxContract.Types.TABLE_NAME + ";";

	// filetypes -------------------------------------------------------------------------------------------------------
	private static final String SQL_CREATE_FILETYPES =
			"CREATE TABLE " + LifeBoxContract.Filetypes.TABLE_NAME +
			"(" +
				LifeBoxContract.Filetypes._ID + PRIMARY_KEY_TYPE + COMMA +
				LifeBoxContract.Filetypes.COLUMN_NAME_FILETYPE + TEXT_TYPE + UNIQUE + NOT_NULL +
			");";

	private static final String SQL_DROP_FILETYPES =
			"DROP TABLE IF EXISTS " + LifeBoxContract.Filetypes.TABLE_NAME + ";";

	// offline_files ---------------------------------------------------------------------------------------------------
	private static final String SQL_CREATE_OFFLINE_FILES =
			"CREATE TABLE " + LifeBoxContract.OfflineFiles.TABLE_NAME +
			"(" +
				LifeBoxContract.OfflineFiles._ID + PRIMARY_KEY_TYPE + COMMA +
				LifeBoxContract.OfflineFiles.COLUMN_NAME_PATH + TEXT_TYPE + UNIQUE + NOT_NULL + COMMA +
				LifeBoxContract.OfflineFiles.COLUMN_NAME_FILETYPES_ID + INT_TYPE + NOT_NULL + COMMA +

				" FOREIGN KEY(" + LifeBoxContract.OfflineFiles.COLUMN_NAME_FILETYPES_ID + ") " +
				" REFERENCES " + LifeBoxContract.Filetypes.TABLE_NAME + "(" + LifeBoxContract.Filetypes._ID + ")" +
			");";

	private static final String SQL_DROP_OFFLINE_FILES =
			"DROP TABLE IF EXISTS " + LifeBoxContract.OfflineFiles.TABLE_NAME + ";";

	// online_files ----------------------------------------------------------------------------------------------------
	private static final String SQL_CREATE_ONLINE_FILES =
			"CREATE TABLE " + LifeBoxContract.OnlineFiles.TABLE_NAME +
			"(" +
				LifeBoxContract.OnlineFiles._ID + PRIMARY_KEY_TYPE + COMMA +
				LifeBoxContract.OnlineFiles.COLUMN_NAME_DRIVEID + TEXT_TYPE + NOT_NULL + COMMA +
				LifeBoxContract.OnlineFiles.COLUMN_NAME_URL + TEXT_TYPE + NOT_NULL + COMMA +
				LifeBoxContract.OnlineFiles.COLUMN_NAME_FILETYPES_ID + INT_TYPE + NOT_NULL + COMMA +

				" FOREIGN KEY(" + LifeBoxContract.OnlineFiles.COLUMN_NAME_FILETYPES_ID + ") " +
				" REFERENCES " + LifeBoxContract.Filetypes.TABLE_NAME + "(" + LifeBoxContract.Filetypes._ID + ")" +
			");";

	private static final String SQL_DROP_ONLINE_FILES =
			"DROP TABLE IF EXISTS " + LifeBoxContract.OnlineFiles.TABLE_NAME + ";";

	// artists ---------------------------------------------------------------------------------------------------------
	private static final String SQL_CREATE_ARTISTS =
			"CREATE TABLE " + LifeBoxContract.Artists.TABLE_NAME +
			"(" +
				LifeBoxContract.Artists._ID + PRIMARY_KEY_TYPE + COMMA +
				LifeBoxContract.Artists.COLUMN_NAME_ARTIST + TEXT_TYPE + UNIQUE + NOT_NULL +
			");";

	private static final String SQL_DROP_ARTISTS =
			"DROP TABLE IF EXISTS " + LifeBoxContract.Artists.TABLE_NAME + ";";

	// albums ----------------------------------------------------------------------------------------------------------
	private static final String SQL_CREATE_ALBUMS =
			"CREATE TABLE " + LifeBoxContract.Albums.TABLE_NAME +
			"(" +
				LifeBoxContract.Albums._ID + PRIMARY_KEY_TYPE + COMMA +
				LifeBoxContract.Albums.COLUMN_NAME_ALBUM + TEXT_TYPE + NOT_NULL + COMMA +
				LifeBoxContract.Albums.COLUMN_NAME_RELEASE_DATE + INT_TYPE + COMMA + 									// NULL
				LifeBoxContract.Albums.COLUMN_NAME_THUMBNAIL_URL + TEXT_TYPE + 											// NULL
			");";

	private static final String SQL_DROP_ALBUMS =
			"DROP TABLE IF EXISTS " + LifeBoxContract.Albums.TABLE_NAME + ";";

	// music_genre -----------------------------------------------------------------------------------------------------
	private static final String SQL_CREATE_MUSIC_GENRES =
			"CREATE TABLE " + LifeBoxContract.MusicGenres.TABLE_NAME +
			"(" +
				LifeBoxContract.MusicGenres._ID + PRIMARY_KEY_TYPE + COMMA +
				LifeBoxContract.MusicGenres.COLUMN_NAME_MUSIC_ID + INT_TYPE + NOT_NULL + COMMA +
				LifeBoxContract.MusicGenres.COLUMN_NAME_MUSIC_GENRE + TEXT_TYPE + NOT_NULL + COMMA +

				" FOREIGN KEY(" + LifeBoxContract.MusicGenres.COLUMN_NAME_MUSIC_ID + ") " +
				" REFERENCES " + LifeBoxContract.Music.TABLE_NAME + "(" + LifeBoxContract.Music._ID + ")" +
			");";

	private static final String SQL_DROP_MUSIC_GENRES =
			"DROP TABLE IF EXISTS " + LifeBoxContract.MusicGenres.TABLE_NAME + ";";

	// entry_tags ------------------------------------------------------------------------------------------------------
	private static final String SQL_CREATE_ENTRY_TAGS =
			"CREATE TABLE " + LifeBoxContract.EntryTags.TABLE_NAME +
			"(" +
				LifeBoxContract.EntryTags._ID + PRIMARY_KEY_TYPE + COMMA +
				LifeBoxContract.EntryTags.COLUMN_NAME_ENTRIES_ID + INT_TYPE + NOT_NULL + COMMA +
				LifeBoxContract.EntryTags.COLUMN_NAME_TAGS_ID + INT_TYPE + NOT_NULL + COMMA +

				" FOREIGN KEY(" + LifeBoxContract.EntryTags.COLUMN_NAME_ENTRIES_ID + ") " +
				" REFERENCES " + LifeBoxContract.Entries.TABLE_NAME + "(" + LifeBoxContract.Entries._ID + ")" + COMMA +
				" FOREIGN KEY(" + LifeBoxContract.EntryTags.COLUMN_NAME_TAGS_ID + ") " +
				" REFERENCES " + LifeBoxContract.Tags.TABLE_NAME + "(" + LifeBoxContract.Tags._ID + ")" +
			");";

	private static final String SQL_DROP_ENTRY_TAGS =
			"DROP TABLE IF EXISTS " + LifeBoxContract.EntryTags.TABLE_NAME + ";";

	// entry_hashtags --------------------------------------------------------------------------------------------------
	private static final String SQL_CREATE_ENTRY_HASHTAGS =
			"CREATE TABLE " + LifeBoxContract.EntryHashtags.TABLE_NAME +
			"(" +
				LifeBoxContract.EntryHashtags._ID + PRIMARY_KEY_TYPE + COMMA +
				LifeBoxContract.EntryHashtags.COLUMN_NAME_ENTRIES_ID + INT_TYPE + NOT_NULL + COMMA +
				LifeBoxContract.EntryHashtags.COLUMN_NAME_HASHTAGS_ID + INT_TYPE + NOT_NULL + COMMA +

				" FOREIGN KEY(" + LifeBoxContract.EntryHashtags.COLUMN_NAME_ENTRIES_ID + ") " +
				" REFERENCES " + LifeBoxContract.Entries.TABLE_NAME + "(" + LifeBoxContract.Entries._ID + ")" + COMMA +
				" FOREIGN KEY(" + LifeBoxContract.EntryHashtags.COLUMN_NAME_HASHTAGS_ID + ") " +
				" REFERENCES " + LifeBoxContract.Hashtags.TABLE_NAME + "(" + LifeBoxContract.Hashtags._ID + ")" +
			");";

	private static final String SQL_DROP_ENTRY_HASHTAGS =
			"DROP TABLE IF EXISTS " + LifeBoxContract.EntryHashtags.TABLE_NAME + ";";

	// file_offline_files ----------------------------------------------------------------------------------------------
	private static final String SQL_CREATE_FILE_OFFLINE_FILES =
			"CREATE TABLE " + LifeBoxContract.FileOfflineFiles.TABLE_NAME +
			"(" +
				LifeBoxContract.FileOfflineFiles._ID + PRIMARY_KEY_TYPE + COMMA +
				LifeBoxContract.FileOfflineFiles.COLUMN_NAME_FILES_ID + INT_TYPE + NOT_NULL + COMMA +
				LifeBoxContract.FileOfflineFiles.COLUMN_NAME_OFFLINE_FILES_ID + INT_TYPE + NOT_NULL + COMMA +

				" FOREIGN KEY(" + LifeBoxContract.FileOfflineFiles.COLUMN_NAME_FILES_ID + ") " +
				" REFERENCES " + LifeBoxContract.Files.TABLE_NAME + "(" + LifeBoxContract.Files._ID + ")" + COMMA +
				" FOREIGN KEY(" + LifeBoxContract.FileOfflineFiles.COLUMN_NAME_OFFLINE_FILES_ID + ") " +
				" REFERENCES " + LifeBoxContract.OfflineFiles.TABLE_NAME + "(" + LifeBoxContract.OfflineFiles._ID + ")" +
			");";

	private static final String SQL_DROP_FILE_OFFLINE_FILES =
			"DROP TABLE IF EXISTS " + LifeBoxContract.FileOfflineFiles.TABLE_NAME + ";";

	// file_online_files -----------------------------------------------------------------------------------------------
	private static final String SQL_CREATE_FILE_ONLINE_FILES =
			"CREATE TABLE " + LifeBoxContract.FileOnlineFiles.TABLE_NAME +
			"(" +
				LifeBoxContract.FileOnlineFiles._ID + PRIMARY_KEY_TYPE + COMMA +
				LifeBoxContract.FileOnlineFiles.COLUMN_NAME_FILES_ID + INT_TYPE + NOT_NULL + COMMA +
				LifeBoxContract.FileOnlineFiles.COLUMN_NAME_ONLINE_FILES_ID + INT_TYPE + NOT_NULL + COMMA +

				" FOREIGN KEY(" + LifeBoxContract.FileOnlineFiles.COLUMN_NAME_FILES_ID + ") " +
				" REFERENCES " + LifeBoxContract.Files.TABLE_NAME + "(" + LifeBoxContract.Files._ID + ")" + COMMA +
				" FOREIGN KEY(" + LifeBoxContract.FileOnlineFiles.COLUMN_NAME_ONLINE_FILES_ID + ") " +
				" REFERENCES " + LifeBoxContract.OnlineFiles.TABLE_NAME + "(" + LifeBoxContract.OnlineFiles._ID + ")" +
			");";

	private static final String SQL_DROP_FILE_ONLINE_FILES =
			"DROP TABLE IF EXISTS " + LifeBoxContract.FileOnlineFiles.TABLE_NAME + ";";


	public DbHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		// tags
		tags = new ArrayList<String>();
		tags.add(Constants.TAG_SMILEY1_EXTRA);
		tags.add(Constants.TAG_SMILEY2_EXTRA);
		tags.add(Constants.TAG_SMILEY3_EXTRA);
		tags.add(Constants.TAG_SMILEY4_EXTRA);
		tags.add(Constants.TAG_SMILEY5_EXTRA);
		tags.add(Constants.TAG_SMILEY6_EXTRA);
		tags.add(Constants.TAG_SMILEY7_EXTRA);
		tags.add(Constants.TAG_SMILEY8_EXTRA);
		tags.add(Constants.TAG_SMILEY9_EXTRA);
		tags.add(Constants.TAG_SMILEY10_EXTRA);

		tags.add(Constants.TAG_LOVE_EXTRA);
		tags.add(Constants.TAG_STAR_EXTRA);
		tags.add(Constants.TAG_DISLIKE_EXTRA);
		tags.add(Constants.TAG_ACHIEVEMENT_EXTRA);
		tags.add(Constants.TAG_WORK_EXTRA);
        tags.add(Constants.TAG_FAMILY_EXTRA);
		tags.add(Constants.TAG_CHILD_EXTRA);
		tags.add(Constants.TAG_PET_EXTRA);
		tags.add(Constants.TAG_FRIENDS_EXTRA);
		tags.add(Constants.TAG_PARTY_EXTRA);
		tags.add(Constants.TAG_OUTDOOR_EXTRA);
		tags.add(Constants.TAG_HOME_EXTRA);
		tags.add(Constants.TAG_TRIP_EXTRA);
		tags.add(Constants.TAG_TRAVEL_EXTRA);
		tags.add(Constants.TAG_EVENT_EXTRA);
		tags.add(Constants.TAG_HOBBY_EXTRA);
		tags.add(Constants.TAG_SPORT_EXTRA);
		tags.add(Constants.TAG_FOOD_EXTRA);
		tags.add(Constants.TAG_CLOTH_EXTRA);
		tags.add(Constants.TAG_SHOPPING_EXTRA);

		// tablenames of the different mediatypes
		types = new ArrayList<String>();
		types.add(Constants.TYPE_FILE); 	// _id == 1
		types.add(Constants.TYPE_MOVIE);    // _id == 2
		types.add(Constants.TYPE_MUSIC);    // _id == 3
		types.add(Constants.TYPE_TEXT);     // _id == 4

		// different types of (offline / online) files
		filetypes = new ArrayList<String>();
		filetypes.add(Constants.MIME_TYPE_IMAGE);
		filetypes.add(Constants.MIME_TYPE_IMAGE_THUMB);
		filetypes.add(Constants.MIME_TYPE_VIDEO_THUMB);
		filetypes.add(Constants.MIME_TYPE_VIDEO);
	}

	/** Called by the framework if the database does not exist. */
	public void onCreate(SQLiteDatabase db)
	{
		// create the tables
		db.execSQL(SQL_BEGIN);

		db.execSQL(SQL_CREATE_ENTRIES);
		db.execSQL(SQL_CREATE_FILES);
		db.execSQL(SQL_CREATE_TEXT);
		db.execSQL(SQL_CREATE_MUSIC);
		db.execSQL(SQL_CREATE_MOVIES);
		db.execSQL(SQL_CREATE_TAGS);
		db.execSQL(SQL_CREATE_HASHTAGS);
		db.execSQL(SQL_CREATE_TYPES);
		db.execSQL(SQL_CREATE_FILETYPES);
		db.execSQL(SQL_CREATE_OFFLINE_FILES);
		db.execSQL(SQL_CREATE_ONLINE_FILES);
		db.execSQL(SQL_CREATE_ARTISTS);
		db.execSQL(SQL_CREATE_ALBUMS);
		db.execSQL(SQL_CREATE_MUSIC_GENRES);
		db.execSQL(SQL_CREATE_ENTRY_TAGS);
		db.execSQL(SQL_CREATE_ENTRY_HASHTAGS);
		db.execSQL(SQL_CREATE_FILE_OFFLINE_FILES);
		db.execSQL(SQL_CREATE_FILE_ONLINE_FILES);

		// insert the predefined rows
		for(String tag : tags)
		{
			// build the statement
			String sql = "INSERT OR IGNORE INTO " + LifeBoxContract.Tags.TABLE_NAME + "(" + LifeBoxContract.Tags.COLUMN_NAME_TAG +
					") VALUES('" + tag + "');";

			db.execSQL(sql);
		}

		for(String type : types)
		{
			// build the statement
			String sql = "INSERT OR IGNORE INTO " + LifeBoxContract.Types.TABLE_NAME + "(" + LifeBoxContract.Types.COLUMN_NAME_TYPE +
					") VALUES('" + type + "');";

			db.execSQL(sql);
		}

		for(String filetype : filetypes)
		{
			// build the statement
			String sql = "INSERT OR IGNORE INTO " + LifeBoxContract.Filetypes.TABLE_NAME + "(" + LifeBoxContract.Filetypes.COLUMN_NAME_FILETYPE +
					") VALUES('" + filetype + "');";

			db.execSQL(sql);
		}

		//todo delete
//		db.execSQL("INSERT INTO hashtags(hashtag) VALUES('hashtag1')");
//		db.execSQL("INSERT INTO hashtags(hashtag) VALUES('hashtag2')");
//		db.execSQL("INSERT INTO hashtags(hashtag) VALUES('hashtag3')");

		db.execSQL(SQL_COMMIT);
	}

	/**
	 * Called when the database version is increased in the application code.
	 * Used to update the database schema.
	 * */
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// discard the data
		db.execSQL(SQL_BEGIN);

		db.execSQL(SQL_DROP_ENTRIES);
		db.execSQL(SQL_DROP_FILES);
		db.execSQL(SQL_DROP_TEXT);
		db.execSQL(SQL_DROP_MUSIC);
		db.execSQL(SQL_DROP_MOVIES);
		db.execSQL(SQL_DROP_TAGS);
		db.execSQL(SQL_DROP_HASHTAGS);
		db.execSQL(SQL_DROP_TYPES);
		db.execSQL(SQL_DROP_FILETYPES);
		db.execSQL(SQL_DROP_OFFLINE_FILES);
		db.execSQL(SQL_DROP_ONLINE_FILES);
		db.execSQL(SQL_DROP_ARTISTS);
		db.execSQL(SQL_DROP_ALBUMS);
		db.execSQL(SQL_DROP_MUSIC_GENRES);
		db.execSQL(SQL_DROP_ENTRY_TAGS);
		db.execSQL(SQL_DROP_ENTRY_HASHTAGS);
		db.execSQL(SQL_DROP_FILE_OFFLINE_FILES);
		db.execSQL(SQL_DROP_FILE_ONLINE_FILES);

		db.execSQL(SQL_COMMIT);

		// build new
		onCreate(db);
	}

	/**
	 * Called when the database version is decreased in the application code.
	 * Used to update the database schema.
	 * */
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		onUpgrade(db, oldVersion, newVersion);
	}
	//##################################################################################################################
	// INSERT ----------------------------------------------------------------------------------------------------------
	//##################################################################################################################
	/**
	 * Inserts a entry of the mediatype file.
	 */
	public void insertFile(String mimeType, String mediaType, String userTitle, String userDescription,
							long userTimestamp, String fileUrl, String thumbnailUrl, String fileDriveId,
							String fileDownloadUrl, String thumbDriveId, String thumbDownloadUrl,
							ArrayList<String> hashtagList, ArrayList<String> tagList)
	{
		// get the data repository in write mode
		SQLiteDatabase db;
		ContentValues values = new ContentValues();

		// get the filetypes_id for files ------------------------------------------------------------------------------
		long filetypesId = selectId
				(
						LifeBoxContract.Filetypes.TABLE_NAME,
						LifeBoxContract.Filetypes.COLUMN_NAME_FILETYPE,
						LifeBoxContract.Filetypes._ID,
						mimeType
				);

		// insert into files -------------------------------------------------------------------------------------------
		long filesId = insertRegular
				(
						LifeBoxContract.Files.TABLE_NAME,
						false,
						new KeyValuePair(LifeBoxContract.Files.COLUMN_NAME_FILETYPES_ID, filetypesId)
				);

		// get the types_id for entries --------------------------------------------------------------------------------
		long typesId = selectTypesId(mediaType);

		// insert into entries -----------------------------------------------------------------------------------------
		long entriesId = insertRegular
				(
						LifeBoxContract.Entries.TABLE_NAME,
						false,
						new KeyValuePair(LifeBoxContract.Entries.COLUMN_NAME_MEDIA_ID, filesId),
						new KeyValuePair(LifeBoxContract.Entries.COLUMN_NAME_TYPES_ID, typesId),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_TITLE, userTitle),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_DESCRIPTION, userDescription),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_USER_DATE, userTimestamp),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_CREATION_DATE, userTimestamp),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_MODIFICATION_DATE, userTimestamp)
				);

		// insert (file) into offline_files ----------------------------------------------------------------------------
		long offlineFilesId = insertRegular
				(
						LifeBoxContract.OfflineFiles.TABLE_NAME,
						false,
						new KeyValuePair(LifeBoxContract.OfflineFiles.COLUMN_NAME_PATH, fileUrl),
						new KeyValuePair(LifeBoxContract.OfflineFiles.COLUMN_NAME_FILETYPES_ID, filetypesId)
				);

		// insert (thumbnail) into offline_files -----------------------------------------------------------------------

		values.put(LifeBoxContract.OfflineFiles.COLUMN_NAME_PATH, thumbnailUrl);
		values.put(LifeBoxContract.OfflineFiles.COLUMN_NAME_FILETYPES_ID, selectFiletypesId(mimeType));

		db = getWritableDatabase();

		long offlineFilesTmbId = db.insert(LifeBoxContract.OfflineFiles.TABLE_NAME, null, values);

		values.clear();

		// insert (file) into online_files -----------------------------------------------------------------------------
		long onlineFilesId = insertRegular
				(
						LifeBoxContract.OnlineFiles.TABLE_NAME,
						false,
						new KeyValuePair(LifeBoxContract.OnlineFiles.COLUMN_NAME_DRIVEID, fileDriveId),
						new KeyValuePair(LifeBoxContract.OnlineFiles.COLUMN_NAME_URL, fileDownloadUrl),
						new KeyValuePair(LifeBoxContract.OnlineFiles.COLUMN_NAME_FILETYPES_ID, filetypesId)
				);

		// insert (thumbnail) into online_files ------------------------------------------------------------------------

		values.put(LifeBoxContract.OnlineFiles.COLUMN_NAME_DRIVEID, thumbDriveId);
		values.put(LifeBoxContract.OnlineFiles.COLUMN_NAME_URL, thumbDownloadUrl);
		values.put(LifeBoxContract.OnlineFiles.COLUMN_NAME_FILETYPES_ID, selectFiletypesId(mimeType));

		db = getWritableDatabase();

		long onlineFilesTmbId = db.insert(LifeBoxContract.OnlineFiles.TABLE_NAME, null, values);

		values.clear();
		db.close();

		// insert (file) into file_offline_files -----------------------------------------------------------------------
		long fileOfflineFilesId = insertRegular
				(
						LifeBoxContract.FileOfflineFiles.TABLE_NAME,
						false,
						new KeyValuePair(LifeBoxContract.FileOfflineFiles.COLUMN_NAME_FILES_ID, filesId),
						new KeyValuePair(LifeBoxContract.FileOfflineFiles.COLUMN_NAME_OFFLINE_FILES_ID, offlineFilesId)
				);

		// insert (thumbnail) into file_offline_files ------------------------------------------------------------------
		long fileOfflineFilesTmbId = insertRegular
				(
						LifeBoxContract.FileOfflineFiles.TABLE_NAME,
						false,
						new KeyValuePair(LifeBoxContract.FileOfflineFiles.COLUMN_NAME_FILES_ID, filesId),
						new KeyValuePair(LifeBoxContract.FileOfflineFiles.COLUMN_NAME_OFFLINE_FILES_ID, offlineFilesTmbId)
				);

		// insert (file) into file_online_files ------------------------------------------------------------------------
		long fileOnlineFilesId = insertRegular
				(
						LifeBoxContract.FileOnlineFiles.TABLE_NAME,
						false,
						new KeyValuePair(LifeBoxContract.FileOnlineFiles.COLUMN_NAME_FILES_ID, filesId),
						new KeyValuePair(LifeBoxContract.FileOnlineFiles.COLUMN_NAME_ONLINE_FILES_ID, onlineFilesId)
				);

		// insert (thumbnail) into file_online_files -------------------------------------------------------------------
		long fileOnlineFilesTmbId = insertRegular
				(
						LifeBoxContract.FileOnlineFiles.TABLE_NAME,
						false,
						new KeyValuePair(LifeBoxContract.FileOnlineFiles.COLUMN_NAME_FILES_ID, filesId),
						new KeyValuePair(LifeBoxContract.FileOnlineFiles.COLUMN_NAME_ONLINE_FILES_ID, onlineFilesTmbId)
				);

		// insert into entry_hashtags ----------------------------------------------------------------------------------
		insertHashtags(entriesId, hashtagList);

		// insert into entry_tags --------------------------------------------------------------------------------------
		insertEntryTags(entriesId, tagList);

		// close the handler
		db.close();
	}

	/**
	 * Insert a entry of the mediatype text.
	 */
	public void insertText(String text, String mediaType, String userTitle, String userDescription, long userTimestamp,
							ArrayList<String> hashtagList, ArrayList<String> tagList)
	{
		// insert into text --------------------------------------------------------------------------------------------
		long textId = insertRegular
				(
						LifeBoxContract.Text.TABLE_NAME, false, new KeyValuePair(LifeBoxContract.Text.COLUMN_NAME_TEXT, text)
				);

		// get the types_id for entries --------------------------------------------------------------------------------
		long typesId = selectTypesId(mediaType);

		// insert into entries -----------------------------------------------------------------------------------------
		long entriesId = insertRegular
				(
						LifeBoxContract.Entries.TABLE_NAME,
						false,
						new KeyValuePair(LifeBoxContract.Entries.COLUMN_NAME_MEDIA_ID, textId),
						new KeyValuePair(LifeBoxContract.Entries.COLUMN_NAME_TYPES_ID, typesId),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_TITLE, userTitle),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_DESCRIPTION, userDescription),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_USER_DATE, userTimestamp),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_CREATION_DATE, userTimestamp),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_MODIFICATION_DATE, userTimestamp)
				);

		// insert into hashtags & entry_hashtags -----------------------------------------------------------------------
		insertHashtags(entriesId, hashtagList);

		// insert into entry_tags --------------------------------------------------------------------------------------
		insertEntryTags(entriesId, tagList);
	}

	/**
	 * Insert a entry of the mediatype movie
	 */
	public void insertMovie(String movieTitle, String movieDirector, String movieDescription, String movieGenre,
							 long movieTimestamp, String movieThumbnailUrl, String mediaType, String userTitle,
							 String userDescription, long userTimestamp,
							 ArrayList<String> hashtagList, ArrayList<String> tagList)
	{
		// insert into movies ------------------------------------------------------------------------------------------
		long moviesId = insertRegular
				(
						LifeBoxContract.Movies.TABLE_NAME,
						false,
						new KeyValuePair(LifeBoxContract.Movies.COLUMN_NAME_TITLE, movieTitle),
						new KeyValuePair(LifeBoxContract.Movies.COLUMN_NAME_DIRECTOR, movieDirector),
						new KeyValuePair(LifeBoxContract.Movies.COLUMN_NAME_DESCRIPTION, movieDescription),
						new KeyValuePair(LifeBoxContract.Movies.COLUMN_NAME_MOVIE_GENRE, movieGenre),
						new KeyValuePair(LifeBoxContract.Movies.COLUMN_NAME_RELEASE_DATE, movieTimestamp),
						new KeyValuePair(LifeBoxContract.Movies.COLUMN_NAME_THUMBNAIL_URL, movieThumbnailUrl)
				);

		// get the types_id for entries --------------------------------------------------------------------------------
		long typesId = selectTypesId(mediaType);

		// insert into entries -----------------------------------------------------------------------------------------
		long entriesId = insertRegular
				(
						LifeBoxContract.Entries.TABLE_NAME,
						false,
						new KeyValuePair(LifeBoxContract.Entries.COLUMN_NAME_MEDIA_ID, moviesId),
						new KeyValuePair(LifeBoxContract.Entries.COLUMN_NAME_TYPES_ID, typesId),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_TITLE, userTitle),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_DESCRIPTION, userDescription),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_USER_DATE, userTimestamp),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_CREATION_DATE, userTimestamp),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_MODIFICATION_DATE, userTimestamp)
				);

		// insert into hashtags & entry_hashtags -----------------------------------------------------------------------
		insertHashtags(entriesId, hashtagList);

		// insert into entry_tags
		insertEntryTags(entriesId, tagList);
	}

	/**
	 * Insert a entry of the mediatype music.
	 */
	public void insertMusic(String musicArtist, String musicAlbum, long musicTimestamp, String musicThumbnailUrl,
							 String musicTrack, String musicGenre, String mediaType, String userTitle, String userDescription,
							 long userTimestamp, ArrayList<String> hashtagList, ArrayList<String> tagList)
	{
		// insert into artists if not exists ---------------------------------------------------------------------------
		long artistsId = insertRegular
				(
						LifeBoxContract.Artists.TABLE_NAME,
						true,
						new KeyValuePair(LifeBoxContract.Artists.COLUMN_NAME_ARTIST, musicArtist)
				);

		// insert into albums ------------------------------------------------------------------------------------------
		long albumsId = insertRegular
				(
						LifeBoxContract.Albums.TABLE_NAME,
						true,
						new KeyValuePair(LifeBoxContract.Albums.COLUMN_NAME_ALBUM, musicAlbum),
						new KeyValuePair(LifeBoxContract.Albums.COLUMN_NAME_RELEASE_DATE, musicTimestamp),
						new KeyValuePair(LifeBoxContract.Albums.COLUMN_NAME_THUMBNAIL_URL, musicThumbnailUrl)
				);

		// insert into music -------------------------------------------------------------------------------------------
		long musicId = insertRegular
				(
						LifeBoxContract.Music.TABLE_NAME,
						false,
						new KeyValuePair(LifeBoxContract.Music.COLUMN_NAME_TRACK, musicTrack),
						new KeyValuePair(LifeBoxContract.Music.COLUMN_NAME_ARTISTS_ID, artistsId),
						new KeyValuePair(LifeBoxContract.Music.COLUMN_NAME_ALBUMS_ID, albumsId)
				);

		// get the types_id for entries --------------------------------------------------------------------------------
		long typesId = selectTypesId(mediaType);

		// insert into entries -----------------------------------------------------------------------------------------
		long entriesId = insertRegular
				(
						LifeBoxContract.Entries.TABLE_NAME,
						false,
						new KeyValuePair(LifeBoxContract.Entries.COLUMN_NAME_MEDIA_ID, musicId),
						new KeyValuePair(LifeBoxContract.Entries.COLUMN_NAME_TYPES_ID, typesId),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_TITLE, userTitle),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_DESCRIPTION, userDescription),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_USER_DATE, userTimestamp),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_CREATION_DATE, userTimestamp),
						new KeyValuePair(LifeBoxContract.Entries.COlUMN_NAME_MODIFICATION_DATE, userTimestamp)
				);

		// insert into music_genres ------------------------------------------------------------------------------------
		long musicGenresId = insertRegular
				(
						LifeBoxContract.MusicGenres.TABLE_NAME,
						false,
						new KeyValuePair(LifeBoxContract.MusicGenres.COLUMN_NAME_MUSIC_ID, musicId),
						new KeyValuePair(LifeBoxContract.MusicGenres.COLUMN_NAME_MUSIC_GENRE, musicGenre)
				);

		// insert into hashtags & entry_hashtags -----------------------------------------------------------------------
		insertHashtags(entriesId, hashtagList);

		// insert into entry_tags --------------------------------------------------------------------------------------
		insertEntryTags(entriesId, tagList);

	}

	/**
	 * Queries a database table in order to get the key matching a condition.
	 * @param table (String) the db table
	 * @param dataColumn (String) the db column to compare with the condition
	 * @param keyColumn (String) the db collumn holding the key
	 * @param condition (String) the term to compare with dataColumn
	 * @return _id (long) of the matching row or -1 if it fails.
	 */
	public long selectId(String table, String dataColumn, String keyColumn, String condition)
	{
		SQLiteDatabase db = this.getReadableDatabase();

		// define the projection
		String[] projection = {keyColumn};

		// define the selection
		String selection = dataColumn + " == " + "'" + condition +"'";

		// query the database to retrieve a cursor object containing the data
		Cursor c = db.query
				(
						table,
						projection,
						selection,
						null,
						null,
						null,
						null
				);

		// adjust the read position
		c.moveToFirst();

		// get the key
		long id = -1;
		try
		{
			id = c.getLong(c.getColumnIndexOrThrow(keyColumn));
		}
		catch(CursorIndexOutOfBoundsException e)
		{
			id = -1;
			Log.e("cursor out of bounds: ", e.getMessage());
		}

		// cleanup
		c.close();
		db.close();

		return id;
	}

	/**
	 * Queries the database to get the primary key of the table types, matching a given mediatype.
	 * @param mediaType (String) the mediatype that have to match
	 * @return _id (long) the primary key of the row
	 */
	public long selectTypesId(String mediaType)
	{
		return selectId
				(
						LifeBoxContract.Types.TABLE_NAME,
						LifeBoxContract.Types.COLUMN_NAME_TYPE,
						LifeBoxContract.Types._ID,
						mediaType
				);
	}

	/**
	 * Queries the database to get the primary key of the table filetypes,
	 * in order to figure out what thumbnail-mimetype matchas a given (file-)mimeType.
	 * @param mimeType (String) the mimetype that have to match
	 * @return _id (long) the primary key of the row
	 */
	public long selectFiletypesId(String mimeType)
	{
		long filetypesId = -1;
		// decide what MIME-Type the thumbnail is of
		if(mimeType.equals(Constants.MIME_TYPE_IMAGE))
		{
			filetypesId = selectId
					(
							LifeBoxContract.Filetypes.TABLE_NAME,
							LifeBoxContract.Filetypes.COLUMN_NAME_FILETYPE,
							LifeBoxContract.Filetypes._ID,
							Constants.MIME_TYPE_IMAGE_THUMB
					);
		}
		else if(mimeType.equals(Constants.MIME_TYPE_VIDEO))
		{
			filetypesId = selectId
					(
							LifeBoxContract.Filetypes.TABLE_NAME,
							LifeBoxContract.Filetypes.COLUMN_NAME_FILETYPE,
							LifeBoxContract.Filetypes._ID,
							Constants.MIME_TYPE_VIDEO_THUMB
					);
		}

		return filetypesId;
	}

	/**
	 * Performs regular insert into a given table.
	 * @param table (String) the target table
	 * @param unique (boolean) if the row should be unique or not
	 * @param keyValuePair (KeyValuePair [varargs]) variable number of key value pairs,
	 *                     where column=key, insertfield=value
	 * @return _id (long) of the newly inserted row
	 */
	public long insertRegular(String table, boolean unique, KeyValuePair... keyValuePair)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		for(KeyValuePair kvp : keyValuePair)
		{
			if(kvp.getValueIsString() == true)
			{
				if(kvp.getStringValue().trim().equals(""))
				{
					// insert NULL
					values.putNull(kvp.getKey());
					Log.d(kvp.getKey(), "null i am stfu joda");
				}
				else
				{
					Log.d(kvp.getKey(), kvp.getStringValue());
					values.put(kvp.getKey(), kvp.getStringValue());
				}

			}
			else if(kvp.getValueIsString() == false)
			{
				if(kvp.getLongValue() == 0)
				{
					// insert NULL
					values.putNull(kvp.getKey());
					Log.d(kvp.getKey(), "null i am stfu joda");
				}
				else
				{
					Log.d(kvp.getKey(), ""+kvp.getLongValue());
					values.put(kvp.getKey(), kvp.getLongValue());
				}

			}
		}

		// get the key
		long id = -1;

		if(unique == false)
		{
			id = db.insertOrThrow(table, null, values);
		}
		else if(unique == true)
		{
			// only insert new rows, but always retrieve an id
			id = db.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_IGNORE);
		}

		// cleanup
		values.clear();
		db.close();

		return id;
	}

	/**
	 * Inserts all new hashtags of a List into the hashtags-table
	 * and inserts all hashtags that belong to a single entry.
	 * @param entriesId (long) the primary key of the entry
	 * @param hashtagList (ArrayList<String>) List containing the hashtags
	 */
	public void insertHashtags(long entriesId, ArrayList<String> hashtagList)
	{
		if(!hashtagList.isEmpty())
		{
			// initialize db object
			SQLiteDatabase db = this.getWritableDatabase();

			// for all hashtags in the List
			for(String hashtag : hashtagList)
			{
				// get the id (long > 0 || -1)
				long hashtagsId = selectId
						(
								LifeBoxContract.Hashtags.TABLE_NAME,
								LifeBoxContract.Hashtags.COLUMN_NAME_HASHTAG,
								LifeBoxContract.Hashtags._ID,
								hashtag
						);
				Log.d("hashtag", hashtag+" id: "+hashtagsId+" found");

				// insert if new
				if(hashtagsId == -1)
				{
					hashtagsId = insertRegular
							(
									LifeBoxContract.Hashtags.TABLE_NAME,
									true,
									new KeyValuePair(LifeBoxContract.Hashtags.COLUMN_NAME_HASHTAG, hashtag)
							);

					Log.d("hashtag", hashtag+" id: "+hashtagsId+" inserted into hashtags");
				}

				// always insert the hashtag in enty_hashtags
				long entryHashtagsId = insertRegular
						(
								LifeBoxContract.EntryHashtags.TABLE_NAME,
								false,
								new KeyValuePair(LifeBoxContract.EntryHashtags.COLUMN_NAME_ENTRIES_ID, entriesId),
								new KeyValuePair(LifeBoxContract.EntryHashtags.COLUMN_NAME_HASHTAGS_ID, hashtag)
						);
				Log.d("hashtag", hashtag+" id: "+hashtagsId+" inserted into entry_hashtags");
			}

			db.close();
		}
	}

	/**
	 * Inserts all tags that belong to a single entry.
	 * @param entriesId (long) the primary key of the entry
	 * @param tagList (ArrayList<String>) List containing the tagnames
	 */
	public void insertEntryTags(long entriesId, ArrayList<String> tagList)
	{
		if(!tagList.isEmpty())
		{
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues();

			for(String tag : tagList)
			{
				// get the db-key of the tag(name)
				long tagsId = selectId
						(
								LifeBoxContract.Tags.TABLE_NAME,
								LifeBoxContract.Tags.COLUMN_NAME_TAG,
								LifeBoxContract.Tags._ID, tag
						);

				values.put(LifeBoxContract.EntryTags.COLUMN_NAME_ENTRIES_ID, entriesId);
				values.put(LifeBoxContract.EntryTags.COLUMN_NAME_TAGS_ID, tagsId);

				long id = db.insertOrThrow(LifeBoxContract.EntryTags.TABLE_NAME, null, values);
			}

			db.close();
		}
	}
	//##################################################################################################################
	// SELECT ----------------------------------------------------------------------------------------------------------
	//##################################################################################################################
	/**
	 * Queries the entries table of the database in order to retrieve
	 * _id, media_id, types_id, title, description and user_date,
	 * which is the intersection of all timelineEntries.
	 * @param limit (int) the amount of rows, the function should return.
	 * @return a 2d array with the media_ids and the types_ids.
	 */
	public String[][] selectEntrySet(int limit, int offset)
	{
		final int COLUMNAMOUNT = 6;
		String entryList[][] = null;

		SQLiteDatabase db = this.getReadableDatabase();

		// ~SELECT entries._id, entries.media_id, entries.types_id, entries.title, entries.description,
		// entries.user_date, types.type FROM entries LEFT OUTER JOIN types ON entries.types_id = types._id
		// ORDER BY user_date DESC LIMIT 10 OFFSET 0
		String query = "SELECT " + LifeBoxContract.Entries.TABLE_NAME + "." + LifeBoxContract.Entries._ID + "," +
				LifeBoxContract.Entries.TABLE_NAME + "." + LifeBoxContract.Entries.COLUMN_NAME_MEDIA_ID + "," +
				LifeBoxContract.Entries.TABLE_NAME + "." + LifeBoxContract.Entries.COlUMN_NAME_TITLE + "," +
				LifeBoxContract.Entries.TABLE_NAME + "." + LifeBoxContract.Entries.COlUMN_NAME_DESCRIPTION + "," +
				LifeBoxContract.Entries.TABLE_NAME + "." + LifeBoxContract.Entries.COlUMN_NAME_USER_DATE + "," +
				LifeBoxContract.Types.TABLE_NAME + "." + LifeBoxContract.Types.COLUMN_NAME_TYPE + " FROM " +
				LifeBoxContract.Entries.TABLE_NAME + " LEFT OUTER JOIN " + LifeBoxContract.Types.TABLE_NAME +
				" ON " + LifeBoxContract.Entries.TABLE_NAME + "." + LifeBoxContract.Entries.COLUMN_NAME_TYPES_ID +
				" = " + LifeBoxContract.Types.TABLE_NAME + "." + LifeBoxContract.Types._ID + " ORDER BY " +
				LifeBoxContract.Entries.TABLE_NAME + "." + LifeBoxContract.Entries.COlUMN_NAME_USER_DATE +
				" LIMIT " + limit + " OFFSET " + offset + ";";

		Cursor c = db.rawQuery(query, null);

		// fetch the rows, save the ids
		if(c.moveToFirst())
		{
			entryList = new String[c.getCount()][COLUMNAMOUNT];
			int i = 0;

			while(!c.isAfterLast())
			{
				entryList[i][0] = String.valueOf(c.getLong(c.getColumnIndexOrThrow(LifeBoxContract.Entries._ID)));
				entryList[i][1] = String.valueOf(c.getLong(c.getColumnIndexOrThrow(LifeBoxContract.Entries.COLUMN_NAME_MEDIA_ID)));
				entryList[i][2] = c.getString(c.getColumnIndexOrThrow(LifeBoxContract.Types.COLUMN_NAME_TYPE));
				entryList[i][3] = c.getString(c.getColumnIndexOrThrow(LifeBoxContract.Entries.COlUMN_NAME_TITLE));
				// could be null
				if(c.isNull(c.getColumnIndexOrThrow(LifeBoxContract.Entries.COlUMN_NAME_DESCRIPTION)))
				{
					entryList[i][4] = "";
				}
				else
				{
					entryList[i][4] = c.getString(c.getColumnIndexOrThrow(LifeBoxContract.Entries.COlUMN_NAME_DESCRIPTION));
				}
				entryList[i][5] = String.valueOf(c.getLong(c.getColumnIndexOrThrow(LifeBoxContract.Entries.COlUMN_NAME_USER_DATE)));

				i++;
				c.moveToNext();
			}
		}

		// clean up
		c.close();
		db.close();

		return entryList;
	}

	/**
	 * Query the database in order to retrieve a column of a single row, matching a condition.
	 * @param table (String) the database table
	 * @param columns (String[]) the _id column and the needed column as array
	 * @param selection (String) the condition
	 * @param isString (boolean) weather the field is a String (or a long)
	 * @return the matching single row. Or null if none exists.
	 */
	public String selectSingleField(String table, String[] columns, String selection, boolean isString)
	{
		SQLiteDatabase db = this.getReadableDatabase();

		// fetch the row
		Cursor c = db.query
				(
						table,
						columns,
						selection,
						null,
						null,
						null,
						null
				);

		String result = null;

		if(c.moveToFirst())
		{
			if(isString)
			{
				result = c.getString(c.getColumnIndexOrThrow(columns[1]));
			}
			else
			{
				result = String.valueOf(c.getLong(c.getColumnIndexOrThrow(columns[1])));
			}
		}

		// clean up
		c.close();
		db.close();

		return result;
	}

	/**
	 * Queries the database to retrieve a single column without any conditions
	 * @param table (String) the table of the column
	 * @param columns (String) the column _id + the requested column
	 * @return (ArrayList<String>) the row stored in a list
	 */
	public ArrayList<String> selectWholeRow(String table, String[] columns)
	{
		ArrayList<String> rowList= new ArrayList<String>();
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor c = db.query
				(
						table,
						columns,
						null,
						null,
						null,
						null,
						null
				);

		if(c.moveToFirst())
		{
			while(!c.isAfterLast())
			{
				rowList.add(c.getString(c.getColumnIndexOrThrow(columns[1])));

				c.moveToNext();
			}
		}

		return rowList;
	}

	/**
	 * Selects path from offline_files, which is the thumbnail path.
	 * @param mediaId (String) the _id of files is the condition
	 * @return (String) the path to the thumbnail
	 */
	public String selectFileThumbnail(String mediaId)
	{
		// get the offline_files_id that belongs to the files_id
		SQLiteDatabase db = this.getReadableDatabase();

		// projection
		String[] columns =
				{
						LifeBoxContract.FileOfflineFiles._ID,
						LifeBoxContract.FileOfflineFiles.COLUMN_NAME_OFFLINE_FILES_ID
				};

		// selection
		String selection = LifeBoxContract.FileOfflineFiles.COLUMN_NAME_FILES_ID + " == " + mediaId;

		// fetch the row
		Cursor c = db.query
				(
						LifeBoxContract.FileOfflineFiles.TABLE_NAME,
						columns,
						selection,
						null,
						null,
						null,
						null
				);

		String thumbnailPath = null;

		if(c.moveToFirst())
		{
			long id;
			String query = null;
			Cursor cThumb = null;

			while(!c.isAfterLast())
			{
				id = c.getLong(c.getColumnIndexOrThrow(LifeBoxContract.FileOfflineFiles.COLUMN_NAME_OFFLINE_FILES_ID));

				/* ~query
				SELECT path FROM offline_files LEFT OUTER JOIN filetypes ON offline_files.filetypes_id = filetypes._id
				WHERE offline_files._id == 1 GROUP BY filetypes.filetype HAVING filetype == 'video/thumbnail'
				OR filetype == 'image/thumbnail';
				 */
				query = "SELECT " + LifeBoxContract.OfflineFiles.COLUMN_NAME_PATH + " FROM " +
						LifeBoxContract.OfflineFiles.TABLE_NAME + " LEFT OUTER JOIN " +
						LifeBoxContract.Filetypes.TABLE_NAME + " ON " + LifeBoxContract.OfflineFiles.TABLE_NAME + "." +
						LifeBoxContract.OfflineFiles.COLUMN_NAME_FILETYPES_ID + " = " +
						LifeBoxContract.Filetypes.TABLE_NAME + "." + LifeBoxContract.Filetypes._ID +
						" WHERE " +LifeBoxContract.OfflineFiles.TABLE_NAME + "." + LifeBoxContract.OfflineFiles._ID +
						" == " + id + " GROUP BY " + LifeBoxContract.Filetypes.TABLE_NAME + "." +
						LifeBoxContract.Filetypes.COLUMN_NAME_FILETYPE + " HAVING " +
						LifeBoxContract.Filetypes.COLUMN_NAME_FILETYPE + " == '" + Constants.MIME_TYPE_VIDEO_THUMB +
						"' OR " + LifeBoxContract.Filetypes.COLUMN_NAME_FILETYPE + " == '" +
						Constants.MIME_TYPE_IMAGE_THUMB + "';";

				cThumb = db.rawQuery(query, null);

				if(cThumb.moveToFirst())
				{
					thumbnailPath = cThumb.getString(cThumb.getColumnIndexOrThrow(LifeBoxContract.OfflineFiles.COLUMN_NAME_PATH));
					break;
				}

				c.moveToNext();
			}
		}

		// clean up
		c.close();
		db.close();

		return thumbnailPath;
	}

	/**
	 * Selects filetype from filetype, matching a condition.
	 * @param mediaId (String) the _id of files is the condition
	 * @return (String) the filetype of the files given _id
	 */
	public String selectFiletypesFiletype(String mediaId)
	{
		String filetype = null;

		SQLiteDatabase db = this.getReadableDatabase();

				/* ~query
				SELECT filetype FROM filetypes WHERE filetypes._id = (SELECT filetypes_id FROM files WHERE files._id = 2);
				 */
		String query = "SELECT " + LifeBoxContract.Filetypes.COLUMN_NAME_FILETYPE + " FROM " +
				LifeBoxContract.Filetypes.TABLE_NAME + " WHERE " + LifeBoxContract.Filetypes.TABLE_NAME + "." +
				LifeBoxContract.Filetypes._ID +	" = (SELECT " +LifeBoxContract.Files.COLUMN_NAME_FILETYPES_ID +
				" FROM " + LifeBoxContract.Files.TABLE_NAME + " WHERE " + LifeBoxContract.Files.TABLE_NAME +
				"." + LifeBoxContract.Files._ID + " = " +	mediaId + ");";

		Cursor c = db.rawQuery(query, null);

		if(c.moveToFirst())
		{
			// could be null
			if(c.isNull(c.getColumnIndexOrThrow(LifeBoxContract.Filetypes.COLUMN_NAME_FILETYPE)))
			{
				filetype = "";
			}
			else
			{
				filetype = c.getString(c.getColumnIndexOrThrow(LifeBoxContract.Filetypes.COLUMN_NAME_FILETYPE));
			}
		}

		// clean up
		c.close();
		db.close();

		return filetype;
	}

	/**
	 * Selects track, artist and thumbnail from music, matching a condition
	 * @param musicId (String) the _id of music is the condition
	 * @return (HashMap<String, String>) storing track, artist and thumbnail of the selected music row
	 */
	public Map<String, String> selectMusic(String musicId)
	{
		// the result (associative) List
		Map<String, String> musicList = new HashMap<String, String>();

		SQLiteDatabase db = this.getReadableDatabase();

		/* ~query
		SELECT track, artist, thumbnail_url FROM music
		LEFT OUTER JOIN artists ON music.artists_id = artists._id
		LEFT OUTER JOIN albums ON music.albums_id = albums._id
		WHERE music._id = 1
		 */

		String query = "SELECT " + LifeBoxContract.Music.COLUMN_NAME_TRACK + ", " +
				LifeBoxContract.Artists.COLUMN_NAME_ARTIST + ", " + LifeBoxContract.Albums.COLUMN_NAME_THUMBNAIL_URL +
				" FROM " + LifeBoxContract.Music.TABLE_NAME + " LEFT OUTER JOIN " +
				LifeBoxContract.Artists.TABLE_NAME + " ON " + LifeBoxContract.Music.TABLE_NAME + "." +
				LifeBoxContract.Music.COLUMN_NAME_ARTISTS_ID + " = " + LifeBoxContract.Artists.TABLE_NAME + "." +
				LifeBoxContract.Artists._ID + " LEFT OUTER JOIN " + LifeBoxContract.Albums.TABLE_NAME + " ON " +
				LifeBoxContract.Music.TABLE_NAME + "." + LifeBoxContract.Music.COLUMN_NAME_ALBUMS_ID + " = " +
				LifeBoxContract.Albums.TABLE_NAME + "." + LifeBoxContract.Albums._ID + " WHERE " +
				LifeBoxContract.Music.TABLE_NAME + "." + LifeBoxContract.Music._ID + " = " + musicId + ";";

		Cursor c = db.rawQuery(query, null);

		// fetch the rows, save the ids
		if(c.moveToFirst())
		{
			while(!c.isAfterLast())
			{
				musicList.put("Track", c.getString(c.getColumnIndexOrThrow(LifeBoxContract.Music.COLUMN_NAME_TRACK)));
				musicList.put("Artist", c.getString(c.getColumnIndexOrThrow(LifeBoxContract.Artists.COLUMN_NAME_ARTIST)));

				// could be null
				if(c.isNull(c.getColumnIndexOrThrow(LifeBoxContract.Albums.COLUMN_NAME_THUMBNAIL_URL)))
				{
					musicList.put("Music Thumbnail", "");
				}
				else
				{
					musicList.put("Music Thumbnail", c.getString(c.getColumnIndexOrThrow(LifeBoxContract.Albums.COLUMN_NAME_THUMBNAIL_URL)));
				}

				c.moveToNext();
			}
		}

		// clean up
		c.close();
		db.close();

		return musicList;
	}

	/**
	 * Selects title, director and thumbnail from movies, matching a condition
	 * @param movieId (String) the _id of movies is the condition
	 * @return (HashMap<String, String>) storing title, director and thumbnail of the selected movie row
	 */
	public Map<String, String> selectMovie(String movieId)
	{
		// the result (associative) List
		Map<String, String> movieList = new HashMap<String, String>();

		SQLiteDatabase db = this.getReadableDatabase();

		String[] columns =
				{
						LifeBoxContract.Movies._ID,
						LifeBoxContract.Movies.COLUMN_NAME_TITLE,
						LifeBoxContract.Movies.COLUMN_NAME_DIRECTOR,
						LifeBoxContract.Movies.COLUMN_NAME_THUMBNAIL_URL
				};

		String selection = LifeBoxContract.Movies._ID + " = " + movieId;

		Cursor c = db.query
				(
						LifeBoxContract.Movies.TABLE_NAME,
						columns,
						selection,
						null,
						null,
						null,
						null
				);

		// fetch the rows, save the ids
		if(c.moveToFirst())
		{
			while(!c.isAfterLast())
			{
				movieList.put("Movie Title", c.getString(c.getColumnIndexOrThrow(LifeBoxContract.Movies.COLUMN_NAME_TITLE)));
				movieList.put("Director", c.getString(c.getColumnIndexOrThrow(LifeBoxContract.Movies.COLUMN_NAME_DIRECTOR)));

				// could be null
				if(c.isNull(c.getColumnIndexOrThrow(LifeBoxContract.Movies.COLUMN_NAME_THUMBNAIL_URL)))
				{
					movieList.put("Movie Thumbnail", "");
				}
				else
				{
					movieList.put("Movie Thumbnail", c.getString(c.getColumnIndexOrThrow(LifeBoxContract.Movies.COLUMN_NAME_THUMBNAIL_URL)));
				}

				c.moveToNext();
			}
		}

		// clean up
		c.close();
		db.close();

		return movieList;
	}
}
