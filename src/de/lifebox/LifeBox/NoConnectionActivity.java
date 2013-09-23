package de.lifebox.LifeBox;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Activity for handling connection problems.
 * @author Markus Bayer
 * @version 0.1 07.06.2013
 */
public class NoConnectionActivity extends Activity
{
	Button.OnClickListener mRetryListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if(isOnline())
			{
				startSignIn();
			}
			else
			{
				Toast.makeText(getBaseContext(), "Check your internet connection.", Toast.LENGTH_SHORT).show();
			}
		}
	};

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.noconnection);

		Button retryBtn = (Button) findViewById(R.id.retry);
		retryBtn.setOnClickListener(mRetryListener);

//		if(isOnline())
//		{
//			startSignIn();
//		}
	}

	/**
	 * Start an intent to signin to g+
	 */
	public void startSignIn()
	{
		Intent intent = new Intent(this, SignInActivity.class);
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

		if(networkInfo != null && networkInfo.isConnected())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}