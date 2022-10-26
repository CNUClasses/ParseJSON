package com.example.parsejson;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ParseJSONActivity extends AppCompatActivity {
	private static final String TAG = "ParseJSON";
	private static final String MYURL = "https://raw.githubusercontent.com/CNUClasses/475_web_data/master/economist.json";

	public static final int MAX_LINES = 15;
	private static final int SPACES_TO_INDENT_FOR_EACH_LEVEL_OF_NESTING = 2;

	private TextView tvRaw;
	private TextView tvfirstname;
	private TextView tvlastname;
	private Button bleft;
	private Button bright;
	JSONArray jsonArray;

	int numberentries = -1;
	int currententry = -1;

	//persists accross config changes
	DataVM myVM;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_parse_json);

		tvRaw = (TextView) findViewById(R.id.tvRaw);
		tvfirstname = (TextView) findViewById(R.id.tvfirstname);
		tvlastname = (TextView) findViewById(R.id.tvlastname);
		bleft = (Button) findViewById(R.id.bleft);
		bright = (Button) findViewById(R.id.bright);

		// Create a ViewModel the first time the system calls an activity's
		// onCreate() method.  Re-created activities receive the same
		// MyViewModel instance created by the first activity.
		myVM = new ViewModelProvider(this).get(DataVM.class);

		// Create the observer which updates the UI.
		final Observer<String> jsonObserver = new Observer<String>() {
			@Override
			public void onChanged(@Nullable final String jsondata) {
				// Update the UI, in this case, a TextView.
				processJSON(jsondata);

			}
		};
		//now observe
		myVM.getjsondata().observe(this,jsonObserver);
		// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// make sure the network is up before you attempt a connection
		// notify user of problem? Not very good, maybe wait a little while and
		// try later? remember make users life easier
		ConnectivityCheck myCheck = new ConnectivityCheck(this);
		if (myCheck.isNetworkReachable()) {
			//can get to network lets get the json data
			myVM.start_thread(MYURL);
		}
		else
			Toast.makeText(this,"Uh Ohh cannot reach network",Toast.LENGTH_SHORT).show();
	}


	public void setText(String string) {
		tvRaw.setText(string);

		// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// add scrolling to the textbox
		// first restrict lines to number visible (full screen in for this case
		// tvRaw.setMaxLines(tvRaw.getLineCount()))
		tvRaw.setMaxLines(MAX_LINES);
		tvRaw.setMovementMethod(new ScrollingMovementMethod());
	}

	public void processJSON(String string) {
		try {
			JSONObject jsonobject = new JSONObject(string);
			
			//*********************************
			//makes JSON indented, easier to read
			Log.d(TAG,jsonobject.toString(SPACES_TO_INDENT_FOR_EACH_LEVEL_OF_NESTING));
			tvRaw.setText(jsonobject.toString(SPACES_TO_INDENT_FOR_EACH_LEVEL_OF_NESTING));

			// you must know what the data format is, a bit brittle
			jsonArray = jsonobject.getJSONArray("people");

			// how many entries
			numberentries = jsonArray.length();

			currententry = 0;
			setJSONUI(currententry); // parse out object currententry

			Log.i(TAG, "Number of entries " + numberentries);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param i
	 *            find the object i in the member var jsonArray get the
	 *            firstname and lastname and set the appropriate UI elements
	 */
	private void setJSONUI(int i) {
		if (jsonArray == null) {
			Log.e(TAG, "tried to dereference null jsonArray");
			return;
		}

		// gotta wrap JSON in try catches cause it throws an exception if you
		// try to get a value that does not exist
		try {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			tvfirstname.setText(jsonObject.getString("firstname"));
			tvlastname.setText(jsonObject.getString("lastname"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		setButtons();
	}

	private void setButtons() {
		// make sure that appropriate buttons enabled only
		bleft.setEnabled(numberentries != -1 && currententry != 0);
		bright.setEnabled(numberentries != -1
				&& currententry != numberentries - 1);
	}

	public void doLeft(View v) {
		if (numberentries != -1 && currententry != 0) {
			currententry--;
			setJSONUI(currententry);
		}
	}

	public void doRight(View v) {
		if (numberentries != -1 && currententry != numberentries) {
			currententry++;
			setJSONUI(currententry);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_parse_json, menu);
		return true;
	}
}
