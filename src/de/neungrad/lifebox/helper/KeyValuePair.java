package de.neungrad.lifebox.helper;

/**
 * Wrapper class for key stringValue pairs.
 * Used for database inserts, where column=key, insertfield=stringValue
 * @version 0.1 23.07.13
 * @autor Markus Bayer
 */
public class KeyValuePair
{
	private String key;
	private String stringValue;
	private long longValue;

	// indicates what datatype the value is of
	private boolean valueIsString;

	/** Constructor. */
	public KeyValuePair(String key, String value)
	{
		this.key = key;
		this.stringValue = value;

		valueIsString = true;
	}

	/** Constructor. */
	public KeyValuePair(String key, long value)
	{
		this.key = key;
		this.longValue = value;

		valueIsString = false;
	}

	public String getKey()
	{
		return key;
	}

	public String getStringValue()
	{
		return stringValue;
	}

	public long getLongValue()
	{
		return longValue;
	}

	public boolean getValueIsString()
	{
		return valueIsString;
	}
}
