package com.almacorp.paradesihoraris.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;

import com.almacorp.paradesihoraris.R;

public class LoadDialogFragment extends Dialog {

	public LoadDialogFragment(Activity a) {
		super(a);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.loading_dialog);
	}
}