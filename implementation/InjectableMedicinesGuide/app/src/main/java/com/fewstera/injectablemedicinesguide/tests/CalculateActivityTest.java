package com.fewstera.injectablemedicinesguide.tests;

import android.content.Context;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.fewstera.injectablemedicinesguide.CalcDrugSelectActivity;
import com.fewstera.injectablemedicinesguide.CalculateActivity;
import com.fewstera.injectablemedicinesguide.MainActivity;
import com.fewstera.injectablemedicinesguide.R;
import com.fewstera.injectablemedicinesguide.database.DatabaseHelper;
import com.fewstera.injectablemedicinesguide.models.Drug;
import com.fewstera.injectablemedicinesguide.models.DrugCalculatorInfo;

/**
 * Tests for the calculate activity
 *
 * Tests that the correct UI elements are displayed when they should and that the headers
 * for each input is correctly displayed
 *
 * @author Aidan Wynne Fewster
 * @version 1.0
 * @since 1.0
 */
public class CalculateActivityTest extends ActivityInstrumentationTestCase2<CalculateActivity> {
    private CalculateActivity _activity;
    private Context _context;
    private DatabaseHelper _db;
    private TextView _drugNameHeader, _infusionRateHeader, _doseHeader, _concentrationHeader
            , _patientWeightHeader, _timeHeader;
    private Spinner _typeSpinner;
    private EditText _infusionRate, _dose, _concentration, _patientWeight, _time;
    private Button _calcButton;
    private Drug _drug;
    private DrugCalculatorInfo _drugCalcInfo;


    public CalculateActivityTest() {
        super(CalculateActivity.class);
    }

    public void populateDatabase(){
        _db.truncateAll();

        _drug = new Drug();
        _drug.setId(1);
        _drug.setName("Adrenaline");
        _db.createDrug(_drug);

        _drugCalcInfo = new DrugCalculatorInfo();
        _drugCalcInfo.setDrugId(_drug.getId());
        _drugCalcInfo.setInfusionRateLabel("Adrenaline infusion rate");
        _drugCalcInfo.setInfusionRateUnits("mL/hour");
        _drugCalcInfo.setDoseUnits("micrograms/kg/minute");
        _drugCalcInfo.setPatientWeightRequired(true);
        _drugCalcInfo.setTimeRequired(true);
        _drugCalcInfo.setConcentrationUnits("mg/mL");
        _drugCalcInfo.setFactor(1);

        _db.createDrugCalcInfo(_drugCalcInfo);
    }

    public void setUp(){
        _context = getInstrumentation().getTargetContext().getApplicationContext();
        _db = new DatabaseHelper(_context, "test");
        populateDatabase();

        Intent i = new Intent(_context, CalcDrugSelectActivity.class);
        i.putExtra(MainActivity.EXTRA_TEST, true);
        i.putExtra(MainActivity.EXTRA_DRUG_ID, _drug.getId());
        setActivityIntent(i);
        _activity = getActivity();

        /* Retrieve all views */
        _drugNameHeader = (TextView) _activity.findViewById(R.id.drug_name_header);
        _infusionRateHeader = (TextView) _activity.findViewById(R.id.infusion_rate_header);
        _doseHeader = (TextView) _activity.findViewById(R.id.dose_header);
        _concentrationHeader = (TextView) _activity.findViewById(R.id.concentration_header);
        _patientWeightHeader = (TextView) _activity.findViewById(R.id.patient_weight_header);
        _timeHeader = (TextView) _activity.findViewById(R.id.time_header);

        _typeSpinner = (Spinner) _activity.findViewById(R.id.calculation_type_spinner);

        _infusionRate = (EditText) _activity.findViewById(R.id.infusion_rate);
        _dose = (EditText) _activity.findViewById(R.id.dose);
        _concentration = (EditText) _activity.findViewById(R.id.concentration);
        _patientWeight = (EditText) _activity.findViewById(R.id.patient_weight);
        _time = (EditText) _activity.findViewById(R.id.time);

        _calcButton = (Button) _activity.findViewById(R.id.calculate_button);
    }

    @SmallTest
    public void testPreconditions() {
        /* Check that nothing needed is null */
        assertNotNull("_drugNameHeader is null", _drugNameHeader);
        assertNotNull("_infusionRateHeader is null", _infusionRateHeader);
        assertNotNull("_doseHeader is null", _doseHeader);
        assertNotNull("_concentrationHeader is null", _concentrationHeader);
        assertNotNull("_patientWeightHeader is null", _patientWeightHeader);
        assertNotNull("_timeHeader is null", _timeHeader);
        assertNotNull("_typeSpinner is null", _typeSpinner);
        assertNotNull("_infusionRate is null", _infusionRate);
        assertNotNull("_dose is null", _dose);
        assertNotNull("_concentration is null", _concentration);
        assertNotNull("_patientWeight is null", _patientWeight);
        assertNotNull("_time is null", _time);
        assertNotNull("_calcButton is null", _calcButton);
    }

    @MediumTest
    public void testDrugHeader() throws Exception{
        final ViewGroup.LayoutParams layoutParams =
                _drugNameHeader.getLayoutParams();

        /* Check layout */
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.WRAP_CONTENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);

        /* Check the header contains the correct text */
        String drugHeader = _drug.getName();
        assertEquals("Drug header is set incorrectly",
                drugHeader, _drugNameHeader.getText().toString());
    }

    @MediumTest
    public void testSpinner() throws Exception{
        final ViewGroup.LayoutParams layoutParams =
                _typeSpinner.getLayoutParams();

        /* Check layout */
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.MATCH_PARENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);

        /* Check the spinner has Dose from IR selected */
        String spinnerText = _context.getResources().getString(R.string.calc_dose_from_ir);
        assertEquals("Drop down default is incorrect",
                spinnerText, _typeSpinner.getSelectedItem().toString());
    }

    @MediumTest
    public void testInfusionRate() throws Exception{
        final ViewGroup.LayoutParams headerLayoutParams =
                _infusionRateHeader.getLayoutParams();

        /* Check label layout */
        assertNotNull(headerLayoutParams);
        assertEquals(headerLayoutParams.width, WindowManager.LayoutParams.WRAP_CONTENT);
        assertEquals(headerLayoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);

        final ViewGroup.LayoutParams inputLayoutParams =
                _infusionRate.getLayoutParams();

        /* Check label layout */
        assertNotNull(inputLayoutParams);
        assertEquals(inputLayoutParams.width, WindowManager.LayoutParams.MATCH_PARENT);
        assertEquals(inputLayoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);

        /* Check the header contains the correct text */
        String infusionRateHeader = _drugCalcInfo.getInfusionRateLabel() + " (" + _drugCalcInfo.getInfusionRateUnits() + ")";
        assertEquals("Infusion rate header is set incorrectly",
                infusionRateHeader, _infusionRateHeader.getText().toString());

        assertEquals("Infusion rate header is hidden",
                View.VISIBLE, _infusionRateHeader.getVisibility());
        assertEquals("Infusion rate input is hidden",
                View.VISIBLE, _infusionRate.getVisibility());
    }

    @MediumTest
    public void testDose() throws Exception{
        final ViewGroup.LayoutParams headerLayoutParams =
                _doseHeader.getLayoutParams();

        /* Check dose header layout */
        assertNotNull(headerLayoutParams);
        assertEquals(headerLayoutParams.width, WindowManager.LayoutParams.WRAP_CONTENT);
        assertEquals(headerLayoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);

        final ViewGroup.LayoutParams inputLayoutParams =
                _dose.getLayoutParams();

        /* Check input layout */
        assertNotNull(inputLayoutParams);
        assertEquals(inputLayoutParams.width, WindowManager.LayoutParams.MATCH_PARENT);
        assertEquals(inputLayoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);

        /* Check the header contains the correct text */
        String doseHeader = "Dose (" + _drugCalcInfo.getDoseUnits() + ")";
        assertEquals("Dose header is set incorrectly",
                doseHeader, _doseHeader.getText().toString());

        assertEquals("Dose header is shown",
                View.GONE, _doseHeader.getVisibility());
        assertEquals("Dose input is shown",
                View.GONE, _dose.getVisibility());
    }

    @MediumTest
    public void testConcentration() throws Exception{
        final ViewGroup.LayoutParams headerLayoutParams =
                _concentrationHeader.getLayoutParams();

        /* Check concentration header layout */
        assertNotNull(headerLayoutParams);
        assertEquals(headerLayoutParams.width, WindowManager.LayoutParams.WRAP_CONTENT);
        assertEquals(headerLayoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);

        final ViewGroup.LayoutParams inputLayoutParams =
                _concentration.getLayoutParams();

        /* Check input layout */
        assertNotNull(inputLayoutParams);
        assertEquals(inputLayoutParams.width, WindowManager.LayoutParams.MATCH_PARENT);
        assertEquals(inputLayoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);

        /* Check the header contains the correct text */
        String concentrationHeader = "Concentration (" + _drugCalcInfo.getConcentrationUnits() + ")";
        assertEquals("Concentration header is set incorrectly",
                concentrationHeader, _concentrationHeader.getText().toString());

        assertEquals("Concentration header is hidden",
                View.VISIBLE, _concentrationHeader.getVisibility());
        assertEquals("Concentration input is hidden",
                View.VISIBLE, _concentration.getVisibility());
    }

    @MediumTest
    public void testWeight() throws Exception{
        final ViewGroup.LayoutParams headerLayoutParams =
                _patientWeightHeader.getLayoutParams();

        /* Check weight header layout */
        assertNotNull(headerLayoutParams);
        assertEquals(headerLayoutParams.width, WindowManager.LayoutParams.WRAP_CONTENT);
        assertEquals(headerLayoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);

        final ViewGroup.LayoutParams inputLayoutParams =
                _patientWeight.getLayoutParams();

        /* Check input layout */
        assertNotNull(inputLayoutParams);
        assertEquals(inputLayoutParams.width, WindowManager.LayoutParams.MATCH_PARENT);
        assertEquals(inputLayoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);

        /* Check the header contains the correct text */
        String weightHeader = "Patient weight (kg)";
        assertEquals("Weight header is set incorrectly",
                weightHeader, _patientWeightHeader.getText().toString());

        if(_drugCalcInfo.isPatientWeightRequired()){
            assertEquals("Weight header is hidden",
                    View.VISIBLE, _patientWeightHeader.getVisibility());
            assertEquals("Weight input is shown",
                    View.VISIBLE, _patientWeight.getVisibility());
        }else{
            assertEquals("Weight header is shown",
                    View.GONE, _patientWeightHeader.getVisibility());
            assertEquals("Weight input is shown",
                    View.GONE, _patientWeight.getVisibility());
        }
    }

    @MediumTest
    public void testTime() throws Exception{
        final ViewGroup.LayoutParams headerLayoutParams =
                _timeHeader.getLayoutParams();

        /* Check weight header layout */
        assertNotNull(headerLayoutParams);
        assertEquals(headerLayoutParams.width, WindowManager.LayoutParams.WRAP_CONTENT);
        assertEquals(headerLayoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);

        final ViewGroup.LayoutParams inputLayoutParams =
                _time.getLayoutParams();

        /* Check input layout */
        assertNotNull(inputLayoutParams);
        assertEquals(inputLayoutParams.width, WindowManager.LayoutParams.MATCH_PARENT);
        assertEquals(inputLayoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);

        /* Check the header contains the correct text */
        String timeHeader = "Time (minutes)";
        assertEquals("Time header is set incorrectly",
                timeHeader, _timeHeader.getText().toString());

        if(_drugCalcInfo.isTimeRequired()){
            assertEquals("Time header is hidden",
                    View.VISIBLE, _timeHeader.getVisibility());
            assertEquals("Time input is shown",
                    View.VISIBLE, _time.getVisibility());
        }else{
            assertEquals("Time header is shown",
                    View.GONE, _timeHeader.getVisibility());
            assertEquals("Time input is shown",
                    View.GONE, _time.getVisibility());
        }

        assertEquals("Time is not set to 60",
                "60", _time.getText().toString());

        assertFalse("Time is editable", _time.isFocusable());
    }

    @MediumTest
    public void testButton() throws Exception{
        final ViewGroup.LayoutParams layoutParams =
                _typeSpinner.getLayoutParams();

        /* Check layout */
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.MATCH_PARENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);

        /* Check the spinner has Dose from IR selected */
        String buttonText = _context.getResources().getString(R.string.calculate_button);
        assertEquals("Calculate button text is incorrect",
                buttonText, _calcButton.getText().toString());
    }

    public void tearDown() throws Exception{
        _db.truncateAll();
        super.tearDown();
    }
}