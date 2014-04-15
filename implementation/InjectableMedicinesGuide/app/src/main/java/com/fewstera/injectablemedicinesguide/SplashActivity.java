package com.fewstera.injectablemedicinesguide;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
/**
 * Splash activity of the application
 *
 * This is the activity responsible for displaying the splash screen when opening the application.
 *
 * @author Aidan Wynne Fewster
 * @version 1.0
 * @since 1.0
 */
public class SplashActivity extends Activity {

    /* How long the splash screen should be displayed for. */
    private final int WAIT_TIME = 2000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		/* Holds the value of which activity to start after splash */
		final Class<?> activityToOpen = getActivityClass();

        /* Starts the splash screen thread, which waits for 2 seconds */
        startSplashScreenThread(activityToOpen);

	}

    /**
     * Returns the class of the next activity to start. If the user isn't logged in
     * then Login activity will be returned. If the user is logged in but the database isn't
     * complete then the Download activity is returned. Otherwise the Main activity is returned.
     *
     * @return the class of the activity to open after splash screen.
     */
    private Class<?> getActivityClass(){
        /* If no credentials are stored, ask user to login. */
        if(!Auth.isLogged(this)){
            return LoginActivity.class;
        }else{
            /* If data isn't downloaded yet, begin download. */
            if(!Preferences.getDownloadComplete(this)){
                return DownloadDataActivity.class;
            }else{
                return MainActivity.class;
            }

        }
    }

    /**
     * Makes the user wait *WAIT_TIME* milliseconds while displaying the splash screen, afterwards
     * the activity passed in is launched.
     *
     * @param startActivityClass class of the activity to open after splash screen.
     */
    private void startSplashScreenThread(final Class<?> startActivityClass) {
        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(WAIT_TIME);
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


}
