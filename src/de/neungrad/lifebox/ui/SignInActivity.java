package de.neungrad.lifebox.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;
import de.neungrad.lifebox.R;
import de.neungrad.lifebox.helper.Constants;

/**
 * Activity for Google+ Sign-in.
 * @author Markus Bayer
 * @version 0.1 10.06.2013
 */
public class SignInActivity extends Activity implements View.OnClickListener,
							ConnectionCallbacks, OnConnectionFailedListener
{
	private static final String TAG = "SignInActivity";
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

	private ProgressDialog mConnectionProgressDialog;
	private PlusClient mPlusClient;
	private ConnectionResult mConnectionResult;

	/** Called when the Activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// check if ethernet connection is available
		if(isOnline())
		{
			// initialize the client
			mPlusClient = new PlusClient.Builder(this, this, this)
					.setVisibleActivities("http://schemas.google.com/AddActivity", "http://schemas.google.com/BuyActivity")
					.build();
			// Progress bar to be displayed if the connection failure is not resolved.
			mConnectionProgressDialog = new ProgressDialog(this);
			mConnectionProgressDialog.setMessage("Signing in...");
		}
		else
		{
			// navigate to the noconnection activity
			Intent intent = new Intent(this, NoConnectionActivity.class);

			// clear the backstack navigation
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}


	}

	/** Called when the activity is becoming visible to the user. */
	@Override
	protected void onStart()
	{
		super.onStart();

		// check for connectivity
		if(isOnline())
		{
			mPlusClient.connect();
		}
		else
		{
			// navigate to the noconnection activity
			Intent intent = new Intent(this, NoConnectionActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}

	}

	@Override
	protected void onStop()
	{
		super.onStop();

		// check for connectivity
		if(isOnline())
		{
			mPlusClient.disconnect();
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result)
	{
		if (result.hasResolution())
		{
			try
			{
				result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
			}
			catch (IntentSender.SendIntentException e)
			{
				mPlusClient.connect();
			}
		}

		// Save the result and resolve the connection failure upon a user click.
		mConnectionResult = result;
	}

	/** Called when the activity is no longer visible to the user. */
	@Override
	protected void onActivityResult(int requestCode, int responseCode, Intent intent)
	{
		// hold the connection
		if (requestCode == REQUEST_CODE_RESOLVE_ERR && responseCode == RESULT_OK)
		{
			mConnectionResult = null;
			mPlusClient.connect();
		}
	}

	/** Called when the connection is established. */
	@Override
	public void onConnected(Bundle connectionHint)
	{
		// accountname == gmail email adress
		String account = mPlusClient.getAccountName();

		// notify the user that his account is connected
		Toast.makeText(this, account + " is connected.", Toast.LENGTH_LONG).show();

		// save the users accountname persistent to the sharedpreferences if no exists
		SharedPreferences settings = getSharedPreferences("preferences", 0);

		if(settings.getString("accountName", "").equals(""))
		{
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("accountName", account);
			editor.commit();
		}


		// Logged in successfully, call MainActivity
		Intent callSelectTypeActivity = new Intent(this, MainActivity.class);

		// clear the backstack navigation
		callSelectTypeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		callSelectTypeActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		callSelectTypeActivity.putExtra(Constants.CALLER_EXTRA, Constants.CALLER_SIGN_IN_ACTIVITY);
		startActivity(callSelectTypeActivity);
	}

	/** Called when the connection is destroyed. */
	@Override
	public void onDisconnected()
	{
		Log.d(TAG, "disconnected");
	}

	/** Called when the Google+ sign in button is clicked. */
	@Override
	public void onClick(View view)
	{
		if (view.getId() == R.id.sign_in_button && !mPlusClient.isConnected())
		{
			// when there is no connection established
			if (mConnectionResult == null)
			{
				// show the dialog
				mConnectionProgressDialog.show();
			}
			else
			{
				try
				{
					// connect
					mConnectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
				}
				catch (IntentSender.SendIntentException e)
				{
					// Try connecting again.
					mConnectionResult = null;
					mPlusClient.connect();
				}
			}
		}
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