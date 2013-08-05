package de.lifebox.LifeBox;

import java.io.Serializable;

/**
 * Wrapper class to hold a 2d-long array.
 * The class is a singelton
 * @version 0.1 31.07.13
 * @autor Markus Bayer
 */
public class EntryList implements Serializable
{
	private static final long serialVerionUID = 1L;
	private long[][] longList;
	private static EntryList singeltonObject;

	/** Private empty constructor to avoid regular instances. */
	private EntryList() {}

	/** ~Constructor */
	public static EntryList getSingeltonObject()
	{
		if(null == singeltonObject)
		{
			singeltonObject = new EntryList();
		}

		return singeltonObject;
	}

	public void setLong(long[][] longList)
	{
		this.longList = longList;
	}

	public long[][] getLong()
	{
		return longList;
	}
}
