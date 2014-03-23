package com.fewstera.injectablemedicinesguide;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		//Holds the value of which activity to start
		final Class<?> startActivityClass;
		
		/* If no credentials are stored, ask user to login. */
		if( (Preferences.getString(this, Preferences.USERNAME_KEY)==null) ||
			(Preferences.getString(this, Preferences.PASSWORD_KEY)==null)){
			//startActivityClass = LoginActivity.class;
		}else{
			//startActivityClass = MainActivity.class;
		}
        startActivityClass = DownloadDataActivity.class;
		Thread splashTread = new Thread() {
			@Override
			public void run() {
				try {
					synchronized (this) {
						wait(2000);
					}
				} catch (InterruptedException e) {}
				finally {
					Intent intent = new Intent();
					intent.setClass(SplashActivity.this, startActivityClass);
					startActivity(intent);
					finish();
				}

			}
		};
        splashTread.start();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}

}
