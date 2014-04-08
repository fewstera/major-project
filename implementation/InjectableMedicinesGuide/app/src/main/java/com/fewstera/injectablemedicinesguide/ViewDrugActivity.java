package com.fewstera.injectablemedicinesguide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fewstera.injectablemedicinesguide.database.DatabaseHelper;

import java.text.SimpleDateFormat;

public class ViewDrugActivity extends LoggedInActivity {

    Drug _drug;
    DatabaseHelper _db = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_drug);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        String indexName = getIntent().getStringExtra(BrowseDrugsActivity.EXTRA_INDEX_NAME);
        int drugId = getIntent().getIntExtra(BrowseDrugsActivity.EXTRA_DRUG_ID, -1);
        if(drugId==-1){
            Intent i = new Intent(this, BrowseDrugsActivity.class);
            startActivity(i);
            Toast toast = Toast.makeText(getApplicationContext(), "Failed to find drug", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }

        setTitle(String.format(getResources().getString(R.string.title_activity_view_drug), indexName));

        _drug = _db.getDrugFromId(drugId);

        loadDrugToView();
    }

    private void loadDrugToView() {
        TextView drugHeader = (TextView) findViewById(R.id.drug_name_header);
        drugHeader.setText(_drug.getName());
        addDrugInformation("ROUTE:", _drug.getRoute());
        addDrugInformation("TRADE NAME:", _drug.getTradeName());
        addDrugInformation("MEDICINE NAME:", _drug.getMedicineName());
        loadDrugInformations();
        addDrugInformation("VERSION:", _drug.getVersion());
        SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy");
        addDrugInformation("DATE PUBLISHED:", parser.format(_drug.getDatePublished()));
    }

    private void loadDrugInformations() {
        for(DrugInformation information: _drug.getDrugInformations(this)){
            String informationHtml = information.getSectionText();
            informationHtml = informationHtml.replace("<SUP>o</SUP>", "&deg;");
            informationHtml = informationHtml.replace("<SUP>", "<small><small>");
            informationHtml = informationHtml.replace("</SUP>", "</small></small>");
            addDrugInformation(information.getHeaderText(), informationHtml);
        }
    }

    private void addDrugInformation(String header, String informationHtml) {
        if(!informationHtml.isEmpty()){
            LayoutInflater inflater = getLayoutInflater();
            View newInformation = inflater.inflate(R.layout.drug_information, null, false);
            TextView infoName = (TextView) newInformation.findViewById(R.id.information_name);
            infoName.setText(header);
            TextView infoContent = (TextView) newInformation.findViewById(R.id.information_content);
            infoContent.setText(Html.fromHtml(informationHtml));
            ((ViewGroup) findViewById(R.id.drug_informations)).addView(newInformation);
        }
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
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
