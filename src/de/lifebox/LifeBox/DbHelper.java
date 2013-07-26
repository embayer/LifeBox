package de.lifebox.LifeBox;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import de.lifebox.LifeBox.Constants;
import de.lifebox.LifeBox.LifeBoxContract;

import java.util.ArrayList;

/**
 * Base-Class for all DbHelper classes
 * @version 0.1 09.07.13
 * @autor Markus Bayer
 */
public class DbHelper extends SQLiteOpenHelper
{
	// if the database schema has changed, the database version must be increased.
	public static final int DATABASE_VERSION = 14;
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
		types.add(Constants.TYPE_FILE);
		types.add(Constants.TYPE_MOVIE);
		types.add(Constants.TYPE_MUSIC);
		types.add(Constants.TYPE_TEXT);

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
}
