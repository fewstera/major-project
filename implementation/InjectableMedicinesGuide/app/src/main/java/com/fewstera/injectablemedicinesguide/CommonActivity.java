package com.fewstera.injectablemedicinesguide;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.fewstera.injectablemedicinesguide.models.DrugInformation;

/**
 * Class for an activity where the user is logged in
 *
 * This class enables the menu for all activity's where the user is logged in.
 *
 * @author Aidan Wynne Fewster
 * @version 1.0
 * @since 1.0
 */
public class CommonActivity extends Activity{
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.common, menu);
        return true;
    }

    /**
     * Displays the About dialog
     *
     * @see android.app.AlertDialog
     */
    protected void displayAbout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.about_title));
        String aboutHtml = getResources().getString(R.string.about_text);
        builder.setMessage(Html.fromHtml(aboutHtml));
        builder.setCancelable(false)
                .setPositiveButton(getString(R.string.close_button), null);
        AlertDialog dialog = builder.show();

        /* Decrease text size */
        TextView message = (TextView) dialog.findViewById(android.R.id.message);
        message.setTextSize(14);
    }

    /**
     * Determines which menu item has been clicked and performs appropriate action
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.action_about:
                displayAbout();
                return true;
            case R.id.action_exit:
                System.exit(0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
