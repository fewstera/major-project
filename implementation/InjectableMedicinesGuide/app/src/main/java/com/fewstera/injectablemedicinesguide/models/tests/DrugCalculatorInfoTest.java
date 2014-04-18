package com.fewstera.injectablemedicinesguide.models.tests;

import android.support.v7.appcompat.R;

import com.fewstera.injectablemedicinesguide.models.DrugCalculatorInfo;

import junit.framework.TestCase;

/**
 * Created by fewstera on 17/04/2014.
 */
public class DrugCalculatorInfoTest extends TestCase {
    private DrugCalculatorInfo _drugCalculatorInfo;

    public void setUp() throws Exception {
        super.setUp();
        _drugCalculatorInfo = new DrugCalculatorInfo();
    }

    public void testConstuct() throws Exception {
        assertNotNull("Drug calculator info is null", _drugCalculatorInfo);
    }

    public void testGetAndSetConcentrationUnits() throws Exception {
        String test = "micrograms/mL";
        _drugCalculatorInfo.setConcentrationUnits(test);
        assertEquals("Concentration units getter or setter invalid",
                test, _drugCalculatorInfo.getConcentrationUnits());
    }

    public void testGetAndSetDrugId() throws Exception {
        int id = 123;
        _drugCalculatorInfo.setDrugId(123);
        assertEquals("Drug id getter or setter invalid",
                id, _drugCalculatorInfo.getDrugId());
    }

    public void testGetAndSetInfusionRateLabel() throws Exception {
        String test = "Adrenaline infusion rate";
        _drugCalculatorInfo.setInfusionRateLabel(test);
        assertEquals("Infusion rate label getter or setter invalid",
                test, _drugCalculatorInfo.getInfusionRateLabel());
    }

    public void testGetAndSetInfusionRateUnits() throws Exception {
        String test = "mL/hour";
        _drugCalculatorInfo.setInfusionRateUnits(test);
        assertEquals("Infusion rate units getter or setter invalid",
                test, _drugCalculatorInfo.getInfusionRateUnits());
    }

    public void testGetAndSetDoseUnits() throws Exception {
        String test = "micrograms/minute";
        _drugCalculatorInfo.setDoseUnits(test);
        assertEquals("Dose units getter or setter invalid",
                test, _drugCalculatorInfo.getDoseUnits());
    }

    public void testGetAndSetTimeRequired() throws Exception {
        boolean test = false;
        _drugCalculatorInfo.setTimeRequired(test);
        assertEquals("Time required getter or setter invalid",
                test, _drugCalculatorInfo.isTimeRequired());

        test = true;
        _drugCalculatorInfo.setTimeRequired(test);
        assertEquals("Time required getter or setter invalid",
                test, _drugCalculatorInfo.isTimeRequired());
    }

    public void testGetAndSetWeightRequired() throws Exception {
        boolean test = false;
        _drugCalculatorInfo.setPatientWeightRequired(test);
        assertEquals("Weight required getter or setter invalid",
                test, _drugCalculatorInfo.isPatientWeightRequired());

        test = true;
        _drugCalculatorInfo.setPatientWeightRequired(test);
        assertEquals("Weight required getter or setter invalid",
                test, _drugCalculatorInfo.isPatientWeightRequired());
    }

    public void testGetAndSetFactor() throws Exception {
        Integer test = new Integer(123);
        _drugCalculatorInfo.setFactor(test);
        assertTrue("Factor required getter or setter invalid",
                test.equals(_drugCalculatorInfo.getFactor()));
    }
}
