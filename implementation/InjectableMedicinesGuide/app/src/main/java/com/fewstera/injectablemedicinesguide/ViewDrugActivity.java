package com.fewstera.injectablemedicinesguide;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fewstera.injectablemedicinesguide.database.DatabaseHelper;
import com.fewstera.injectablemedicinesguide.models.Drug;
import com.fewstera.injectablemedicinesguide.models.DrugInformation;

/**
 * This class is an activity for displaying a drug and all its information. If a drug has
 * a calculator, this will also allow the user to navigate to the calculator for that drug.
 *
 * @author Aidan Wynne Fewster
 * @version 1.0
 * @since 1.0
 */
public class ViewDrugActivity extends LoggedInActivity {

    Drug _drug;
    DatabaseHelper _db = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_drug);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        /* Retrieve the drug to display  */
        String indexName = getIntent().getStringExtra(BrowseDrugsActivity.EXTRA_INDEX_NAME);
        int drugId = getIntent().getIntExtra(BrowseDrugsActivity.EXTRA_DRUG_ID, -1);
        _drug = _db.getDrugFromId(drugId);

        setTitle(String.format(getResources().getString(R.string.title_activity_view_drug), indexName));

        /* If the drug does not exists */
        if(_drug==null){
            Intent i = new Intent(this, BrowseDrugsActivity.class);
            startActivity(i);
            Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.view_drug_error), Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }

        loadDrugToView();
    }

    /**
     * Called when the user clicks the calculator button
     *
     * @param view of the button
     */
    public void openCalcClick(View view){
        Intent in = new Intent(this, CalculateActivity.class);
        /* Adds needed information for the calculate activity */
        in.putExtra(MainActivity.EXTRA_DRUG_ID, _drug.getId());
         /* Starts the calculate activity */
        startActivity(in);
    }

    /**
     * Retrieve all information regarding a drug and displays it to the user.
     */
    private void loadDrugToView() {
        TextView drugHeader = (TextView) findViewById(R.id.drug_name_header);
        drugHeader.setText(_drug.getName());
        enableCalculator();
        loadDrugInformations();
    }

    /**
     * Displays the calculator button if the drug has a calculator
     */
    private void enableCalculator() {
        if(_drug.getCalculatorInfo(this)!=null){
            ((TextView) findViewById(R.id.calculator_header)).setVisibility(ViewGroup.VISIBLE);
            ((Button) findViewById(R.id.open_calc_button)).setVisibility(ViewGroup.VISIBLE);
        }
    }

    /**
     * Display all DrugInformation's.
     */
    private void loadDrugInformations() {
        for(DrugInformation information: _drug.getDrugInformations(this)){
            addDrugInformation(information);
        }
    }

    /**
     * Adds a DrugInformation to the view.
     *
     * @param drugInfo the DrugInformation to display
     */
    private void addDrugInformation(DrugInformation drugInfo) {
        String informationHtml = drugInfo.getSectionText();
        /* Check the section text isnt empty */
        if(!informationHtml.isEmpty()){

            informationHtml = parseHtml(informationHtml);

            /* inflate the drug information layout */
            LayoutInflater inflater = getLayoutInflater();
            View newInformation = inflater.inflate(R.layout.drug_information, null, false);

            /* Set the drug info name */
            TextView infoName = (TextView) newInformation.findViewById(R.id.information_name);
            infoName.setText(drugInfo.getHeaderText());

            /* Set the section text */
            TextView infoContent = (TextView) newInformation.findViewById(R.id.information_content);
            infoContent.setText(Html.fromHtml(informationHtml));

            /* If there is a header helper, display an info button and make the header clickable */
            if(drugInfo.getHeaderHelper()!=null){
                /* Show the button */
                ImageButton infoButton = (ImageButton) newInformation.findViewById(R.id.info_button);
                infoButton.setVisibility(View.VISIBLE);

                /* Set the tag for the button and header, so that click can be registered */
                Integer tag = new Integer(drugInfo.getId());
                infoButton.setTag(tag);
                infoName.setTag(tag);
            }else{
                /* Disable clicks if no header helper */
                infoName.setOnClickListener(null);
            }
            /* Add the inflated drug information to the view */
            ((ViewGroup) findViewById(R.id.drug_informations)).addView(newInformation);
        }
    }

    /**
     * Called when the user clicks the info icon. This then displays help for the selected item
     *
     * @param view the buttons view
     */
    public void onInfoClicked(View view){
        int clickedId = ((Integer) view.getTag()).intValue();
        DrugInformation clickedDrugInfo = null;
        /* Find which DrugInformation has been clicked on */
        for(DrugInformation information :_drug.getDrugInformations()){
            if(information.getId()==clickedId){
                clickedDrugInfo = information;
                break;
            }
        }

        /* Display the AlertDialog */
        if(clickedDrugInfo!=null){
            displayInfo(clickedDrugInfo);
        }
    }

    /**
     * Displays the HeaderHelper for the given DrugInformation within an AlertDialog
     *
     * @param clickedDrugInfo the DrugInformation thats been clicked
     * @see android.app.AlertDialog
     */
    private void displayInfo(DrugInformation clickedDrugInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Information");
        String infoHtml = parseHtml(clickedDrugInfo.getHeaderHelper());
        builder.setMessage(Html.fromHtml(infoHtml));
        builder.setCancelable(false)
                .setPositiveButton("Close", null);
        AlertDialog dialog = builder.show();

        /* Decrease text size */
        TextView message = (TextView) dialog.findViewById(android.R.id.message);
        message.setTextSize(14);
    }

    /**
     * Parses HTML so that it's looks better on mobile devices.
     *
     * @param html the html to parse
     * @return the parsed html
     */
    private String parseHtml(String html){
        html = html.replace("<SUP>o</SUP>", "&deg;");
        html = html.replace("<SUP>", "<small><small>");
        html = html.replace("</SUP>", "</small></small>");
        return html;
    }


}
