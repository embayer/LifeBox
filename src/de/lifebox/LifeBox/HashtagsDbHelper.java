package de.lifebox.LifeBox;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database Helper class
 * @version 0.1 25.06.13
 * @autor Markus Bayer
 */
public class HashtagsDbHelper extends SQLiteOpenHelper
{
	// If you change the database schema, you must increment the database version.
	public static final int DATABASE_VERSION = 2;
	public static final String DATABASE_NAME = "LifeBox.db";

	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ", ";

	// SQL Strings
	private static final String SQL_CREATE_HASHTAGS =
			"CREATE TABLE " + LifeBoxContract.Hashtags.TABLE_NAME + " (" +
					LifeBoxContract.Hashtags._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
					LifeBoxContract.Hashtags.COLUMN_NAME_HASHTAG + TEXT_TYPE +
					" )";

	private static final String SQL_DELETE_HASHTAGS =
			"DROP TABLE IF EXISTS " + LifeBoxContract.Hashtags.TABLE_NAME;

    public HashtagsDbHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/** Called by the framework if the database does not exist. */
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(SQL_CREATE_HASHTAGS);
	}

	/**
	 * Called when the database version is increased in the application code.
	 * Used to update the database schema.
	 * */
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// This database is only a cache for online data, so its upgrade policy is
		// to simply to discard the data and start over
		db.execSQL(SQL_DELETE_HASHTAGS);
		onCreate(db);
	}

	public void onOpen()
	{

	}

	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		onUpgrade(db, oldVersion, newVersion);
	}
}