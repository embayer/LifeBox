package de.lifebox.LifeBox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Activity that provides a form for text entries.
 * @version 0.1 15.07.13
 * @autor Markus Bayer
 */
public class TextFormActivity extends Activity
{
	// the ui elements
	private EditText inputText;
	private Button saveButton;

	// the button listener
	Button.OnClickListener mSaveListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent(getBaseContext(), MetaFormActivity.class);

			// get the text
			String text = inputText.getText().toString();

			if(null != text && !text.trim().equals(""))
			{
				// set the extra
				intent.putExtra(Constants.MEDIA_TYPE_EXTRA, Constants.TYPE_TEXT);
				intent.putExtra(Constants.TEXT_EXTRA, text);

				startActivity(intent);
			}
		}
	};

	/** Called when the Activity is first created. */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.textform);

		// initialize the ui elements, set the listener
		inputText = (EditText) findViewById(R.id.in_text);
		inputText.requestFocus();
		saveButton = (Button) findViewById(R.id.save_text);
		saveButton.setOnClickListener(mSaveListener);
	}
}