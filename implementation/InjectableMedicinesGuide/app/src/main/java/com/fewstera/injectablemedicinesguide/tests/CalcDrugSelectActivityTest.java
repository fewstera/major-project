package com.fewstera.injectablemedicinesguide.tests;

import android.content.Context;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;


import com.fewstera.injectablemedicinesguide.MainActivity;
import com.fewstera.injectablemedicinesguide.CalcDrugSelectActivity;
import com.fewstera.injectablemedicinesguide.R;
import com.fewstera.injectablemedicinesguide.ViewDrugActivity;
import com.fewstera.injectablemedicinesguide.database.DatabaseHelper;
import com.fewstera.injectablemedicinesguide.models.Drug;
import com.fewstera.injectablemedicinesguide.models.DrugCalculatorInfo;
import com.fewstera.injectablemedicinesguide.models.DrugIndex;

/**
 * Tests for the calculator drug select activity
 *
 * @author Aidan Wynne Fewster
 * @version 1.0
 * @since 1.0
 */
public class CalcDrugSelectActivityTest  extends ActivityInstrumentationTestCase2<CalcDrugSelectActivity> {
    private CalcDrugSelectActivity _activity;
    private Context _context;
    private DatabaseHelper _db;
    private ListView _list;
    private EditText _searchBox;
    private int _listSize;


    public CalcDrugSelectActivityTest() {
        super(CalcDrugSelectActivity.class);
    }

    public void createDrug(int id, String name){
        Drug drug = new Drug();
        drug.setId(id);
        drug.setName(name);
        _db.createDrug(drug);
    }

    public void addCalcInfo(int drugId){
        DrugCalculatorInfo drugCalc = new DrugCalculatorInfo();
        drugCalc.setDrugId(drugId);
        drugCalc.setInfusionRateLabel("IR Label");
        drugCalc.setInfusionRateUnits("mL/hour");
        drugCalc.setDoseUnits("micrograms/kg/minute");
        drugCalc.setPatientWeightRequired(true);
        drugCalc.setTimeRequired(true);
        drugCalc.setConcentrationUnits("mg/mL");
        drugCalc.setFactor(1);
        _db.createDrugCalcInfo(drugCalc);
    }

    public void populateDatabase(){
        _db.truncateAll();

        /* Create 3 drugs drugs and add them to database*/
        for(int x = 0; x < 3; x++){
            createDrug(x, "Drug " + x);
        }

        /* Add calculators to 2 of the drugs (id 1 and 2)*/
        addCalcInfo(1);
        addCalcInfo(2);

        Log.d(MainActivity.DEBUG_NAME, "Calculation number: " + _db.getAllDrugsWithCalcs().get(0).getName());

        /* Amount of drugs with calculators to show in list (2) */
        _listSize = 2;
    }

    public void setUp(){
        _context = getInstrumentation().getTargetContext().getApplicationContext();
        _db = new DatabaseHelper(_context, "test");
        populateDatabase();

        Intent i = new Intent(_context, CalcDrugSelectActivity.class);
        i.putExtra(MainActivity.EXTRA_TEST, true);
        setActivityIntent(i);
        _activity = getActivity();

        /* Fetch the third drug information */
        _list = (ListView) _activity.findViewById(R.id.list);
        _searchBox = (EditText) _activity.findViewById(R.id.search_text);

    }

    @SmallTest
    public void testPreconditions() {
        /* Check that nothing needed is null */
        assertNotNull("_list is null", _list);
        assertNotNull("_searchBox is null", _searchBox);
    }

    @MediumTest
    public void testSearchOnScreen() throws Exception{
        /* Check the search is on the screen */
        final View decorView = _activity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(decorView, _searchBox);

        final ViewGroup.LayoutParams layoutParams =
                _searchBox.getLayoutParams();

        /* Check layout */
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.MATCH_PARENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);

        /* Check the header contains the correct text */
        String searchHint = _activity.getResources().getString(R.string.search_hint);
        assertEquals("Search hint set incorrectly",
                searchHint, _searchBox.getHint().toString());
    }

    @MediumTest
    public void testListOnScreen() throws Exception{
        /* Check the download message is on the screen */
        final View decorView = _activity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(decorView, _list);

        final ViewGroup.LayoutParams layoutParams =
                _list.getLayoutParams();

        /* Check layout */
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.MATCH_PARENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.MATCH_PARENT);
    }

    @MediumTest
    public void testListSize() throws Exception{
        assertEquals("List not displaying correct amount of drugs with calculators",
                _listSize, _list.getCount());
    }

    public void tearDown() throws Exception{
        _db.truncateAll();
        super.tearDown();
    }
}