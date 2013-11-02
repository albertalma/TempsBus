package com.almacorp.paradesihoraris.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.almacorp.paradesihoraris.R;

public class ListAdapter extends ArrayAdapter<String> {
	
	private Context context;
	private List<String> names;
	private List<String> codes;

	public ListAdapter(Context context, List<String> names,
			List<String> codes) {
		super(context,R.layout.list_element,names);
		this.context = context;
		this.names = names;
		this.codes = codes;
	}
	
	public String getItemCode(int position) {
		return codes.get(position);
	}
	
	public String getItemName(int position) {
		return names.get(position);
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.list_element, parent, false);
		TextView name = (TextView) rowView.findViewById(R.id.stop_name);
		TextView code = (TextView) rowView.findViewById(R.id.stop_code);
		name.setText(names.get(position));
		code.setText(codes.get(position));
		return rowView;
	}

}
