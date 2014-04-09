package com.fewstera.injectablemedicinesguide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends LoggedInActivity {

    public static final String DEBUG_NAME = "com.fewstera.IMG";
    public final static String EXTRA_DRUG_ID = "com.fewstera.injectablemedicinesguide.extras.drugId";


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        TextView welcomeText = (TextView) findViewById(R.id.welcome_text_view);
        String welcomeHtml = getResources().getString(R.string.welcome_text);
        welcomeText.setText(Html.fromHtml(welcomeHtml));

        TextView updateText = (TextView) findViewById(R.id.last_update_textview);
        String lastUpdate = Preferences.getString(this, Preferences.UPDATE_DATE_KEY, "never");

        String textHtml = String.format(getResources().getString(R.string.last_updated_text), lastUpdate);
        updateText.setText(Html.fromHtml(textHtml));
	}

    public void updateClick(View view){
        Intent intent = new Intent(this, DownloadDataActivity.class);
        startActivity(intent);
        finish();
    }

    public void calculatorClick(View view){
        Intent intent = new Intent(this, CalcDrugSelectActivity.class);
        startActivity(intent);
    }

    public void viewDrugsClick(View view){
        Intent intent = new Intent(this, BrowseDrugsActivity.class);
        startActivity(intent);
    }

}
