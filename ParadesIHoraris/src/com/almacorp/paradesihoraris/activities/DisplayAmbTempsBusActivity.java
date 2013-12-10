package com.almacorp.paradesihoraris.activities;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.almacorp.paradesihoraris.R;
import com.almacorp.paradesihoraris.adapters.BusAdapter;
import com.almacorp.paradesihoraris.dialogs.LoadDialogFragment;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class DisplayAmbTempsBusActivity extends ActionBarActivity {

	static private int NETWORKERROR = 0;
	static private int ERROR = 1;

	private LoadDialogFragment progressBar;

	private String body;
	private String codi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.webview);
		progressBar = new LoadDialogFragment(this);
		progressBar.show();
		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			// handles a search query
			String query = intent.getStringExtra(SearchManager.QUERY);
			codi = query;
			Log.d("Search", "Search code: " + query);
			makePost();
		} else {
			codi = intent.getStringExtra("codi");
		}
		makePost();

		// loadWebView();
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	private void makePost() {
		if (!isOnline()) {
			progressBar.dismiss();
			showErrorMessage(NETWORKERROR);
		} else {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("codi", codi));
			new ConnectionTask().execute(nameValuePairs);
		}
	}

	private void showErrorMessage(int idError) {
		if (idError == NETWORKERROR)
			setContentView(R.layout.network_error);
		else if (idError == ERROR)
			setContentView(R.layout.error);
	}

	private String TAG = "DATBA";

	private class ConnectionTask extends
			AsyncTask<List<NameValuePair>, Void, String> {

		HttpResponse response;

		@Override
		protected String doInBackground(List<NameValuePair>... nameValuePairs) {
			String exception = "";
			String url = getString(R.string.url_bus);
			List<NameValuePair> nvPairs = nameValuePairs[0];
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			try {
				httppost.setEntity(new UrlEncodedFormEntity(nvPairs));
				// Execute HTTP Post Request
				response = httpclient.execute(httppost);
				Log.i("postData", response.getStatusLine().toString());
				HttpEntity entity = response.getEntity();
				body = EntityUtils.toString(entity);
			} catch (UnsupportedEncodingException e) {
				exception = e.getMessage();
			} catch (ClientProtocolException e) {
				exception = e.getMessage();
			} catch (IOException e) {
				exception = e.getMessage();
			}
			return exception;
		}

		protected void onPostExecute(String exception) {
			progressBar.dismiss();
			if (!exception.equals("")) {
				int duration = Toast.LENGTH_SHORT;
				Context context = getApplicationContext();
				Toast toast = Toast.makeText(context, exception, duration);
				toast.show();
				Log.e(TAG, exception);
			} else
				showStop();
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

	public void showStop() {
		Log.d(TAG, "showStop");
		Log.i(TAG, body);
		setContentView(R.layout.activity_display_parades);
		//loadAd();
		Document doc = Jsoup.parse(body);
		putTitle(doc);
		putStops(doc);
	}

	private void putStops(Document doc) {
		Elements stops = doc.select("#linia_amb,#linia_tmb");
		if (stops.size() <= 0)
			showErrorMessage(ERROR);
		else {
			List<String> names = new ArrayList<String>();
			List<String> times = new ArrayList<String>();
			for (Element stop : stops) {
				Log.i(TAG, "Bus name: " + stop.text());
				names.add(stop.text());
				String time = stop.parent()
						.select("div:nth-child(2) b span:nth-child(1)").text();
				Log.i(TAG, "Time: " + time);
				if (time.equals("imminent"))
					time = getResources().getString(R.string.imminent);
				times.add(time);
			}
			BusAdapter adapter = new BusAdapter(this, names, times);
			PullToRefreshListView listView = (PullToRefreshListView) findViewById(R.id.listView_bus);
			listView.setAdapter(adapter);
			onRefreshListener(listView);
		}
	}

	private void onRefreshListener(PullToRefreshListView listView) {
		listView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// Do work to refresh the list here.
				makePost();
			}

		});

	}

	private void putTitle(Document doc) {
		Elements getName = doc.select("[data-role=list-divider]");
		for (Element aux : getName) {
			Elements e = aux.select("div span");
			TextView stopName = (TextView) findViewById(R.id.stop_name);
			stopName.setText(e.get(1).text());
		}
		TextView stopId = (TextView) findViewById(R.id.stop_id);
		String text = getResources().getString(R.string.stopNumber);
		stopId.setText(text + " " + codi);
	}

	// Called when the user clicks on Save Button
	public void saveStop(View view) {
		Intent addStop = new Intent(this, AddCodeActivity.class);
		addStop.putExtra("codi", codi);
		startActivity(addStop);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.display_activity_actions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "click actionbar: " + item.getItemId());
		switch (item.getItemId()) {
		case R.id.action_search:
			onSearchRequested();
			return true;
		case R.id.action_refresh:
			makePost();
		default:
			return false;
		}
	}
}
