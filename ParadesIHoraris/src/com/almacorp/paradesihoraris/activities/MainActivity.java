package com.almacorp.paradesihoraris.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.almacorp.paradesihoraris.R;
import com.almacorp.paradesihoraris.adapters.ListAdapter;
import com.almacorp.paradesihoraris.dialogs.DeleteDialogFragment;
import com.almacorp.paradesihoraris.dialogs.DeleteDialogFragment.DeleteDialogListener;
import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class MainActivity extends ActionBarActivity implements
		DeleteDialogListener {

	private String key;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//loadAd();
		Intent intent = getIntent();
		Log.i("Main", "entro");
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			// handles a search query
			String query = intent.getStringExtra(SearchManager.QUERY);
			sendBusCode(query);
		} else {
			initializeListView();
		}
	}

	private void loadAd() {
		// request TEST ads to avoid being disabled for clicking your own ads
		AdRequest adRequest = new AdRequest();

		// test mode on EMULATOR
		adRequest.addTestDevice(AdRequest.TEST_EMULATOR);

		// test mode on DEVICE (this example code must be replaced with your
		// device uniquq ID)
		adRequest.addTestDevice("F8E8CBBA37CCE7A1DEC480BFEFCE0FED");

		AdView adView = (AdView) findViewById(R.id.adView);

		// Initiate a request to load an ad in test mode.
		// You can keep this even when you release your app on the market,
		// because
		// only emulators and your test device will get test ads. The user will
		// receive real ads.
		adView.loadAd(adRequest);
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		initializeListView();
	}

	private static final String PREFS_NAME = "Bus";
	private ListAdapter adapter;

	private void initializeListView() {
		Log.e("MAIN", "intializeListView");
		ListView listView = (ListView) findViewById(R.id.listView_bus);
		SharedPreferences codeList = getSharedPreferences(PREFS_NAME, 0);
		Map<String, ?> mapBus = codeList.getAll();
		if (!mapBus.isEmpty()) {
			disableEmptyText();
			List<String> names = new ArrayList<String>();
			List<String> codes = new ArrayList<String>();
			for (Map.Entry<String, ?> entry : mapBus.entrySet()) {
				names.add(entry.getKey());
				codes.add(entry.getValue().toString());
			}
			adapter = new ListAdapter(this, names, codes);
			listView.setAdapter(adapter);
		} else {
			showEmptyText();
		}
		setOnClickListener(listView);
		setOnLongClickListener(listView);
	}

	private void disableEmptyText() {
		TextView onPlus = (TextView) findViewById(R.id.clickOnPlus);
		TextView noStop = (TextView) findViewById(R.id.noStop);
		onPlus.setVisibility(View.GONE);
		noStop.setVisibility(View.GONE);
	}

	private void showEmptyText() {
		TextView onPlus = (TextView) findViewById(R.id.clickOnPlus);
		TextView noStop = (TextView) findViewById(R.id.noStop);
		onPlus.setVisibility(View.VISIBLE);
		noStop.setVisibility(View.VISIBLE);
	}

	private void setOnLongClickListener(ListView listView) {
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				key = adapter.getItemName(position);
				Log.e("MAIN", "onLongclickKey: " + key);
				DialogFragment dialog = new DeleteDialogFragment();
				dialog.show(getSupportFragmentManager(), "DeleteDialogFragment");
				return false;
			}
		});

	}

	private void setOnClickListener(final ListView listView) {
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String code = adapter.getItemCode(position);
				sendBusCode(code);
			}
		});

	}

	/** Called when the user clicks the Send button */
	public void sendBusCode(String codi) {
		if (codi.isEmpty() || codi.equals(""))
			showEmptyToast();
		else {
			Intent intent = new Intent(this, DisplayAmbTempsBusActivity.class);
			intent.putExtra("codi", codi);
			startActivity(intent);
		}
	}

	private void showEmptyToast() {
		Context context = getApplicationContext();
		CharSequence text = getResources().getString(R.string.emptyCode);
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_search:
			onSearchRequested();
			return true;
		case R.id.action_new:
			addCode();
		default:
			return false;
		}
	}

	private void addCode() {
		Intent intent = new Intent(this, AddCodeActivity.class);
		startActivity(intent);
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		Log.e("MAIN", "onPositiveClick");
		Log.e("MAIN", "Delete: " + key);
		SharedPreferences codeList = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = codeList.edit();
		editor.remove(key);
		editor.commit();
		adapter.remove(key);
		adapter.notifyDataSetChanged();
		if (adapter.isEmpty())
			showEmptyText();
		else
			disableEmptyText();
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
	}

}
