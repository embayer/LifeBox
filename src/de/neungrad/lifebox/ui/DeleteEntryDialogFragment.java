package de.neungrad.lifebox.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import de.neungrad.lifebox.R;
/**
 * Dialog fragment to let the user confirm the deletion of a entry
 * @version 0.1 08.08.13
 * @autor Markus Bayer
 */
public class DeleteEntryDialogFragment extends DialogFragment
{
	/* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
	public interface NoticeDialogListener
	{
		public void onDialogPositiveClick(DialogFragment dialog);
		public void onDialogNegativeClick(DialogFragment dialog);
	}

	// instance of the interface to deliver action events
	NoticeDialogListener mListener;

	// Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		// verify that the host activity implements the callback interface
		try
		{
			// Instantiate the NoticeDialogListener so we can send events to the host
			mListener = (NoticeDialogListener) activity;
		}
		catch (ClassCastException e)
		{
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement NoticeDialogListener");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(R.string.dialog_delete_confirmation)
				.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						mListener.onDialogPositiveClick(DeleteEntryDialogFragment.this);
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						mListener.onDialogNegativeClick(DeleteEntryDialogFragment.this);
					}
				});

		// Create the AlertDialog object and return it
		return builder.create();
	}
}