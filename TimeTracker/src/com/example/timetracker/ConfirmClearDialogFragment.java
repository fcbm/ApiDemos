package com.example.timetracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;


public class ConfirmClearDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

	private static final String TAG = "ConfirmClearDialogFragment";
	private static final String KEY_NUMBER_OF_ITEMS = "num_of_items"; 
	private TimeListAdapter mAdapter;
	
	static public ConfirmClearDialogFragment newInstance(TimeListAdapter adapter)
	{
		
		
		ConfirmClearDialogFragment dialog = new ConfirmClearDialogFragment();
		dialog.mAdapter = adapter;
		
		Bundle args = new Bundle();
		args.putInt(KEY_NUMBER_OF_ITEMS, dialog.mAdapter.getCount());
		
		dialog.setArguments(args);
		
		return dialog;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		String aMessage = getResources().getString(R.string.confirm_clear_all_message);

		// NOTE : We're using getArguments() NOT savedInstanceState
		int aCount = getArguments().getInt(KEY_NUMBER_OF_ITEMS);
		
		return new AlertDialog.Builder(getActivity()).
				setTitle(R.string.confirm_clear_all_title).
				setMessage(String.format(aMessage, aCount)).
				setPositiveButton(R.string.ok, this).
				setNegativeButton(R.string.cancel, this).
				create();
		
	}
	
	
/*
	// NOTE: onCreateView is used when you want to supply a custom layout

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog, container, false);
        View tv = v.findViewById(R.id.text);
        ((TextView)tv).setText("Dialog #" + mNum + ": using style "
                + getNameForNum(mNum));

        // Watch for button clicks.
        Button button = (Button)v.findViewById(R.id.show);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
                ((FragmentDialog)getActivity()).showDialog();
            }
        });

        return v;
    }
 */

	@Override
	public void onClick(DialogInterface dialog, int which) {
		
		if (which == DialogInterface.BUTTON_POSITIVE)
		{
			mAdapter.clear();
			dialog.dismiss();
		}
		else if (which == DialogInterface.BUTTON_NEGATIVE)
		{
			dialog.dismiss();
		}
		
	}
}
