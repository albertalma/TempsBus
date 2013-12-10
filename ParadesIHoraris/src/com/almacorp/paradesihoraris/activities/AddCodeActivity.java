package com.almacorp.paradesihoraris.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.almacorp.paradesihoraris.R;

public class AddCodeActivity extends ActionBarActivity {

	private static final String PREFS_NAME = "Bus";
	private static final String TAG = "ADDCODEACTIVITY";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.add_code);
		Intent intent = getIntent();
		String codi = new String();
		if ((codi = intent.getStringExtra("codi")) != null) {
			Log.d(TAG,"codi: " + codi);
			setCode(codi);
		}
	}

	private void setCode(String codi) {
		EditText code = (EditText) findViewById(R.id.text_code);
		code.setText(codi);
	}

	@Override
	protected void onStart() {
		super.onStart();
		EditText code = (EditText) findViewById(R.id.text_code);
		code.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEND)
					saveStop();
				return false;
			}

		});
	}

	/** Called when the user clicks the Save button */
	public void onSave(View view) {
		saveStop();
	}

	private void saveStop() {
		EditText name = (EditText) findViewById(R.id.text_name);
		EditText code = (EditText) findViewById(R.id.text_code);
		String stringName = name.getText().toString();
		String stringCode = code.getText().toString();
		if (stringName.equals("")) showNameToast();
		else if (stringCode.equals("")) showCodeToast();
		else {
			SharedPreferences codeList = getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor editor = codeList.edit();
			editor.putString(stringName, stringCode);
			editor.commit();
			finish();
		}
	}

	private void showNameToast() {
		Context context = getApplicationContext();
		CharSequence text = getResources().getString(R.string.emptyName);
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}
	
	private void showCodeToast() {
		Context context = getApplicationContext();
		CharSequence text = getResources().getString(R.string.emptyCode);
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	/** Called when the user clicks the Cancel button */
	public void onCancel(View view) {
		finish();
	}

}
