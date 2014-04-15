package com.fewstera.injectablemedicinesguide;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Class for an activity where the user is logged in
 *
 * This class enables the menu for all activity's where the user is logged in.
 *
 * @author Aidan Wynne Fewster
 * @version 1.0
 * @since 1.0
 */
public class LoggedInActivity extends Activity{
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Determines which menu item has been clicked and performs appropriate action
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.action_home:
                intent.setClass(this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_browse_drugs:
                intent.setClass(this, BrowseDrugsActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_update:
                intent.setClass(this, DownloadDataActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_logout:
                Auth.logout(this);
                intent.setClass(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_exit:
                System.exit(0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
