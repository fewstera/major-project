package com.fewstera.injectablemedicinesguide;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fewstera.injectablemedicinesguide.database.DatabaseHelper;
import com.fewstera.injectablemedicinesguide.models.Drug;
import com.fewstera.injectablemedicinesguide.models.DrugCalculatorInfo;

import java.text.DecimalFormat;

/**
 * Class for handling and displaying the calculate activity
 *
 * This class is used to layout the calculate values and to send the calculation to
 * the calculate class to validate and calculate.
 *
 * @author Aidan Wynne Fewster
 * @version 1.0
 * @since 1.0
 */
public class CalculateActivity extends LoggedInActivity  implements AdapterView.OnItemSelectedListener {

    Drug _drug;
    DatabaseHelper _db = new DatabaseHelper(this);
    Calculator _calculator;
    DrugCalculatorInfo _calculatorInfo;
    private int _calcType;

    /* Format for the display of numbers (2 decimal places) */
    private final DecimalFormat _twoDp = new DecimalFormat("0.##");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        int drugId = getIntent().getIntExtra(MainActivity.EXTRA_DRUG_ID, -1);

        _drug = _db.getDrugFromId(drugId);

        /* Check to make sure that the drug has been passed with the intent actually exists. */
        if(_drug==null){
            Intent i = new Intent(this, BrowseDrugsActivity.class);
            startActivity(i);
            Toast toast = Toast.makeText(getApplicationContext(), "Failed to find drug", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }

        _calculatorInfo = _drug.getCalculatorInfo(this);
        _calculator = new Calculator(_calculatorInfo);

        setTitle(String.format(getResources().getString(R.string.title_activity_calculate), _drug.getName()));

        setupUI();
    }

    /* This method is responsible for hiding and showing the needed inputs and filling labels */
    private void setupUI() {
        TextView headerText = (TextView) findViewById(R.id.drug_name_header);
        headerText.setText(_drug.getName());
        loadHeaderText();
        loadCalcTypeSpinner();
        showRequiredValues();

    }

    /**
     * Loads the appropriate text into the header labels.
     */
    private void loadHeaderText() {
        String doseHeader = String.format(getResources().getString(R.string.dose_header), _calculatorInfo.getDoseUnits());
        ((TextView) findViewById(R.id.dose_header)).setText(doseHeader);
        String iRHeader = _calculatorInfo.getInfusionRateLabel() + " (" + _calculatorInfo.getInfusionRateUnits() + ")";
        ((TextView) findViewById(R.id.infusion_rate_header)).setText(iRHeader);
        String conHeader = String.format(getResources().getString(R.string.concentration_header), _calculatorInfo.getConcentrationUnits());
        ((TextView) findViewById(R.id.concentration_header)).setText(conHeader);
    }

    /**
     * Hides and shows the patient weight and time required labels and inputs where needed.
     */
    private void showRequiredValues() {
        if(_calculatorInfo.isPatientWeightRequired()){ showWeight(); }
        if(_calculatorInfo.isTimeRequired()){ showTime(); }

    }

    /**
     * This method hides and shows the infusion rate and dose when the user changes the
     * calculation type
     */
    private void updateCalculationtype() {
        if(_calcType == Calculator.TYPE_IR_FROM_DOSE){
            toggleDose(true);
            toggleInfusionRate(false);
        }else{
            ((TextView) findViewById(R.id.infusion_rate)).requestFocus();
            toggleDose(false);
            toggleInfusionRate(true);
        }
    }

    /**
     * Shows the patient weight label and input
     */
    private void showWeight(){
        ((TextView) findViewById(R.id.patient_weight_header)).setVisibility(View.VISIBLE);
        ((EditText) findViewById(R.id.patient_weight)).setVisibility(View.VISIBLE);
    }

    /**
     * Shows the time label and input
     */
    private void showTime(){
        ((TextView) findViewById(R.id.time_header)).setVisibility(View.VISIBLE);
        ((EditText) findViewById(R.id.time)).setVisibility(View.VISIBLE);
    }

    /**
     * Shows and hides the dose input and labels where needed
     */
    private void toggleDose(boolean show){
        int state = (show) ? View.VISIBLE : View.GONE;
        ((TextView) findViewById(R.id.dose_header)).setVisibility(state);
        EditText dose = (EditText) findViewById(R.id.dose);
        dose.setVisibility(state);
        dose.requestFocus();
    }

    /**
     * Shows and hides the infusion rate input and labels where needed
     */
    private void toggleInfusionRate(boolean show){
        int state = (show) ? View.VISIBLE : View.GONE;
        ((TextView) findViewById(R.id.infusion_rate_header)).setVisibility(state);
        EditText infusionRate = (EditText) findViewById(R.id.infusion_rate);
        infusionRate.setVisibility(state);
        infusionRate.requestFocus();
    }


    /**
     * Loads the available calculation types from the strings array a populates the drop down list.
     */
    private void loadCalcTypeSpinner() {
        Spinner calcSpinner = (Spinner) findViewById(R.id.calculation_type_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.calculation_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        calcSpinner.setOnItemSelectedListener(this);
        calcSpinner.setAdapter(adapter);
    }


    /**
     * This method is classed when the user clicks the calculate button. It gathers all information
     * and sends it to the calculate object for validation. After validation the user will either be
     * displayed with an error, warning or the successful calculation.
     *
     * @param view the view of the button pressed.
     */
    public void calculateClick(View view){

        /* Hide the keyboard from the screen */
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(getCurrentFocus()!=null){
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        _calculator.setType(_calcType);

        if(_calcType==Calculator.TYPE_DOSE_FROM_IR){
            float infusionRate = getFloatFromEditText(R.id.infusion_rate);
            _calculator.setInfusionRate(infusionRate);
        }else if(_calcType==Calculator.TYPE_IR_FROM_DOSE){
            float dose = getFloatFromEditText(R.id.dose);
            _calculator.setDose(dose);
        }else{
            displayToast("Error, no calculation type selected");
            return ;
        }


        float concentration = getFloatFromEditText(R.id.concentration);
        _calculator.setConcentration(concentration);

        if(_calculatorInfo.isPatientWeightRequired()){
            float weight = getFloatFromEditText(R.id.patient_weight);
            _calculator.setWeight(weight);
        }

        if(_calculatorInfo.isTimeRequired()){
            float time = getFloatFromEditText(R.id.time);
            _calculator.setTime(time);
        }

        /* Send for validation */
        validateAndSubmit();

    }


    /**
     * Returns a float from a EditTExt
     *
     * @param editTextId the id of the EditText
     * @return the float value of the EditText or -1 if not a number
     */
    private float getFloatFromEditText(int editTextId){
        float returnVal;
        try{
            returnVal = Float.parseFloat(((EditText) findViewById(editTextId)).getText().toString());
        }catch (NumberFormatException e){
            returnVal = -1;
        }
        return returnVal;
    }


    /**
     * Validates all the entered information and alerts the user of any error / warnings
     */
    private void validateAndSubmit() {
        switch(_calculator.validate(false)){
            case Calculator.ERROR_DOSE:
                displayToast("Dosage must be greater than 0");
                break;
            case Calculator.ERROR_IR:
                displayToast("Infusion rate must be greater than 0");
                break;

            case Calculator.ERROR_CONCENTRATION:
                displayToast("Concentration must greater than 0");
                break;
            case Calculator.ERROR_TIME:
                displayToast("Time must greater than 0");
                break;
            case Calculator.ERROR_WEIGHT:
                displayToast("Weight must greater than 0");
                break;
            case Calculator.WARN_WEIGHT:
                warnAboutWeight();
                break;
            case Calculator.SUCCESS:
                /* Success, retrieve the calculation */
                submitCalculation();
                break;
        }

    }


    /**
     * Shows the user with a warning regarding the weight they have entered within a dialog. This
     * is called when the weight seems incorrect (To low or to high)
     */
    private void warnAboutWeight() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String weightString = _twoDp.format(_calculator.getWeight());
        builder.setTitle("Is the weight (" + weightString + "kg) correct?");
        builder.setMessage("The weight you entered " + weightString + "kg seems incorrect. Do you want to continue using this value?");
        /* Do nothing when the user presses cancel */
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                /* Perform calculation if user press continue */
                submitCalculation();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    /**
     * This method retrieves the value of the calculation and displays the results, along with
     * an explanation of the calculation to the user via a web view.
     */
    private void submitCalculation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Calculation results");

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.show_calculation_layout, null);
        WebView displayCalcView = (WebView) layout.findViewById(R.id.display_calc_html);

        /* Zoom the WebView so the calculation fits inside and allow user to zoom. */
        WebSettings settings = displayCalcView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);

        /* Retrieve the calculated value */
        float val = _calculator.calculate();

        /* Gets the HTML to display within the webview */
        String displayHtml = (_calcType==Calculator.TYPE_DOSE_FROM_IR) ? getDosCalcHtml(val) : getIRCalcHtml(val);

        /* Display the webview */
        displayCalcView.loadData(displayHtml, "text/html", "utf-8");
        builder.setView(layout);
        builder.setCancelable(false)
               .setPositiveButton("OK", null);
        builder.show();

    }


    /**
     * Returns the HTML for displaying the calculations for infusion rate from dose.
     * The HTML produces 2 equations (explaining the calculation used) and then answer.
     *
     * @param val the answer to the calculation
     */
    private String getIRCalcHtml(float val) {
        String dose = _twoDp.format(_calculator.getDose());
        String weight = _twoDp.format(_calculator.getWeight());
        String time = _twoDp.format(_calculator.getTime());
        String concentration = _twoDp.format(_calculator.getConcentration());
        String answer = _twoDp.format(val);

        /* Information for the first equation (The explanation equation)  */
        String firstEquationTop = "dose (" + _calculatorInfo.getDoseUnits() + ") "
                + ((_calculatorInfo.isPatientWeightRequired()) ? "&times; weight (kg) " : "")
                + ((_calculatorInfo.isTimeRequired()) ? " &times; time (mins)" : "");

        String firstEquationBottom = "concentration (" + _calculatorInfo.getConcentrationUnits() + ")";

        /* Information for the second equation (The explanation equation with numbers)  */
        String secondEquationTop = dose + " " + _calculatorInfo.getDoseUnits() + " "
                + ((_calculatorInfo.isPatientWeightRequired()) ? "&times; " + weight + " kg " : "")
                + ((_calculatorInfo.isTimeRequired()) ? " &times; " + time + " mins" : "");

        String secondEquationBottom = concentration + " " + _calculatorInfo.getConcentrationUnits();

        String answerText = answer + " " + _calculatorInfo.getInfusionRateUnits();

        String returnHtml = getResources().getString(R.string.display_calculation_html);

        String type = getResources().getString(R.string.calc_ir_from_dose);

        /* Populate the HMTL from the strings.xml file with the required values. */
        returnHtml = String.format(returnHtml, type, firstEquationTop, firstEquationBottom,
                secondEquationTop, secondEquationBottom, answerText);

        return returnHtml;
    }

    /**
     * Returns the HTML for displaying the calculations for dose from infusion rate.
     * The HTML produces 2 equations (explaining the calculation used) and then answer.
     *
     * @param val the answer to the calculation
     */
    private String getDosCalcHtml(float val) {
        String infusionRate = _twoDp.format(_calculator.getInfusionRate());
        String weight = _twoDp.format(_calculator.getWeight());
        String time = _twoDp.format(_calculator.getTime());
        String concentration = _twoDp.format(_calculator.getConcentration());
        String answer = _twoDp.format(val);

        /* Information for the first equation (The explanation equation)  */
        String firstEquationTop = "infusion rate (" + _calculatorInfo.getInfusionRateUnits() + ") "
                + "&times; concentration (" + _calculatorInfo.getInfusionRateUnits()  + ")";

        String firstEquationBottom = ((_calculatorInfo.isTimeRequired()) ? " time (mins) &times;" : "")
                + ((_calculatorInfo.isPatientWeightRequired()) ? " weight (kg) &times;" : "");

        /* Remove the extra ' &times;' from the string */
        firstEquationBottom = firstEquationBottom.substring(0, firstEquationBottom.length() - 8);

        /* Information for the second equation (The explanation equation with numbers)  */
        String secondEquationTop = infusionRate + " " + _calculatorInfo.getInfusionRateUnits() + " "
                + "&times; " + concentration + " " + _calculatorInfo.getInfusionRateUnits();


        String secondEquationBottom = ((_calculatorInfo.isTimeRequired()) ? " " + time + " mins &times;" : "")
                + ((_calculatorInfo.isPatientWeightRequired()) ? " " + weight + " kg &times;" : "");

        /* Remove the extra ' &times;' from the string */
        secondEquationBottom = secondEquationBottom.substring(0, secondEquationBottom.length() - 8);

        String answerText = answer + " " + _calculatorInfo.getDoseUnits();

        String returnHtml = getResources().getString(R.string.display_calculation_html);

        String type = getResources().getString(R.string.calc_dose_from_ir);

        /* Populate the HMTL from the strings.xml file with the required values. */
        returnHtml = String.format(returnHtml, type, firstEquationTop, firstEquationBottom,
                secondEquationTop, secondEquationBottom, answerText);

        return returnHtml;
    }

    /**
     * Displays a message to the user in a Toast
     *
     * @param message the message to display
     * @see android.widget.Toast
     */
    private void displayToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * This method is called when the user changes the selected calcualtion type within the drop
     * down menu
     *
     * @param adapterView the adapter view of the spinner
     * @param view the view of the selected item
     * @param pos the position of selected item
     * @param id the id of the selected item
     */
    public void onItemSelected(AdapterView<?> adapterView, View view,
                               int pos, long id) {
        String selectValue = (String)adapterView.getItemAtPosition(pos);
        if(selectValue.equals(getResources().getString(R.string.calc_dose_from_ir))){
            _calcType = Calculator.TYPE_DOSE_FROM_IR;
        }else{
            _calcType = Calculator.TYPE_IR_FROM_DOSE;
        }

        updateCalculationtype();
    }

    /**
     * This should never be called, as the spinner should never be hidden, but is required
     * for the implementation of OnItemSelectedListener
     *
     * @param adapterView the adapted view of the spinner
     */
    public void onNothingSelected(AdapterView<?> adapterView) {
        _calcType = Calculator.TYPE_NOTHING_SELECTED;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}