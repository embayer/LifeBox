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
 * Class for all database related actions.
 * @version 0.1 09.07.13
 * @autor Markus Bayer
 */
public class DbHelper extends SQLiteOpenHelper
{
	private static final String TAG = "DbHelper";
	// if the database schema has changed, the database version must be increased.
	public static final int DATABASE_VERSION = 20;
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
	// DELETE ----------------------------------------------------------------------------------------------------------
	//##################################################################################################################

	/**
	 * Delete all rows in all tables associated with a mediafile.
	 * @param entriesId (String) the entries._id
	 * @param mediaId (String) entries.media_id
	 * @return true if all associated rows where deleted otherwise false.
	 */
	public boolean deleteFile(String entriesId, String mediaId)
	{
		SQLiteDatabase db = getWritableDatabase();

		// the result
		boolean success = false;

		int deletedRows = 0;

		// delete entries row
		// where clause for entries
		String clause = LifeBoxContract.Entries._ID + " == " + entriesId;

		deletedRows += db.delete(LifeBoxContract.Entries.TABLE_NAME, clause, null);

		// delete files row
		clause = LifeBoxContract.Files._ID + " == " + mediaId;

		deletedRows += db.delete(LifeBoxContract.Files.TABLE_NAME, clause, null);

		// delete offline_files rows
		// get the offline_files._id(s) from file_offline_files
		// projection
		String[] columns = {LifeBoxContract.FileOfflineFiles.COLUMN_NAME_OFFLINE_FILES_ID};

		// selection
		String selection = LifeBoxContract.FileOfflineFiles.COLUMN_NAME_FILES_ID + " == " + mediaId;

		Cursor c = db.query(LifeBoxContract.FileOfflineFiles.TABLE_NAME, columns, selection,null, null, null,null);

		ArrayList<String> offlineFilesIds = new ArrayList<String>();

		// if there are results
		if(c.moveToFirst())
		{
			// for all results
			while(!c.isAfterLast())
			{
				offlineFilesIds.add(String.valueOf(c.getLong(
						c.getColumnIndexOrThrow(LifeBoxContract.FileOfflineFiles.COLUMN_NAME_OFFLINE_FILES_ID))));

				c.moveToNext();
			}
		}

		c.close();

		// results?
		if(offlineFilesIds.size() > 0)
		{
			for(String offlineFileId : offlineFilesIds)
			{
				clause = LifeBoxContract.OfflineFiles._ID + " == " + offlineFileId;

				deletedRows += db.delete(LifeBoxContract.OfflineFiles.TABLE_NAME, clause, null);
			}
		}

		// delete file_offline_files rows
		clause = LifeBoxContract.FileOfflineFiles.COLUMN_NAME_FILES_ID + " == " + mediaId;

		deletedRows += db.delete(LifeBoxContract.FileOfflineFiles.TABLE_NAME, clause, null);

		// delete online_files rows
		// get the online_files._id(s) from file_online_files
		// projection
		String[] onlineColumns = {LifeBoxContract.FileOnlineFiles.COLUMN_NAME_ONLINE_FILES_ID};

		// selection
		selection = LifeBoxContract.FileOnlineFiles.COLUMN_NAME_FILES_ID + " == " + mediaId;

		c = db.query(LifeBoxContract.FileOnlineFiles.TABLE_NAME, onlineColumns, selection,null, null, null,null);

		ArrayList<String> onlineFilesIds = new ArrayList<String>();

		// if there are results
		if(c.moveToFirst())
		{
			// for all results
			while(!c.isAfterLast())
			{
				onlineFilesIds.add(String.valueOf(c.getLong(
						c.getColumnIndexOrThrow(LifeBoxContract.FileOnlineFiles.COLUMN_NAME_ONLINE_FILES_ID))));

				c.moveToNext();
			}
		}

		c.close();

		// results?
		if(onlineFilesIds.size() > 0)
		{
			for(String onlineFileId : onlineFilesIds)
			{
				clause = LifeBoxContract.OnlineFiles._ID + " == " + onlineFileId;

				deletedRows += db.delete(LifeBoxContract.OnlineFiles.TABLE_NAME, clause, null);
			}
		}

		// delete file_online_files rows
		clause = LifeBoxContract.FileOnlineFiles.COLUMN_NAME_FILES_ID + " == " + mediaId;

		deletedRows += db.delete(LifeBoxContract.FileOnlineFiles.TABLE_NAME, clause, null);

		// delete entry_tags row(s)
		deleteEntryTags(entriesId);

		// delete entry_hashtags (if there any) and hashtags row(s) (if they was unique)
		deleteHashtags(entriesId);

		// check if all rows where deleted
		// 1x entries, 1x files, 2x offline_files, 2x file_offline_files, 2x online_files, 2x file_online_files = 10
		// tags and hashtags are variable so they don't count
		if(deletedRows == 10)
		{
			success = true;
		}

		// clean up
		db.close();

		return success;
	}

	/**
	 * Delete all rows in all tables associated with a text.
	 * @param entriesId (String) the entries._id
	 * @param mediaId (String) entries.media_id
	 * @return true if all associated rows where deleted otherwise false.
	 */
	public boolean deleteText(String entriesId, String mediaId)
	{
		SQLiteDatabase db = getWritableDatabase();

		// the result
		boolean success = false;

		int deletedRows = 0;

		// delete entries row
		String clause = LifeBoxContract.Entries._ID + " == " + entriesId;

		deletedRows += db.delete(LifeBoxContract.Entries.TABLE_NAME, clause, null);

		// delete text row
		clause = LifeBoxContract.Text._ID + " == " + mediaId;

		deletedRows += db.delete(LifeBoxContract.Text.TABLE_NAME, clause, null);

		// delete entry_tags row(s)
		deleteEntryTags(entriesId);

		// delete entry_hashtags (if there any) and hashtags row(s) (if they was unique)
		deleteHashtags(entriesId);

		// check if all rows where deleted
		// 1x entries, 1x text = 2
		if(deletedRows == 2)
		{
			success = true;
		}

		// clean up
		db.close();

		return success;
	}

	/**
	 * Delete all rows in all tables associated with a music.
	 * @param entriesId (String) the entries._id
	 * @param mediaId (String) entries.media_id
	 * @return true if all associated rows where deleted otherwise false.
	 */
	public boolean deleteMusic(String entriesId, String mediaId)
	{
		SQLiteDatabase db = getWritableDatabase();

		// the result
		boolean success = false;

		int deletedRows = 0;

		// delete entries row
		String clause = LifeBoxContract.Entries._ID + " == " + entriesId;

		deletedRows += db.delete(LifeBoxContract.Entries.TABLE_NAME, clause, null);

		// get artists_id and albums_id from music
		String[] columns = {LifeBoxContract.Music.COLUMN_NAME_ARTISTS_ID, LifeBoxContract.Music.COLUMN_NAME_ALBUMS_ID};

		String selection = LifeBoxContract.Music._ID + " == " + mediaId;

		Cursor c = db.query(LifeBoxContract.Music.TABLE_NAME, columns, selection, null, null, null, null);

		String artistsId = "";
		String albumsId = "";

		if(c.moveToFirst())
		{
			artistsId = String.valueOf(c.getLong(c.getColumnIndexOrThrow(LifeBoxContract.Music.COLUMN_NAME_ARTISTS_ID)));
			albumsId = String.valueOf(c.getLong(c.getColumnIndexOrThrow(LifeBoxContract.Music.COLUMN_NAME_ALBUMS_ID)));
		}

		c.close();

		// delete music row
		clause = LifeBoxContract.Music._ID + " == " + mediaId;

		deletedRows += db.delete(LifeBoxContract.Music.TABLE_NAME, clause, null);

		// delete albums row
		clause = LifeBoxContract.Albums._ID + " == " + albumsId;

		deletedRows += db.delete(LifeBoxContract.Albums.TABLE_NAME, clause, null);

		// delete music_genres row
		clause = LifeBoxContract.MusicGenres._ID + " == " + mediaId;

		deletedRows += db.delete(LifeBoxContract.MusicGenres.TABLE_NAME, clause, null);

		// check if the deleted artist was unique
		selection = LifeBoxContract.Music.COLUMN_NAME_ARTISTS_ID + " == " + artistsId;

		c = db.query(LifeBoxContract.Music.TABLE_NAME, null, selection, null, null, null, null);

		if(c.getCount() == 0)
		{
			// artist was unique
			// delete artists row
			clause = LifeBoxContract.Artists._ID + " == " + artistsId;

			deletedRows += db.delete(LifeBoxContract.Artists.TABLE_NAME, clause, null);
		}

		c.close();

		// delete entry_tags row(s)
		deleteEntryTags(entriesId);

		// delete entry_hashtags (if there any) and hashtags row(s) (if they was unique)
		deleteHashtags(entriesId);

		// check if all rows where deleted
		// 1x entries, 1x music, 1x artists, 1x albums, 1x music_genres = 5
		if(deletedRows == 5)
		{
			success = true;
		}

		// clean up
		db.close();

		return success;
	}

	/**
	 * Delete all rows in all tables associated with a movie.
	 * @param entriesId (String) the entries._id
	 * @param mediaId (String) entries.media_id
	 * @return true if all associated rows where deleted otherwise false.
	 */
	public boolean deleteMovie(String entriesId, String mediaId)
	{
		SQLiteDatabase db = getWritableDatabase();

		// the result
		boolean success = false;

		int deletedRows = 0;

		// delete entries row
		String clause = LifeBoxContract.Entries._ID + " == " + entriesId;

		deletedRows += db.delete(LifeBoxContract.Entries.TABLE_NAME, clause, null);

		// delete movies row
		clause = LifeBoxContract.Movies._ID + " == " + mediaId;

		deletedRows += db.delete(LifeBoxContract.Movies.TABLE_NAME, clause, null);

		// delete entry_tags row(s)
		deleteEntryTags(entriesId);

		// delete entry_hashtags (if there any) and hashtags row(s) (if they was unique)
		deleteHashtags(entriesId);

		// check if all rows where deleted
		// 1x entries, 1x movies = 2
		if(deletedRows == 2)
		{
			success = true;
		}

		// clean up
		db.close();

		return success;
	}

	/**
	 * Delete all rows of entry_tags matching a given entriesId.
	 * @param entriesId (String) the entries._id is the condition
	 */
	public void deleteEntryTags(String entriesId)
	{
		SQLiteDatabase db = getWritableDatabase();

		String clause = LifeBoxContract.EntryTags.COLUMN_NAME_ENTRIES_ID + " == " + entriesId;

		db.delete(LifeBoxContract.EntryTags.TABLE_NAME, clause, null);

		db.close();
	}

	/**
	 * Delete all rows of entry_hashtags matching the given entriesId,
	 * and all rows of hashtags if the corresponding row of entry_hashtags was unique.
	 * @param entriesId (String) the entries._id is the condition
	 */
	public void deleteHashtags(String entriesId)
	{
		SQLiteDatabase db = getWritableDatabase();

		// select entry_hashtags.hashtags_id for the given entries._id
		String[] columns = {LifeBoxContract.EntryHashtags.COLUMN_NAME_HASHTAGS_ID};

		String selection = LifeBoxContract.EntryHashtags.COLUMN_NAME_ENTRIES_ID + " == " + entriesId;

		Cursor c = db.query(LifeBoxContract.EntryHashtags.TABLE_NAME, columns, selection, null, null, null, null);

		ArrayList<String> hashtagsIds = new ArrayList<String>();

		String clause;

		if(c.moveToFirst())
		{
			while(!c.isAfterLast())
			{
				// save the hashtags
				hashtagsIds.add(String.valueOf(
						c.getLong(c.getColumnIndexOrThrow(LifeBoxContract.EntryHashtags.COLUMN_NAME_HASHTAGS_ID))));

				c.moveToNext();
			}

			// delete entry_hashtags
			clause = LifeBoxContract.EntryHashtags.COLUMN_NAME_ENTRIES_ID + " == " + entriesId;

			db.delete(LifeBoxContract.EntryHashtags.TABLE_NAME, clause, null);
		}

		c.close();

		// check if the deleted hashtags where unique and save them
		ArrayList<String> uniqueHashtagsIds = new ArrayList<String>();

		if(hashtagsIds.size() > 0)
		{
			for(String hashtagId : hashtagsIds)
			{
				selection = LifeBoxContract.EntryHashtags.COLUMN_NAME_HASHTAGS_ID + " == " + hashtagId;

				c = db.query(LifeBoxContract.EntryHashtags.TABLE_NAME, null, selection, null, null, null, null);

				// if there are no results (after deleting) the deleted hashtag was unique -> save
				if(c.getCount() == 0)
				{
					uniqueHashtagsIds.add(hashtagId);
				}
			}

			c.close();
		}

		// delete from hashtags
		// are there unique hashtags?
		if(uniqueHashtagsIds.size() > 0)
		{
			for(String hashtagsId : uniqueHashtagsIds)
			{
				clause = LifeBoxContract.Hashtags._ID + " == " + hashtagsId;

				db.delete(LifeBoxContract.Hashtags.TABLE_NAME, clause, null);
			}
		}

		db.close();
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
					Log.d(kvp.getKey(), "null");
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
					Log.d(kvp.getKey(), "null");
				}
				else
				{
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
								new KeyValuePair(LifeBoxContract.EntryHashtags.COLUMN_NAME_HASHTAGS_ID, hashtagsId)
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

				SQLiteDatabase db = getWritableDatabase();

				long id = db.insertOrThrow(LifeBoxContract.EntryTags.TABLE_NAME, null, values);
				db.close();
			}
		}
	}
	//##################################################################################################################
	// SELECT ----------------------------------------------------------------------------------------------------------
	//##################################################################################################################

	/**
	 * Select the total amount of entries.
	 * @return (String) the total amount of entries
	 */
	public String selectCountEntries()
	{
		String result = "";

		SQLiteDatabase db = getReadableDatabase();

		/* ~query
		SELECT COUNT(_id) FROM entries;
		 */
		String query =
				"SELECT COUNT(" + LifeBoxContract.Entries._ID + ") FROM " + LifeBoxContract.Entries.TABLE_NAME + ";";

		Cursor c = db.rawQuery(query, null);

		if(c.moveToFirst())
		{
			result = String.valueOf(c.getInt(c.getColumnIndexOrThrow("COUNT(" + LifeBoxContract.Entries._ID + ")")));
		}

		db.close();

		return result;
	}

	/**
	 * Select the total amount of files
	 * @param filetype (String) ['image/jpg' || 'video/mp4']
	 * @return (String) the total amount of (image or video) files
	 */
	public String selectCountFiles(String filetype)
	{
		String result = "";

		SQLiteDatabase db = getReadableDatabase();

		/* ~query
		SELECT COUNT(e._id) FROM entries  e
		INNER JOIN (files f
		INNER JOIN filetypes ft
		ON ft._id = f.filetypes_id) AS ft
		ON ft._id = e.media_id
		WHERE filetype == 'image/jpeg';
		 */
		String query =
				"SELECT COUNT(e." + LifeBoxContract.Entries._ID + ") FROM " + LifeBoxContract.Entries.TABLE_NAME + " e " +
				"INNER JOIN (" + LifeBoxContract.Files.TABLE_NAME + " f " +
				"INNER JOIN " + LifeBoxContract.Filetypes.TABLE_NAME + " ft " +
				" ON ft." + LifeBoxContract.Filetypes._ID + " = f." + LifeBoxContract.Files.COLUMN_NAME_FILETYPES_ID +
				") AS ft " +
				"ON ft." + LifeBoxContract.Files._ID + " = e." + LifeBoxContract.Entries.COLUMN_NAME_MEDIA_ID +
				" WHERE " + LifeBoxContract.Filetypes.COLUMN_NAME_FILETYPE + " == '" + filetype + "';";

		Cursor c = db.rawQuery(query, null);

		if(c.moveToFirst())
		{
			Log.e("errso", c.getColumnNames()[0]);
			result = String.valueOf(c.getInt(0));
		}

		db.close();

		return result;
	}

	/**
	 * Select the amount of mediatypes
	 * @param mediatype (String) [music || movies]
	 * @return (String) the total amount of (music or movie) mediatypes
	 */
	public String selectCountMedia(String mediatype)
	{
		String result = "";

		SQLiteDatabase db = getReadableDatabase();

		/* ~query
		SELECT COUNT(e._id) FROM entries  e
		INNER JOIN types t ON t._id = e.types_id
		WHERE t.type == 'music';
		 */
		String query =
				"SELECT COUNT(e." + LifeBoxContract.Entries._ID + ") FROM " + LifeBoxContract.Entries.TABLE_NAME + " e " +
						"INNER JOIN " + LifeBoxContract.Types.TABLE_NAME + " t " +
						"ON t." + LifeBoxContract.Types._ID + " = e." + LifeBoxContract.Entries.COLUMN_NAME_TYPES_ID +
						" WHERE t." + LifeBoxContract.Types.COLUMN_NAME_TYPE + " == '" + mediatype + "';";

		Cursor c = db.rawQuery(query, null);

		if(c.moveToFirst())
		{
			result = String.valueOf(c.getInt(0));
		}

		db.close();

		return result;
	}

	/**
	 * Select the amount of a specific field.
	 * @param field (String) the field-column to count
	 * @param table (String) the table holding the field
	 * @return (String) the amount of appearance
	 */
	public String selectCount(String field, String table)
	{
		String result = "";

		SQLiteDatabase db = getReadableDatabase();

		/* ~query
		SELECT COUNT(_id) FROM entry_tags;
		 */
		String query =
				"SELECT COUNT(DISTINCT " + field + ") FROM " + table + ";";


		Cursor c = db.rawQuery(query, null);

		if(c.moveToFirst())
		{
			result = String.valueOf(c.getInt(0));
		}

		db.close();

		return result;
	}

	/**
	 * Select a List of tags in ordered by use.
	 * @param limit (int) the amount of tags returned.
	 * @return (ArrayList<HashMap<String, String>>) List of tags and their usage.
	 */
	public ArrayList<HashMap<String, String>> selectTagUsage(int limit)
	{
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();

		SQLiteDatabase db = getReadableDatabase();

		/* ~query
		SELECT t.tag, COUNT(t._id) FROM entry_tags et
		INNER JOIN tags t ON t._id = et.tags_id
		GROUP BY t.tag
		ORDER BY COUNT(t._id) DESC LIMIT 10;
		 */

		String query = "SELECT t." + LifeBoxContract.Tags.COLUMN_NAME_TAG + ", COUNT(t." + LifeBoxContract.Tags._ID +
			") FROM " + LifeBoxContract.EntryTags.TABLE_NAME + " et " +
			"INNER JOIN " + LifeBoxContract.Tags.TABLE_NAME + " t ON t." + LifeBoxContract.Tags._ID + " = et." +
			LifeBoxContract.EntryTags.COLUMN_NAME_TAGS_ID +
			" GROUP BY t." + LifeBoxContract.Tags.COLUMN_NAME_TAG +
			" ORDER BY COUNT(t." + LifeBoxContract.Tags._ID + ") DESC LIMIT " + limit + ";";

		Cursor c = db.rawQuery(query, null);

		if(c.moveToFirst())
		{
			// the keys
			String tag = "tag";
			String count = "count";

			while(!c.isAfterLast())
			{
				HashMap<String, String> hm = new HashMap<String, String>();
				hm.put
						(
								tag,
								c.getString(c.getColumnIndexOrThrow("t." + LifeBoxContract.Tags.COLUMN_NAME_TAG))
						);
				hm.put
						(
								count,
								String.valueOf(c.getInt(1))
						);

				result.add(hm);

				c.moveToNext();
			}
		}

		db.close();

		return result;
	}

	/**
	 * Select a List of hashtags in ordered by use.
	 * @param limit (int) the amount of hashtags returned.
	 * @return (ArrayList<HashMap<String, String>>) List of hashtags and their usage.
	 */
	public ArrayList<HashMap<String, String>> selectHashtagUsage(int limit)
	{
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();

		SQLiteDatabase db = getReadableDatabase();

		/* ~query
		SELECT h.hashtag, COUNT(h._id) FROM entry_hashtags eh
		INNER JOIN hashtags h ON h._id = eh.hashtags_id
		GROUP BY h.hashtag
		ORDER BY COUNT(h._id) DESC LIMIT 10;
		 */

		String query = "SELECT h." + LifeBoxContract.Hashtags.COLUMN_NAME_HASHTAG + ", COUNT(h." +
				LifeBoxContract.Hashtags._ID +
				") FROM " + LifeBoxContract.EntryHashtags.TABLE_NAME + " eh " +
				"INNER JOIN " + LifeBoxContract.Hashtags.TABLE_NAME + " h ON h." + LifeBoxContract.Hashtags._ID + " = eh." +
				LifeBoxContract.EntryHashtags.COLUMN_NAME_HASHTAGS_ID +
				" GROUP BY h." + LifeBoxContract.Hashtags.COLUMN_NAME_HASHTAG +
				" ORDER BY COUNT(h." + LifeBoxContract.Hashtags._ID + ") DESC LIMIT " + limit + ";";

		Cursor c = db.rawQuery(query, null);

		if(c.moveToFirst())
		{
			// the keys
			String hashtag = "hashtag";
			String count = "count";

			while(!c.isAfterLast())
			{
				HashMap<String, String> hm = new HashMap<String, String>();
				hm.put
						(
								hashtag,
								c.getString(c.getColumnIndexOrThrow("h." + LifeBoxContract.Hashtags.COLUMN_NAME_HASHTAG))
						);
				hm.put
						(
								count,
								String.valueOf(c.getInt(1))
						);

				result.add(hm);

				c.moveToNext();
			}
		}

		db.close();

		return result;
	}

	/**
	 * Select a List of genres in ordered by use.
	 * @param genreColumn (String) the column of the genre
	 * @param table (String) the table holding the genreColumn
	 * @param limit (int) the amount of genres returned.
	 * @return (ArrayList<HashMap<String, String>>) List of genres and their usage.
	 */
	public ArrayList<HashMap<String, String>> selectGenreUsage(String genreColumn, String table, int limit)
	{
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();

		SQLiteDatabase db = getReadableDatabase();

		/* ~query
		SELECT music_genre, COUNT(music_genre) FROM music_genres
		GROUP BY music_genre
		ORDER BY COUNT(music_genre) DESC LIMIT 10;
		 */

		String query = "SELECT " + genreColumn + ", COUNT(" + genreColumn + ") FROM " + table +
				" GROUP BY " + genreColumn +
				" ORDER BY COUNT(" + genreColumn + ") DESC LIMIT " + limit + ";";

		Cursor c = db.rawQuery(query, null);

		if(c.moveToFirst())
		{
			// the keys
			String genre = "genre";
			String count = "count";

			while(!c.isAfterLast())
			{
				HashMap<String, String> hm = new HashMap<String, String>();
				hm.put
						(
								genre,
								c.getString(c.getColumnIndexOrThrow(genreColumn))
						);
				hm.put
						(
								count,
								String.valueOf(c.getInt(1))
						);

				result.add(hm);

				c.moveToNext();
			}
		}

		db.close();

		return result;
	}

	/**
	 * Select a Map of the first or last entrytitle.
	 * @param minOrMax (String) [MIN || MAX] if the first or last entry should be returned
	 * @return (HashMap<String, String>) of the first or last title and user_date
	 */
	public HashMap<String, String> selectExtremeTitle(String minOrMax)
	{
		HashMap<String, String> result = new HashMap<String, String>();

		SQLiteDatabase db = getReadableDatabase();

		/*
		SELECT title, MIN(user_date) FROM entries;
		 */

		String query = "SELECT " + LifeBoxContract.Entries.COlUMN_NAME_TITLE + ", " + minOrMax + "(" +
				LifeBoxContract.Entries.COlUMN_NAME_USER_DATE + ") FROM " + LifeBoxContract.Entries.TABLE_NAME + ";";

		Cursor c = db.rawQuery(query, null);

		if(c.moveToFirst())
		{
			result.put("title", c.getString(c.getColumnIndexOrThrow(LifeBoxContract.Entries.COlUMN_NAME_TITLE)));
			result.put("user_date", String.valueOf(c.getInt(1)));

			c.moveToNext();
		}

		db.close();

		return result;
	}

	/**
	 * Select the first user_date from entries.
	 * @return (long) the selected user_date or -1 if entries has no rows
	 */
	public long selectFirstUserDate()
	{
		// the result
		long firstUserDate = -1;

		SQLiteDatabase db = getReadableDatabase();

		String query = "SELECT MIN(" + LifeBoxContract.Entries.COlUMN_NAME_USER_DATE + ") FROM " +
				LifeBoxContract.Entries.TABLE_NAME + ";";

		Cursor c = db.rawQuery(query, null);

		if(c.moveToFirst())
		{
			firstUserDate = c.getLong(0);
		}

		// clean up
		c.close();
		db.close();

		return firstUserDate;
	}

	/**
	 * Select the last user_date from entries.
	 * @return (long) the selected user_date or -1 if entries has no rows
	 */
	public long selectLastUserDate()
	{
		// the result
		long lastUserDate = -1;

		SQLiteDatabase db = getReadableDatabase();

		String query = "SELECT MAX(" + LifeBoxContract.Entries.COlUMN_NAME_USER_DATE + ") FROM " +
				LifeBoxContract.Entries.TABLE_NAME + ";";

		Cursor c = db.rawQuery(query, null);

		if(c.moveToFirst())
		{
			lastUserDate = c.getLong(0);
		}

		// clean up
		c.close();
		db.close();

		return lastUserDate;
	}

	/**
	 * Select media_id, types_id, title, description, user_date from entries and the matching type from types.
	 * @param id the _id of the requested entry.
	 * @return (Entry) entry object from the target row.
	 */
	public Entry selectEntry(String id)
	{
		SQLiteDatabase db = getReadableDatabase();

		// result Entry
		Entry entry = null;

		/* ~query
		SELECT entries._id, entries.media_id, entries.types_id, entries.title, entries.description,
		entries.user_date, types.type FROM entries LEFT OUTER JOIN types ON entries.types_id = types._id
		WHERE entries._id == 1
		*/

		String query = "SELECT " + LifeBoxContract.Entries.TABLE_NAME + "." + LifeBoxContract.Entries._ID + "," +
				LifeBoxContract.Entries.TABLE_NAME + "." + LifeBoxContract.Entries.COLUMN_NAME_MEDIA_ID + "," +
				LifeBoxContract.Entries.TABLE_NAME + "." + LifeBoxContract.Entries.COLUMN_NAME_TYPES_ID + "," +
				LifeBoxContract.Entries.TABLE_NAME + "." + LifeBoxContract.Entries.COlUMN_NAME_TITLE + "," +
				LifeBoxContract.Entries.TABLE_NAME + "." + LifeBoxContract.Entries.COlUMN_NAME_DESCRIPTION + "," +
				LifeBoxContract.Entries.TABLE_NAME + "." + LifeBoxContract.Entries.COlUMN_NAME_USER_DATE + "," +
				LifeBoxContract.Entries.TABLE_NAME + "." + LifeBoxContract.Entries.COlUMN_NAME_CREATION_DATE + "," +
				LifeBoxContract.Entries.TABLE_NAME + "." + LifeBoxContract.Entries.COlUMN_NAME_MODIFICATION_DATE + "," +
				LifeBoxContract.Types.TABLE_NAME + "." + LifeBoxContract.Types.COLUMN_NAME_TYPE + " FROM " +
				LifeBoxContract.Entries.TABLE_NAME + " LEFT OUTER JOIN " + LifeBoxContract.Types.TABLE_NAME +
				" ON " + LifeBoxContract.Entries.TABLE_NAME + "." + LifeBoxContract.Entries.COLUMN_NAME_TYPES_ID +
				" = " + LifeBoxContract.Types.TABLE_NAME + "." + LifeBoxContract.Types._ID + " WHERE " +
				LifeBoxContract.Entries.TABLE_NAME + "." + LifeBoxContract.Entries._ID + " == " + id + ";";

		Cursor c = db.rawQuery(query, null);

		if(c.moveToFirst())
		{
			long mediaId = c.getLong(c.getColumnIndexOrThrow(LifeBoxContract.Entries.COLUMN_NAME_MEDIA_ID));
			long typesId = c.getLong(c.getColumnIndexOrThrow(LifeBoxContract.Entries.COLUMN_NAME_TYPES_ID));
			String title = c.getString(c.getColumnIndexOrThrow(LifeBoxContract.Entries.COlUMN_NAME_TITLE));

			// could be null
			String description = "";
			if(!c.isNull(c.getColumnIndexOrThrow(LifeBoxContract.Entries.COlUMN_NAME_DESCRIPTION)))
			{
				description = c.getString(c.getColumnIndexOrThrow(LifeBoxContract.Entries.COlUMN_NAME_DESCRIPTION));
			}

			long userDate = c.getLong(c.getColumnIndexOrThrow(LifeBoxContract.Entries.COlUMN_NAME_USER_DATE));

			long creationDate = c.getLong(c.getColumnIndexOrThrow(LifeBoxContract.Entries.COlUMN_NAME_CREATION_DATE));

			// could be null
			long modificationDate = -1;
			if(!c.isNull(c.getColumnIndexOrThrow(LifeBoxContract.Entries.COlUMN_NAME_MODIFICATION_DATE)))
			{
				modificationDate = c.getLong(c.getColumnIndexOrThrow(LifeBoxContract.Entries.COlUMN_NAME_MODIFICATION_DATE));
			}


			String type = c.getString(c.getColumnIndexOrThrow(LifeBoxContract.Types.COLUMN_NAME_TYPE));

			entry = new Entry(mediaId, typesId, title, description, userDate, creationDate, modificationDate);
			entry.setType(type);
		}

		db.close();

		return entry;
	}

	/**
	 * Select a single MediaFile matching a given mediaId.
	 * @param mediaId (String) the _id from files is the condition
	 * @return (MediaFile) the selected MediaFile or null if none matches the condition.
	 */
	public MediaFile selectMediaFile(String mediaId)
	{
		SQLiteDatabase db = getReadableDatabase();

		// the result MediaFile
		MediaFile mediaFile = null;

		/* ~query
		SELECT filetype, path, driveid, url
		FROM (file_offline_files
		INNER JOIN offline_files ON file_offline_files.offline_files_id = offline_files._id
		INNER JOIN filetypes ON offline_files.filetypes_id = filetypes._id) AS offline
		INNER JOIN (file_online_files
		INNER JOIN online_files ON file_online_files.online_files_id = online_files._id) AS online
		ON offline_files_id = online_files_id
		WHERE offline.files_id == 1 AND online.files_id == 1
		 */

		String query = "SELECT " + LifeBoxContract.Filetypes.COLUMN_NAME_FILETYPE + ", " +
				LifeBoxContract.OfflineFiles.COLUMN_NAME_PATH + ", " +
				LifeBoxContract.OnlineFiles.COLUMN_NAME_DRIVEID + ", " +
				LifeBoxContract.OnlineFiles.COLUMN_NAME_URL + " FROM (" + LifeBoxContract.FileOfflineFiles.TABLE_NAME +
				" INNER JOIN " + LifeBoxContract.OfflineFiles.TABLE_NAME + " ON " +
				LifeBoxContract.FileOfflineFiles.TABLE_NAME + "." +
				LifeBoxContract.FileOfflineFiles.COLUMN_NAME_OFFLINE_FILES_ID + " = " +
				LifeBoxContract.OfflineFiles.TABLE_NAME + "." + LifeBoxContract.OfflineFiles._ID +
				" INNER JOIN " + LifeBoxContract.Filetypes.TABLE_NAME + " ON " +
				LifeBoxContract.OfflineFiles.TABLE_NAME + "." + LifeBoxContract.OfflineFiles.COLUMN_NAME_FILETYPES_ID +
				" = " + LifeBoxContract.Filetypes.TABLE_NAME + "." + LifeBoxContract.Filetypes._ID + ")AS offline INNER JOIN (" +
				LifeBoxContract.FileOnlineFiles.TABLE_NAME + " INNER JOIN " +
				LifeBoxContract.OnlineFiles.TABLE_NAME + " ON " + LifeBoxContract.FileOnlineFiles.TABLE_NAME + "." +
				LifeBoxContract.FileOnlineFiles.COLUMN_NAME_ONLINE_FILES_ID + " = " +
				LifeBoxContract.OnlineFiles.TABLE_NAME + "." + LifeBoxContract.OnlineFiles._ID + ")as online ON " +
				LifeBoxContract.FileOfflineFiles.COLUMN_NAME_OFFLINE_FILES_ID + " = " +
				LifeBoxContract.FileOnlineFiles.COLUMN_NAME_ONLINE_FILES_ID + " WHERE offline." +
				LifeBoxContract.FileOfflineFiles.COLUMN_NAME_FILES_ID + " == " + mediaId + " AND online." +
				LifeBoxContract.FileOnlineFiles.COLUMN_NAME_FILES_ID + " == " + mediaId + ";";

		Cursor c = db.rawQuery(query, null);

		String filetype = "";

		String offlinePathFile = "";
		String offlinePathThumbnail = "";

		if(c.moveToFirst())
		{
			String driveIdFile = "";
			String driveIdThumbnail = "";
			String urlFile = "";
			String urlThumbnail = "";

			while(!c.isAfterLast())
			{
				String thisFiletype = c.getString(
						c.getColumnIndexOrThrow(LifeBoxContract.Filetypes.COLUMN_NAME_FILETYPE));

				// check if the current row is a file or a thumbnail
				if(thisFiletype.equals(Constants.MIME_TYPE_IMAGE) || thisFiletype.equals(Constants.MIME_TYPE_VIDEO))
				{
					// file
					filetype = thisFiletype;

					offlinePathFile = c.getString(
							c.getColumnIndexOrThrow(LifeBoxContract.OfflineFiles.COLUMN_NAME_PATH));
					driveIdFile = c.getString(c.getColumnIndexOrThrow(LifeBoxContract.OnlineFiles.COLUMN_NAME_DRIVEID));
					urlFile = c.getString(c.getColumnIndexOrThrow(LifeBoxContract.OnlineFiles.COLUMN_NAME_URL));
				}
				else if(thisFiletype.equals(Constants.MIME_TYPE_IMAGE_THUMB)
						|| thisFiletype.equals(Constants.MIME_TYPE_VIDEO_THUMB))
				{
					// thumbnail
					offlinePathThumbnail = c.getString(
							c.getColumnIndexOrThrow(LifeBoxContract.OfflineFiles.COLUMN_NAME_PATH));
					driveIdThumbnail = c.getString(c.getColumnIndexOrThrow(LifeBoxContract.OnlineFiles.COLUMN_NAME_DRIVEID));
					urlThumbnail = c.getString(c.getColumnIndexOrThrow(LifeBoxContract.OnlineFiles.COLUMN_NAME_URL));
				}

				c.moveToNext();
			}

			// all fields are fetched -> build the file
			mediaFile = new MediaFile
					(
							filetype,
							offlinePathFile,
							offlinePathThumbnail,
							driveIdFile,
							driveIdThumbnail,
							urlFile,
							urlThumbnail
					);
		}

		db.close();

		return mediaFile;
	}

	/**
	 * Select a single Movie matching a given mediaId.
	 * @param mediaId (String) the _id from movies is the condition
	 * @return (Movie) the selected Movie or null if none matches the condition.
	 */
	public Movie selectMovie(String mediaId)
	{
		SQLiteDatabase db = getReadableDatabase();

		// the result Movie
		Movie movie = null;

		/* ~query
		SELECT title, director description, movie_genre, release_date, thumbnail_url
		FROM movies WHERE _id == 1
		 */

		String query = "SELECT " + LifeBoxContract.Movies.COLUMN_NAME_TITLE + ", " +
				LifeBoxContract.Movies.COLUMN_NAME_DIRECTOR + ", " +
				LifeBoxContract.Movies.COLUMN_NAME_DESCRIPTION + ", " +
				LifeBoxContract.Movies.COLUMN_NAME_MOVIE_GENRE + ", " +
				LifeBoxContract.Movies.COLUMN_NAME_RELEASE_DATE + ", " +
				LifeBoxContract.Movies.COLUMN_NAME_THUMBNAIL_URL + " FROM " +
				LifeBoxContract.Movies.TABLE_NAME + " WHERE " + LifeBoxContract.Movies._ID + " == " + mediaId + ";";

		Cursor c = db.rawQuery(query, null);

		if(c.moveToFirst())
		{
			String title = "";
			String director = "";
			String description = "";
			String movieGenre = "";
			String releaseDate = "";
			String thumbnailUrl = "";

			title = c.getString(c.getColumnIndexOrThrow(LifeBoxContract.Movies.COLUMN_NAME_TITLE));
			director = c.getString(c.getColumnIndexOrThrow(LifeBoxContract.Movies.COLUMN_NAME_DIRECTOR));

			// could be null
			if(!c.isNull(c.getColumnIndexOrThrow(LifeBoxContract.Movies.COLUMN_NAME_DESCRIPTION)))
			{
				description = c.getString(c.getColumnIndexOrThrow(LifeBoxContract.Movies.COLUMN_NAME_DESCRIPTION));
			}

			// could be null
			if(!c.isNull(c.getColumnIndexOrThrow(LifeBoxContract.Movies.COLUMN_NAME_MOVIE_GENRE)))
			{
				movieGenre = c.getString(c.getColumnIndexOrThrow(LifeBoxContract.Movies.COLUMN_NAME_MOVIE_GENRE));
			}

			// could be null
			if(!c.isNull(c.getColumnIndexOrThrow(LifeBoxContract.Movies.COLUMN_NAME_RELEASE_DATE)))
			{
				releaseDate = String.valueOf(c.getString(c.getColumnIndexOrThrow(LifeBoxContract.Movies.COLUMN_NAME_RELEASE_DATE)));
			}

			// could be null
			if(!c.isNull(c.getColumnIndexOrThrow(LifeBoxContract.Movies.COLUMN_NAME_THUMBNAIL_URL)))
			{
				thumbnailUrl = c.getString(c.getColumnIndexOrThrow(LifeBoxContract.Movies.COLUMN_NAME_THUMBNAIL_URL));
			}

			movie = new Movie(title, description, director, movieGenre, releaseDate, thumbnailUrl);
		}

		db.close();

		return movie;
	}

	/**
	 * Select a single Music matching a given mediaId.
	 * @param mediaId (String) the _id from Music is the condition
	 * @return (Music) the selected Music or null if none matches the condition.
	 */
	public Music selectMusic(String mediaId)
	{
		SQLiteDatabase db = getReadableDatabase();

		// the result Music
		Music music = null;

		/* ~query
		SELECT track, artist, album, release_date, thumbnail_url, music_genre
		FROM music
		INNER JOIN albums ON albums_id = albums._id
		INNER JOIN artists ON artists_id = artists._id
		INNER JOIN music_genres ON music._id = music_genres.music_id
		WHERE music._id = 1
		 */

		String query = "SELECT " + LifeBoxContract.Music.COLUMN_NAME_TRACK + ", " +
				LifeBoxContract.Artists.COLUMN_NAME_ARTIST + ", " +
				LifeBoxContract.Albums.COLUMN_NAME_ALBUM + ", " +
				LifeBoxContract.Albums.COLUMN_NAME_RELEASE_DATE + ", " +
				LifeBoxContract.Albums.COLUMN_NAME_THUMBNAIL_URL + ", " +
				LifeBoxContract.MusicGenres.COLUMN_NAME_MUSIC_GENRE + " FROM " +
				LifeBoxContract.Music.TABLE_NAME + " INNER JOIN " + LifeBoxContract.Albums.TABLE_NAME + " ON " +
				LifeBoxContract.Music.COLUMN_NAME_ALBUMS_ID + " = " +  LifeBoxContract.Albums.TABLE_NAME + "." +
				LifeBoxContract.Albums._ID + " INNER JOIN " + LifeBoxContract.Artists.TABLE_NAME + " ON " +
				LifeBoxContract.Music.COLUMN_NAME_ARTISTS_ID + " = " + LifeBoxContract.Artists.TABLE_NAME + "." +
				LifeBoxContract.Artists._ID + " INNER JOIN " + LifeBoxContract.MusicGenres.TABLE_NAME + " ON " +
				LifeBoxContract.Music.TABLE_NAME + "." + LifeBoxContract.Music._ID + " = " +
				LifeBoxContract.MusicGenres.TABLE_NAME + "." + LifeBoxContract.MusicGenres.COLUMN_NAME_MUSIC_ID +
				" WHERE " + LifeBoxContract.Music.TABLE_NAME + "." + LifeBoxContract.Music._ID + " = " + mediaId + ";";

		Cursor c = db.rawQuery(query, null);

		if(c.moveToFirst())
		{
			String track = "";
			String artist = "";
			String album = "";
			String releaseDate = "";
			String thumbnailUrl = "";
			String musicGenre = "";

			track = c.getString(c.getColumnIndexOrThrow(LifeBoxContract.Music.COLUMN_NAME_TRACK));
			artist = c.getString(c.getColumnIndexOrThrow(LifeBoxContract.Artists.COLUMN_NAME_ARTIST));
			album = c.getString(c.getColumnIndexOrThrow(LifeBoxContract.Albums.COLUMN_NAME_ALBUM));

			// could be null
			if(!c.isNull(c.getColumnIndexOrThrow(LifeBoxContract.Albums.COLUMN_NAME_RELEASE_DATE)))
			{
				releaseDate = String.valueOf(c.getString(c.getColumnIndexOrThrow(LifeBoxContract.Albums.COLUMN_NAME_RELEASE_DATE)));
			}

			// could be null
			if(!c.isNull(c.getColumnIndexOrThrow(LifeBoxContract.Albums.COLUMN_NAME_THUMBNAIL_URL)))
			{
				thumbnailUrl = c.getString(c.getColumnIndexOrThrow(LifeBoxContract.Albums.COLUMN_NAME_THUMBNAIL_URL));
			}

			musicGenre = c.getString(c.getColumnIndexOrThrow(LifeBoxContract.MusicGenres.COLUMN_NAME_MUSIC_GENRE));

			music = new Music(artist, album, releaseDate, thumbnailUrl, track, musicGenre);
		}

		db.close();

		return music;
	}

	/**
	 * Select all tags related to a given entry_id
 	 * @param id the entry_id is the condition
	 * @return (ArrayList<String>) List of all tags related to a given entry_id (can have length == 0)
	 */
	public ArrayList<String> selectTags(String id)
	{
		// result List
		ArrayList<String> tagList = new ArrayList<String>();

		SQLiteDatabase db = getReadableDatabase();

		/* ~query
		SELECT tag FROM entry_tags LEFT OUTER JOIN tags ON entry_tags.tags_id = tags._id
		WHERE entry_tags.entries_id == 13
		 */
		String query = "SELECT " + LifeBoxContract.Tags.COLUMN_NAME_TAG + " FROM " +
				LifeBoxContract.EntryTags.TABLE_NAME + " LEFT OUTER JOIN " + LifeBoxContract.Tags.TABLE_NAME + " ON " +
				LifeBoxContract.EntryTags.TABLE_NAME + "." + LifeBoxContract.EntryTags.COLUMN_NAME_TAGS_ID + " = " +
				LifeBoxContract.Tags.TABLE_NAME + "." + LifeBoxContract.Tags._ID + " WHERE " +
				LifeBoxContract.EntryTags.TABLE_NAME + "." + LifeBoxContract.EntryTags.COLUMN_NAME_ENTRIES_ID +
				" == " + id + ";";

		Cursor c = db.rawQuery(query, null);

		if(c.moveToFirst())
		{
			while(!c.isAfterLast())
			{
				tagList.add(c.getString(c.getColumnIndexOrThrow(LifeBoxContract.Tags.COLUMN_NAME_TAG)));

				c.moveToNext();
			}
		}

		c.close();
		db.close();

		return tagList;
	}

	/**
	 * Select all hashtags related to a given entry_id
	 * @param id the entry_id is the condition
	 * @return (ArrayList<String>) List of all hashtags related to a given entry_id (can have length == 0)
	 */
	public ArrayList<String> selectHashtags(String id)
	{
		// result List
		ArrayList<String> hashtagList = new ArrayList<String>();

		SQLiteDatabase db = getReadableDatabase();

		/* ~query
		SELECT hashtag FROM entry_hashtags LEFT OUTER JOIN hashtags ON entry_hashtags.hashtagstags_id = hashtags._id
		WHERE entry_hashtagstags.entries_id == 13
		 */
		String query = "SELECT " + LifeBoxContract.Hashtags.COLUMN_NAME_HASHTAG + " FROM " +
				LifeBoxContract.EntryHashtags.TABLE_NAME + " LEFT OUTER JOIN " + LifeBoxContract.Hashtags.TABLE_NAME +
				" ON " + LifeBoxContract.EntryHashtags.TABLE_NAME + "." +
				LifeBoxContract.EntryHashtags.COLUMN_NAME_HASHTAGS_ID + " = " +	LifeBoxContract.Hashtags.TABLE_NAME +
				"." + LifeBoxContract.Hashtags._ID + " WHERE " + LifeBoxContract.EntryHashtags.TABLE_NAME + "." +
				LifeBoxContract.EntryHashtags.COLUMN_NAME_ENTRIES_ID + " == " + id + ";";

		Cursor c = db.rawQuery(query, null);

		if(c.moveToFirst())
		{
			while(!c.isAfterLast())
			{
				hashtagList.add(c.getString(c.getColumnIndexOrThrow(LifeBoxContract.Hashtags.COLUMN_NAME_HASHTAG)));

				c.moveToNext();
			}
		}

		db.close();

		return hashtagList;
	}


	/**
	 * Select a list of entries._id(s) matchiing a set of conditions.
	 * @param title (String) entries.title
	 * @param fromDate (String) beginning of the timeinterval
	 * @param toDate (String) end of the timeinterval
	 * @param tagList (ArrayList<String>) list of tags
	 * @param hashtagList (ArrayList<String>) list of hashtags
	 * @param mediatypeList (ArrayList<String>) list of mediatypes
	 * @return (ArrayList<String>) a list of entries._id(s) matching the parameter conditions
	 */
	public ArrayList<String> selectFilteredEntryList(String title, String fromDate, String toDate,
													 ArrayList<String> tagList, ArrayList<String> hashtagList,
													 ArrayList<String> mediatypeList)
	{
		// the result
		ArrayList<String> entryIdList = new ArrayList<String>();

		SQLiteDatabase db = getReadableDatabase();

		String namedQueryeFullEntries =
				"CREATE VIEW IF NOT EXISTS fullentries AS " +
				"SELECT e._id, e.media_id, e.type, e.title, e.user_date, et.tag, eh.hashtag " +
				"FROM (entries e " +
				"INNER JOIN types ty ON ty._id = e.types_id) AS e " +
				"LEFT OUTER JOIN (entry_tags et " +
				"INNER JOIN tags ta ON ta._id = et.tags_id) AS et " +
				"ON e._id = et.entries_id " +
				"INNER JOIN tags " +
				"LEFT OUTER JOIN (entry_hashtags eh " +
				"INNER JOIN hashtags ht ON ht._id = eh.hashtags_id) AS eh " +
				"ON e._id = eh.entries_id";

		// the WHERE clause
		String whereClause = " WHERE ";

		// title (optional)
		if(!title.equals(""))
		{
			whereClause += "fe." + LifeBoxContract.Entries.COlUMN_NAME_TITLE + " == '" + title + "' AND ";
		}

		// time interval
		/* ~dateclause
		datetime(user_date, 'unixepoch') BETWEEN datetime(1376376840000, 'unixepoch') AND datetime(1376390220000, 'unixepoch')
		 */
		whereClause += "fe." + LifeBoxContract.Entries.COlUMN_NAME_USER_DATE +
				" BETWEEN " + fromDate + " AND " + toDate;

		// tags (optional)
		// get the amount of tags to place the right amount of 'ORs'
		int tagAmount = tagList.size();

		if(tagAmount > 0)
		{
			int i = 1;

			whereClause += " AND (";

			for(String tag : tagList)
			{

				whereClause += "fe." + LifeBoxContract.Tags.COLUMN_NAME_TAG + " == '" + tag + "'";
				// the amount of separators are n-1
				if(i < tagAmount)
				{
					whereClause += " OR ";
				}
				else if(i == tagAmount)
				{
					whereClause += ") ";
				}

				i++;
			}
		}

		// hashtags (optional)
		// get the amount of hashtags to place the right amount of 'ORs'
		int hashtagAmount = hashtagList.size();

		if(hashtagAmount > 0)
		{
			whereClause += " AND (";

			int j = 1;

			for(String hashtag : hashtagList)
			{

				whereClause += "fe." + LifeBoxContract.Hashtags.COLUMN_NAME_HASHTAG + " == '" + hashtag + "'";
				// the amount of separators are n-1
				if(j < hashtagAmount)
				{
					whereClause += " OR ";
				}
				else if(j == hashtagAmount)
				{
					whereClause += ") ";
				}

				j++;
			}
		}

		// mediatypes (optional)
		// get the amount of mediatypes to place the right amount of 'ORs'
		int mediatypesAmount = mediatypeList.size();

		if(mediatypesAmount > 0)
		{
			whereClause += " AND ";

			int k = 1;

			for(String mediatype : mediatypeList)
			{
				// mediatype file needs distinction in image or video
				if(mediatype.equals(Constants.TYPE_IMAGE_FILE))
				{
					whereClause +=
							"fe." + LifeBoxContract.Types.COLUMN_NAME_TYPE + " == '" + Constants.TYPE_FILE + "'";
					whereClause +=
							" AND f." + LifeBoxContract.Filetypes.COLUMN_NAME_FILETYPE + " == '" + mediatype + "'";
				}
				else if(mediatype.equals(Constants.TYPE_VIDEO_FILE))
				{
					whereClause +=
							"fe." + LifeBoxContract.Types.COLUMN_NAME_TYPE + " == '" + Constants.TYPE_FILE + "'";
					whereClause +=
							" AND f." + LifeBoxContract.Filetypes.COLUMN_NAME_FILETYPE + " == '" + mediatype + "'";
				}
				else
				{
					whereClause += "fe." + LifeBoxContract.Types.COLUMN_NAME_TYPE + " == '" + mediatype + "'";
				}

				// the amount of separators are n-1
				if(k < mediatypesAmount)
				{
					whereClause += " OR ";
				}

				k++;
			}
		}

		// remove duplicated rows
		whereClause += " GROUP BY fe._id;";

		// create the view if not exists
		db.execSQL(namedQueryeFullEntries);

		String baseQuery =
				"SELECT fe._id, fe.media_id, fe.type, fe.title, fe.user_date, fe.tag, fe.hashtag, f.filetype " +
				"FROM fullentries fe " +
				"LEFT OUTER JOIN (files f " +
				"INNER JOIN filetypes ft ON ft._id = f.filetypes_id) AS f " +
				"ON f._id = fe.media_id";

		Log.d(TAG, "query: "+whereClause);

		// select
		String query = baseQuery + whereClause;

		Log.d(TAG, "fullquery: "+query);

		Cursor c = db.rawQuery(query, null);

		// results?
		if(c.moveToFirst())
		{
			while(!c.isAfterLast())
			{
				entryIdList.add(String.valueOf(c.getLong(c.getColumnIndexOrThrow(LifeBoxContract.Entries._ID))));

				c.moveToNext();
			}
		}

		db.close();

		return entryIdList;
	}

	/**
	 * Queries the entries table of the database in order to retrieve
	 * _id, media_id, types_id, title, description and user_date,
	 * which is the intersection of all timelineEntries.
	 * @param whereClause (String) a condition including 'WHERE'
	 * @param sortOrder (String) the sort order (DESC or ASC)
	 * @param limit (int) the amount of rows, the function should return.
	 * @param offset (int) the offset
	 * @return a 2d array with the media_ids and the types_ids.
	 */
	public String[][] selectEntrySet(String whereClause, String sortOrder, int limit, int offset)
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
				" = " + LifeBoxContract.Types.TABLE_NAME + "." + LifeBoxContract.Types._ID;

		// add the where clause if exists
		if(null != whereClause && !whereClause.trim().equals(""))
		{
			query += " " + whereClause;
		}

		// add the limit and offset
		query +=
				" ORDER BY " +
				LifeBoxContract.Entries.TABLE_NAME + "." + LifeBoxContract.Entries.COlUMN_NAME_USER_DATE +
				" " + sortOrder + " LIMIT " + limit + " OFFSET " + offset + ";";

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

		String s = columns[0] + " == " + selection;

		// fetch the row
		Cursor c = db.query
				(
						table,
						columns,
						s,
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

		c.close();
		db.close();

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
	public Map<String, String> selectMusicMap(String musicId)
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
	public Map<String, String> selectMovieMap(String movieId)
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
