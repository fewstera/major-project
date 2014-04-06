package com.fewstera.injectablemedicinesguide;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.fewstera.injectablemedicinesguide.database.DatabaseHelper;

import java.util.List;

public class BrowseDrugsActivity extends Activity {

    public final static String EXTRA_DRUG_ID = "com.fewstera.injectablemedicinesguide.extras.drugId";

    DatabaseHelper _db = new DatabaseHelper(this);
    ArrayAdapter<DrugIndex> _listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_drugs);

        List<DrugIndex> values = _db.getAllDrugIndexes();

        _listAdapter = new ArrayAdapter<DrugIndex>(this,
                android.R.layout.simple_list_item_1, values);

        ListView myList=(ListView)findViewById(android.R.id.list);
        myList.setAdapter(_listAdapter);

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
                DrugIndex drugIndex = (DrugIndex)adapter.getItemAtPosition(position);
                Intent in = new Intent(BrowseDrugsActivity.this, ViewDrugActivity.class);
                Log.d("MyApplication", "Sending id: " + drugIndex.getDrugId());
                in.putExtra(BrowseDrugsActivity.EXTRA_DRUG_ID, drugIndex.getDrugId());
                startActivity(in);
            }
        });

        startTextChangedListener();


    }

    private void startTextChangedListener() {
        EditText searchText = (EditText)findViewById(R.id.search_text);
        searchText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence searchText, int start, int before, int count) {
                _listAdapter.getFilter().filter(searchText);
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.browse_drugs, menu);
        return true;
    }

}
