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
import android.widget.Button;
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

    public void openCalcClick(View view){
        Intent in = new Intent(this, CalculateActivity.class);
        /* Adds needed information for the calculate activity */
        in.putExtra(MainActivity.EXTRA_DRUG_ID, _drug.getId());
         /* Starts the calculate activity */
        startActivity(in);
    }

    private void loadDrugToView() {
        TextView drugHeader = (TextView) findViewById(R.id.drug_name_header);
        drugHeader.setText(_drug.getName());
        enableCalculator();
        addDrugInformation("ROUTE:", _drug.getRoute());
        addDrugInformation("TRADE NAME:", _drug.getTradeName());
        addDrugInformation("MEDICINE NAME:", _drug.getMedicineName());
        loadDrugInformations();
        addDrugInformation("VERSION:", _drug.getVersion());
        SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy");
        addDrugInformation("DATE PUBLISHED:", parser.format(_drug.getDatePublished()));
    }

    private void enableCalculator() {
        if(_drug.getCalculatorInfo(this)!=null){
            ((TextView) findViewById(R.id.calculator_header)).setVisibility(ViewGroup.VISIBLE);
            ((Button) findViewById(R.id.open_calc_button)).setVisibility(ViewGroup.VISIBLE);
        }
    }

    private void loadDrugInformations() {
        for(DrugInformation information: _drug.getDrugInformations(this)){
            String informationHtml = information.getSectionText();
            addDrugInformation(information.getHeaderText(), informationHtml);
        }
    }

    private void addDrugInformation(String header, String informationHtml) {
        if(!informationHtml.isEmpty()){
            informationHtml = informationHtml.replace("<SUP>o</SUP>", "&deg;");
            informationHtml = informationHtml.replace("<SUP>", "<small><small>");
            informationHtml = informationHtml.replace("</SUP>", "</small></small>");
            LayoutInflater inflater = getLayoutInflater();
            View newInformation = inflater.inflate(R.layout.drug_information, null, false);
            TextView infoName = (TextView) newInformation.findViewById(R.id.information_name);
            infoName.setText(header);
            TextView infoContent = (TextView) newInformation.findViewById(R.id.information_content);
            infoContent.setText(Html.fromHtml(informationHtml));
            ((ViewGroup) findViewById(R.id.drug_informations)).addView(newInformation);
        }
    }


}
