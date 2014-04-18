package com.fewstera.injectablemedicinesguide.database.tests;

import android.content.Context;
import android.test.ActivityTestCase;
import android.util.Log;

import com.fewstera.injectablemedicinesguide.MainActivity;
import com.fewstera.injectablemedicinesguide.database.DatabaseHelper;
import com.fewstera.injectablemedicinesguide.models.Drug;
import com.fewstera.injectablemedicinesguide.models.DrugCalculatorInfo;
import com.fewstera.injectablemedicinesguide.models.DrugIndex;
import com.fewstera.injectablemedicinesguide.models.DrugInformation;

import junit.framework.TestCase;

/**
 * Created by fewstera on 17/04/2014.
 */
public class DatabaseHelperTest extends ActivityTestCase {

    private Context _context;
    private DatabaseHelper _db;

    public void setUp(){
        _context = getInstrumentation().getTargetContext().getApplicationContext();
        _db = new DatabaseHelper(_context, "test");
        _db.truncateAll();
        populateDatabase();
    }

    public void createDrug(int id, String name){
        Drug drug = new Drug();
        drug.setId(id);
        drug.setName(name);
        for(int x = 1; x <= 5; x++){
            drug.addDrugInformation(new DrugInformation("Info " + x, "Helper", "Text"));
        }
        _db.createDrug(drug);
    }

    public void createIndex(int id, String name){
        DrugIndex index = new DrugIndex(id, name);
        _db.createDrugIndex(index);
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
            createIndex(x, "Index " + x);
        }

        /* Add calculators to 2 of the drugs (id 1 and 2)*/
        addCalcInfo(1);
        addCalcInfo(2);
    }

    public void testTruncateAll() throws Exception {
        _db.truncateAll();
        assertEquals("Truncate all, indexes is not empty",
                0, _db.getAllDrugIndexes().size());

        assertEquals("Truncate all, calcs is not empty",
                0, _db.getAllDrugsWithCalcs().size());

        assertNull("Truncate all, drugs is not empty",
                 _db.getDrugFromId(0));

        assertTrue("Truncate all, drug infos is not empty",
                 _db.getDrugInformationsFromDrugId(0).isEmpty());
    }

    public void testTruncateCalcs() throws Exception {
        _db.truncateCalcs();
        assertEquals("Truncate all, calcs is not empty",
                0, _db.getAllDrugsWithCalcs().size());

    }

    public void testTruncateIndexs() throws Exception {
        _db.truncateIndexs();
        assertEquals("Truncate all, indexes is not empty",
                0, _db.getAllDrugIndexes().size());
    }

    public void testCreateDrug() throws Exception {
        _db.truncateAll();

        createDrug(10, "Test drug");
        assertNotNull("Failed getting new drug",
                _db.getDrugFromId(10));
    }

    public void testCreateDrugIndex() throws Exception {
        _db.truncateAll();

        createIndex(10, "Test index");
        assertFalse("Failed getting new index",
                _db.getAllDrugIndexes().isEmpty());
    }

    public void testCreateDrugCalcInfo() throws Exception {
        createDrug(10, "Test drug");
        addCalcInfo(10);

        assertNotNull("Unable to fetch drug calcualtor",
                _db.getDrugCalcInfoFromDrugId(10));
    }

    public void testGetAllDrugIndexes() throws Exception {
        assertFalse("Get all indexes returning empty",
                _db.getAllDrugIndexes().isEmpty());
    }

    public void testGetAllDrugsWithCalcs() throws Exception {
        assertFalse("Get all calculators returning empty",
                _db.getAllDrugsWithCalcs().isEmpty());
    }

    public void testGetDrugFromId() throws Exception {
        assertNotNull("Fetching drug id failed",
                _db.getDrugFromId(0));
    }

    public void testGetDrugInformationsFromDrugId() throws Exception {
        assertFalse("Get drug infos for drug returning empty",
                _db.getDrugInformationsFromDrugId(0).isEmpty());
    }

    public void testGetDrugCalcInfoFromDrugId() throws Exception {
        assertNotNull("Fetching drug calculator from id failed",
                _db.getDrugFromId(1));
    }
}
