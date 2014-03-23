package com.fewstera.injectablemedicinesguide;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class DownloadDataActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download_data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.download_data, menu);
		return true;
	}

}
