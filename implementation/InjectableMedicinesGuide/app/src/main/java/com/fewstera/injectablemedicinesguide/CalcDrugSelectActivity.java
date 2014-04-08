package com.fewstera.injectablemedicinesguide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import com.fewstera.injectablemedicinesguide.database.DatabaseHelper;
import java.util.List;

/**
 * Activity for selecting which drug to calculate dosgae for
 *
 * This activity is used to display a list of all drugs which have a calculator within the database.
 * The user can search for drugs by entering a string. Once the user has found a drug, they can click it,
 * which will open the calculator activity.
 *
 * @author Aidan Wynne Fewster
 * @version 1.0
 * @since 1.0
 */
public class CalcDrugSelectActivity extends LoggedInActivity {

    /* The Extras which are passed to the CalculateDrug activity */
    public final static String EXTRA_DRUG_ID = "com.fewstera.injectablemedicinesguide.extras.drugId";

    DatabaseHelper _db = new DatabaseHelper(this);
    ArrayAdapter<Drug> _listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc_drug_select);
        Log.d(MainActivity.DEBUG_NAME, "Started calc activity");
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Log.d(MainActivity.DEBUG_NAME, "Populating");
        poplateDrugsListView();
        Log.d(MainActivity.DEBUG_NAME, "Listener");
        startTextChangedListener();

        Log.d(MainActivity.DEBUG_NAME, "Loaded");


    }

    /**
     * Populates the ListView with the drug indexes and adds on click listeners for the items.
     */
    private void poplateDrugsListView() {
        /* Fetches all drug index from DB */
        Log.d(MainActivity.DEBUG_NAME, "Fetching data");
        List<Drug> drugsWithCalcs = _db.getAllDrugsWithCalcs();
        Log.d(MainActivity.DEBUG_NAME, "Got data");

        for(Drug d: drugsWithCalcs){
            Log.d(MainActivity.DEBUG_NAME, "Drug name" + d.getName());
        }
        /* Starts a new list adapter using the drugsIndex */
        _listAdapter = new ArrayAdapter<Drug>(this,
                android.R.layout.simple_list_item_1, drugsWithCalcs);

        ListView myList=(ListView)findViewById(android.R.id.list);
        /* Adds the adapter to the ListView */
        myList.setAdapter(_listAdapter);

        /* On item click listener for list view, called when an item is clicked */
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
                /* Fetch the drug index for the index which has been click */
                Drug drug = (Drug)adapter.getItemAtPosition(position);

                Intent in = new Intent(CalcDrugSelectActivity.this, ViewDrugActivity.class);
                /* Adds needed information for the view drug activity */
                in.putExtra(BrowseDrugsActivity.EXTRA_DRUG_ID, drug.getId());

                /* Starts the view drug activity */
                startActivity(in);
            }
        });
    }

    /**
     * Adds a listener to the search box, so that when the search text changes the results
     * are filtered
     */
    private void startTextChangedListener() {
        EditText searchText = (EditText)findViewById(R.id.search_text);

        /* Adds a listener for when the text changes within the search EditText */
        searchText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            /**
             * Handles the users search by filtering the list view.
             * Only drugs that match the search text will be shown within the list view.
             */
            public void onTextChanged(CharSequence searchText, int start, int before, int count) {
                /* Filter the list using the users search */
                _listAdapter.getFilter().filter(searchText);
            }

        });
    }

}
