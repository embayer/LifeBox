package de.lifebox.LifeBox;

import android.provider.BaseColumns;

/**
 * Database description class
 * @version 0.1 25.06.13
 * @autor Markus Bayer
 */
public final class LifeBoxContract
{
//	private static final String TEXT_TYPE = " TEXT";
//	private static final String COMMA_SEP = ",";
//
//	private static final String SQL_CREATE_HASHTAGS =
//			"CREATE TABLE " + Hashtags.TABLE_NAME + " (" +
//					Hashtags._ID + " INTEGER PRIMARY KEY," +
//					Hashtags.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
//					Hashtags.COLUMN_NAME_HASHTAG + TEXT_TYPE + COMMA_SEP +
//					" )";
//
//	private static final String SQL_DELETE_HASHTAGS =
//			"DROP TABLE IF EXISTS " + LifeBoxContract.Hashtags.TABLE_NAME;

	/** Constructor is empty to prevent instantiating the Contract class. */
	public LifeBoxContract() {}

	/** Inner class that defines the table contents */
	public static abstract class Hashtags implements BaseColumns
	{
		public static final String TABLE_NAME = "hashtags";
		public static final String COLUMN_NAME_HASHTAG = "hashtag";
	}
}
