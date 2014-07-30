package de.neungrad.lifebox.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import de.neungrad.lifebox.R;

/**
 * Activity for handling connection problems.
 * @author Markus Bayer
 * @version 0.1 07.06.2013
 */
public class NoConnectionActivity extends Activity
{
	// button listener retry
	Button.OnClickListener mRetryListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			// check connectivity
			if(isOnline())
			{
				// signin per Google+ Sign-In
				startSignIn();
			}
			else
			{
				// inform the user about the missing connection
				Toast.makeText(getBaseContext(), "Check your internet connection.", Toast.LENGTH_SHORT).show();
			}
		}
	};

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.noconnection);

		// init the retry button, set the listener
		Button retryBtn = (Button) findViewById(R.id.retry);
		retryBtn.setOnClickListener(mRetryListener);
	}

	/**
	 * Start an intent to signin to Google+ Sign-In
	 */
	public void startSignIn()
	{
		Intent intent = new Intent(this, SignInActivity.class);

		// clear backstack navigation
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	/**
	 * Check if the device has an ethernet connection.
	 * @return true if there is a connection else return false
	 */
	public boolean isOnline()
	{
		ConnectivityManager connMgr = (ConnectivityManager)
				getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		if(null != networkInfo && networkInfo.isConnected())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}