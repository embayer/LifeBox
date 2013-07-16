package de.lifebox.LifeBox;

import android.app.Activity;
import android.util.JsonReader;

import java.io.IOException;
import java.util.ArrayList;

/**
 * description
 *
 * @version 0.1 12.07.13
 * @autor Markus Bayer
 */
abstract class AbstractSearchMedia extends Activity
{
	abstract ArrayList<?> parseMediaJsonString(String json);
	abstract ArrayList<?> parseMediaArray(JsonReader reader);
	abstract ArrayList<?> parseMedia(JsonReader reader);
}
