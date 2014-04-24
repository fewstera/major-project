package com.fewstera.injectablemedicinesguide;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
/**
 * Main activity of the system
 *
 * This is the first activity the user see upon opening the application (provided they've previously
 * logged in).
 *
 * This activity allows user to upload the database ad navigate to other section of the application
 *
 * @author Aidan Wynne Fewster
 * @version 1.0
 * @since 1.0
 */
public class MainActivity extends LoggedInActivity {

    public static final String DEBUG_NAME = "com.fewstera.IMG";
    public static final String EXTRA_TEST = "com.fewstera.injectablemedicinesguide.test.TESTING";
    public final static String EXTRA_DRUG_ID = "com.fewstera.injectablemedicinesguide.extras.drugId";


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            setContentView(R.layout.activity_main);
        }else{
            setContentView(R.layout.activity_main_pre);
        }


        /* Populate the welcome text */
        TextView welcomeText = (TextView) findViewById(R.id.welcome_text_view);
        String welcomeHtml = getResources().getString(R.string.welcome_text);
        welcomeText.setText(Html.fromHtml(welcomeHtml));

        /* Populate the last update text */
        TextView updateText = (TextView) findViewById(R.id.last_update_textview);
        String lastUpdate = Preferences.getString(this, Preferences.UPDATE_DATE_KEY, "never");
        String textHtml = String.format(getResources().getString(R.string.last_updated_text), lastUpdate);
        updateText.setText(Html.fromHtml(textHtml));
	}

    /**
     *  When the update button is clicked
     */
    public void updateClick(View view){
        Intent intent = new Intent(this, DownloadDataActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * When the calculator button is clicked
     *
     * @param view of the button
     */
    public void calculatorClick(View view){
        Intent intent = new Intent(this, CalcDrugSelectActivity.class);
        startActivity(intent);
    }

    /**
     * When the view drugs button is clicked
     *
     * @param view of the button
     */
    public void viewDrugsClick(View view){
        Intent intent = new Intent(this, BrowseDrugsActivity.class);
        startActivity(intent);
    }
    /**
     * When the about button is clicked
     *
     * @param view of the button
     */
    public void aboutClick(View view){
        displayAbout();
    }

}
