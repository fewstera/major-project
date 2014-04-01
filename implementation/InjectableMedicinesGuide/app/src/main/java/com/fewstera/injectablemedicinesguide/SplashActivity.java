package com.fewstera.injectablemedicinesguide;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.preference.Preference;
import android.view.Menu;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		//Holds the value of which activity to start
		final Class<?> startActivityClass;
		
		/* If no credentials are stored, ask user to login. */
		if(!Auth.isLogged(this)){
			startActivityClass = LoginActivity.class;
		}else{
            /* If data isn't downloaded yet, begin download. */
            if(!Preferences.getDownloadComplete(this)){
                startActivityClass = DownloadDataActivity.class;
            }else{
                startActivityClass = MainActivity.class;
            }

		}
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
