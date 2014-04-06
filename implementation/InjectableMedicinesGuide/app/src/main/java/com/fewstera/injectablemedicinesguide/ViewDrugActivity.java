package com.fewstera.injectablemedicinesguide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.fewstera.injectablemedicinesguide.database.DatabaseHelper;

public class ViewDrugActivity extends Activity {

    Drug _drug;
    DatabaseHelper _db = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_drug);

        int drugId = getIntent().getIntExtra(BrowseDrugsActivity.EXTRA_DRUG_ID, -1);
        Log.d("MyApplication", "Recieved: " + drugId);

        if(drugId==-1){
            Log.d("MyApplication", "Killing");
            Intent i = new Intent(this, BrowseDrugsActivity.class);
            startActivity(i);
            Toast toast = Toast.makeText(getApplicationContext(), "Failed to find drug", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }

        _drug = _db.getDrugFromId(drugId);
        TextView drugName = (TextView) findViewById(R.id.drugName);
        drugName.setText(_drug.getName());
        Log.d("MyApplication", "Loaded");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_logout:
                Auth.logout(this);
                Intent intent = new Intent();
                intent.setClass(ViewDrugActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
