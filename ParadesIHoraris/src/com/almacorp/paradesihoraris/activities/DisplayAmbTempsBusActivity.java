package com.almacorp.paradesihoraris.activities;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.almacorp.paradesihoraris.R;
import com.almacorp.paradesihoraris.dialogs.LoadDialogFragment;
import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class DisplayAmbTempsBusActivity extends ActionBarActivity {

	private LoadDialogFragment progressBar;

	private String body;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.webview);
		progressBar = new LoadDialogFragment(this);
		progressBar.show();
		loadAd();
		makePost();
		// loadWebView();
	}

	private void makePost() {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		Log.i(TAG, "adterValue");
		Intent intent = getIntent();
		String codi = intent.getStringExtra("codi");
		Log.i(TAG, "afterGetString");
		nameValuePairs.add(new BasicNameValuePair("codi", codi));
		new ConnectionTask().execute(nameValuePairs);

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
			if (body != null)
				Log.i(TAG, "Body: " + body);
			return exception;
		}

		protected void onPostExecute(String exception) {
			if (!exception.equals("")) {
				Log.e(TAG, exception);
			}
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

	private void loadWebView() {
		Intent intent = getIntent();
		int codi = Integer.parseInt(intent.getStringExtra("codi"));
		String url = getString(R.string.url_temps_bus, codi);
		Log.e("DATBA", url);
		WebView myWebView = (WebView) findViewById(R.id.ambTempsBus);
		WebSettings webSettings = myWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		myWebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			public void onPageFinished(WebView view, String url) {
				if (progressBar.isShowing()) {
					progressBar.dismiss();
				}
			}
		});
		myWebView.loadUrl(url);
	}
}
