package de.lifebox.LifeBox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Activity for handling connection problems.
 * @author Markus Bayer
 * @version 0.1 07.06.2013
 */
public class NoConnectionActivity extends Activity
{
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.noconnection);
	}

	//TODO replace with button listener
	public void startSignIn(View view)
	{
		Intent intent = new Intent(this, SignInActivity.class);
		startActivity(intent);
	}
}