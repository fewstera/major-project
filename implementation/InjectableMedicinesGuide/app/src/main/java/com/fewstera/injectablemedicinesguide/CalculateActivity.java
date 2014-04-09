package com.fewstera.injectablemedicinesguide;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by fewstera on 08/04/2014.
 */
public class CalculateActivity extends LoggedInActivity  implements AdapterView.OnItemSelectedListener {

    Drug _drug;
    DatabaseHelper _db = new DatabaseHelper(this);
    Calculator _calculator;
    DrugCalculatorInfo _calculatorInfo;
    private int _calcType;
    private final DecimalFormat _twoDp = new DecimalFormat("0.##");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        int drugId = getIntent().getIntExtra(MainActivity.EXTRA_DRUG_ID, -1);
        if(drugId==-1){
            Intent i = new Intent(this, BrowseDrugsActivity.class);
            startActivity(i);
            Toast toast = Toast.makeText(getApplicationContext(), "Failed to find drug", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }

        _drug = _db.getDrugFromId(drugId);
        _calculatorInfo = _drug.getCalculatorInfo(this);
        _calculator = new Calculator(_calculatorInfo);

        setTitle(String.format(getResources().getString(R.string.title_activity_calculate), _drug.getName()));

        _calcType = Calculator.TYPE_DOSE_FROM_IR;

        setupUI();
    }

    private void setupUI() {
        TextView headerText = (TextView) findViewById(R.id.drug_name_header);
        headerText.setText(_drug.getName());
        loadHeaderText();
        loadCalcTypeSpinner();
        showRequiredValues();

    }

    private void loadHeaderText() {
        String doseHeader = String.format(getResources().getString(R.string.dose_header), _calculatorInfo.getDoseUnits());
        ((TextView) findViewById(R.id.dose_header)).setText(doseHeader);
        String iRHeader = _calculatorInfo.getInfusionRateLabel() + " (" + _calculatorInfo.getInfusionRateUnits() + ")";
        ((TextView) findViewById(R.id.infusion_rate_header)).setText(iRHeader);
        String conHeader = String.format(getResources().getString(R.string.concentration_header), _calculatorInfo.getConcentrationUnits());
        ((TextView) findViewById(R.id.concentration_header)).setText(conHeader);
    }

    private void showRequiredValues() {
        if(_calculatorInfo.isPatientWeightRequired()){ showWeight(); }
        if(_calculatorInfo.isTimeRequired()){ showTime(); }

    }

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

    private void showWeight(){
        ((TextView) findViewById(R.id.patient_weight_header)).setVisibility(View.VISIBLE);
        ((EditText) findViewById(R.id.patient_weight)).setVisibility(View.VISIBLE);
    }

    private void showTime(){
        ((TextView) findViewById(R.id.time_header)).setVisibility(View.VISIBLE);
        ((EditText) findViewById(R.id.time)).setVisibility(View.VISIBLE);
    }

    private void toggleDose(boolean show){
        int state = (show) ? View.VISIBLE : View.GONE;
        ((TextView) findViewById(R.id.dose_header)).setVisibility(state);
        EditText dose = (EditText) findViewById(R.id.dose);
        dose.setVisibility(state);
        dose.requestFocus();
    }

    private void toggleInfusionRate(boolean show){
        int state = (show) ? View.VISIBLE : View.GONE;
        ((TextView) findViewById(R.id.infusion_rate_header)).setVisibility(state);
        EditText infusionRate = (EditText) findViewById(R.id.infusion_rate);
        infusionRate.setVisibility(state);
        infusionRate.requestFocus();
    }

    private void loadCalcTypeSpinner() {
        Spinner calcSpinner = (Spinner) findViewById(R.id.calculation_type_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.calculation_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        calcSpinner.setOnItemSelectedListener(this);
        calcSpinner.setAdapter(adapter);
    }

    public void calculateClick(View view){

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

        validateAndSubmit();

    }

    private float getFloatFromEditText(int editTextId){
        float returnVal;
        try{
            returnVal = Float.parseFloat(((EditText) findViewById(editTextId)).getText().toString());
        }catch (NumberFormatException e){
            returnVal = -1;
        }
        return returnVal;
    }

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
                submitCalculation();
                break;
        }

    }

    private void warnAboutWeight() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String weightString = _twoDp.format(_calculator.getWeight());
        builder.setTitle("Is the weight (" + weightString + "kg) correct?");
        builder.setMessage("The weight you entered " + weightString + "kg seems incorrect. Do you want to continue using this value?");
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                submitCalculation();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void submitCalculation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Calculation results");

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.show_calculation_layout, null);
        WebView displayCalcView = (WebView) layout.findViewById(R.id.display_calc_html);
        WebSettings settings = displayCalcView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);

        float val = _calculator.calculate();
        String displayHtml = (_calcType==Calculator.TYPE_DOSE_FROM_IR) ? getDosCalcHtml(val) : getIRCalcHtml(val);
        displayCalcView.loadData(displayHtml, "text/html", "utf-8");
        builder.setView(layout);
        builder.setCancelable(false)
               .setPositiveButton("OK", null);
        builder.show();

    }

    private String getIRCalcHtml(float val) {
        String dose = _twoDp.format(_calculator.getDose());
        String weight = _twoDp.format(_calculator.getWeight());
        String time = _twoDp.format(_calculator.getTime());
        String concentration = _twoDp.format(_calculator.getConcentration());
        String answer = _twoDp.format(val);

        String firstEquationTop = "dose (" + _calculatorInfo.getDoseUnits() + ") "
                + ((_calculatorInfo.isPatientWeightRequired()) ? "&times; weight (kg) " : "")
                + ((_calculatorInfo.isTimeRequired()) ? " &times; time (mins)" : "");

        String firstEquationBottom = "concentration (" + _calculatorInfo.getConcentrationUnits() + ")";

        String secondEquationTop = dose + " " + _calculatorInfo.getDoseUnits() + " "
                + ((_calculatorInfo.isPatientWeightRequired()) ? "&times; " + weight + " kg " : "")
                + ((_calculatorInfo.isTimeRequired()) ? " &times; " + time + " mins" : "");

        String secondEquationBottom = concentration + " " + _calculatorInfo.getConcentrationUnits();

        String answerText = answer + " " + _calculatorInfo.getInfusionRateUnits();

        String returnHtml = getResources().getString(R.string.display_calculation_html);

        String type = getResources().getString(R.string.calc_ir_from_dose);

        returnHtml = String.format(returnHtml, type, firstEquationTop, firstEquationBottom,
                secondEquationTop, secondEquationBottom, answerText);

        return returnHtml;
    }

    private String getDosCalcHtml(float val) {
        String infusionRate = _twoDp.format(_calculator.getInfusionRate());
        String weight = _twoDp.format(_calculator.getWeight());
        String time = _twoDp.format(_calculator.getTime());
        String concentration = _twoDp.format(_calculator.getConcentration());
        String answer = _twoDp.format(val);

        String firstEquationTop = "infusion rate (" + _calculatorInfo.getInfusionRateUnits() + ") "
                + "&times; concentration (" + _calculatorInfo.getInfusionRateUnits()  + ")";

        String firstEquationBottom = ((_calculatorInfo.isTimeRequired()) ? " time (mins) &times;" : "")
                + ((_calculatorInfo.isPatientWeightRequired()) ? " weight (kg) &times;" : "");

        /* Remove the extra ' &times;' from the string */
        firstEquationBottom = firstEquationBottom.substring(0, firstEquationBottom.length() - 8);


        String secondEquationTop = infusionRate + " " + _calculatorInfo.getInfusionRateUnits() + " "
                + "&times; " + concentration + " " + _calculatorInfo.getInfusionRateUnits();


        String secondEquationBottom = ((_calculatorInfo.isTimeRequired()) ? " " + time + " mins &times;" : "")
                + ((_calculatorInfo.isPatientWeightRequired()) ? " " + weight + " kg &times;" : "");

        /* Remove the extra ' &times;' from the string */
        secondEquationBottom = secondEquationBottom.substring(0, secondEquationBottom.length() - 8);

        String answerText = answer + " " + _calculatorInfo.getDoseUnits();

        String returnHtml = getResources().getString(R.string.display_calculation_html);

        String type = getResources().getString(R.string.calc_dose_from_ir);

        returnHtml = String.format(returnHtml, type, firstEquationTop, firstEquationBottom,
                secondEquationTop, secondEquationBottom, answerText);

        return returnHtml;
    }

    private void displayToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

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