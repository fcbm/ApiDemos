package com.example.timetracker;

import java.util.List;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class TimeListAdapter extends ArrayAdapter<Long> 
								implements OnLongClickListener {

	

	public TimeListAdapter(Context context, int resource, List<Long> objects) {
		super(context, resource, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = convertView;
		
		if (view == null)
		{
			// TODO: 
			// - why parent parameter is set to null ?
			// - why call from() ?
			view = LayoutInflater.from(getContext()).inflate(R.layout.time_row, null);
			view.setOnLongClickListener( this );
		}
		
		long time = getItem(position);
		
		String taskString = getContext().getResources().getString(R.string.task_name);
		
		TextView name = (TextView) view.findViewById(R.id.lap_name);
		name.setText(String.format(taskString, position + 1));
		
		TextView lapTime = (TextView) view.findViewById(R.id.lap_time);
		lapTime.setText( DateUtils.formatElapsedTime( time) );
		
		return view;
	}
	
	@Override
	public boolean onLongClick(View v)
	{
		Toast.makeText( getContext(), "onLongClick", Toast.LENGTH_LONG).show();
		return false;
	}

}
