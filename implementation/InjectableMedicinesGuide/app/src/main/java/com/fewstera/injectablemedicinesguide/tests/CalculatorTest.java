package com.fewstera.injectablemedicinesguide.tests;

import com.fewstera.injectablemedicinesguide.Calculator;
import com.fewstera.injectablemedicinesguide.models.DrugCalculatorInfo;

import junit.framework.TestCase;

import java.text.DecimalFormat;

/**
 * Tests for the calculator class
 *
 * Tests the validate function by testing each possible output multiple times.
 *
 * Tests the success of the validation
 *
 * Test the output of the calculations by performing the same calculation using the medusa website
 * (http://www.injguide.nhs.uk/), recording the results and testing against the results found.
 * Drug of each equation type (pateint weight and time, just time and just patient weight) has been
 * selected for testing.
 *
 * @author Aidan Wynne Fewster
 * @version 1.0
 * @since 1.0
 */
public class CalculatorTest extends TestCase {
    private DrugCalculatorInfo _drugCalcInfo;
    private Calculator _calculator;

    private final DecimalFormat _twoDp = new DecimalFormat("0.##");

    public void setUp() throws Exception {
        super.setUp();
        _calculator = new Calculator(null);
    }

    private void setupAdrenaline(){
        _drugCalcInfo = new DrugCalculatorInfo();
        _drugCalcInfo.setDrugId(1);
        _drugCalcInfo.setInfusionRateLabel("Adrenaline infusion rate");
        _drugCalcInfo.setInfusionRateUnits("mL/hour");
        _drugCalcInfo.setDoseUnits("micrograms/kg/minute");
        _drugCalcInfo.setPatientWeightRequired(true);
        _drugCalcInfo.setTimeRequired(true);
        _drugCalcInfo.setConcentrationUnits("mg/mL");
        _drugCalcInfo.setFactor(1);
        _calculator = new Calculator(_drugCalcInfo);
    }

    private void setupMidazolam(){
        _drugCalcInfo = new DrugCalculatorInfo();
        _drugCalcInfo.setDrugId(2);
        _drugCalcInfo.setInfusionRateLabel("Midazolam infusion rate");
        _drugCalcInfo.setInfusionRateUnits("mL/hour");
        _drugCalcInfo.setDoseUnits("micrograms/kg/hour");
        _drugCalcInfo.setPatientWeightRequired(true);
        _drugCalcInfo.setTimeRequired(false);
        _drugCalcInfo.setConcentrationUnits("mg/mL");
        _drugCalcInfo.setFactor(1000);
        _calculator = new Calculator(_drugCalcInfo);
    }

    private void setupGlycerylTrinitrate(){
        _drugCalcInfo = new DrugCalculatorInfo();
        _drugCalcInfo.setDrugId(3);
        _drugCalcInfo.setInfusionRateLabel("Glyceryl trinitrate infusion rate");
        _drugCalcInfo.setInfusionRateUnits("mL/hour");
        _drugCalcInfo.setDoseUnits("micrograms/minute");
        _drugCalcInfo.setPatientWeightRequired(false);
        _drugCalcInfo.setTimeRequired(true);
        _drugCalcInfo.setConcentrationUnits("micrograms/mL");
        _drugCalcInfo.setFactor(1);
        _calculator = new Calculator(_drugCalcInfo);
    }

    public void testSetAndGetType() throws Exception {
        int type = Calculator.TYPE_IR_FROM_DOSE;
        _calculator.setType(type);

        assertEquals("Type not set correctly",
                type, _calculator.getType());

        type = Calculator.TYPE_DOSE_FROM_IR;
        _calculator.setType(type);

        assertEquals("Type not set correctly",
                type, _calculator.getType());
    }

    public void testSetAndGetConcentration() throws Exception {
        double concentration = (double) 123.24;
        _calculator.setConcentration(concentration);

        assertEquals("Concentration not set correctly",
                concentration, _calculator.getConcentration());

        concentration = (double) 3433;
        _calculator.setConcentration(concentration);

        assertEquals("Concentration not set correctly",
                concentration, _calculator.getConcentration());
    }

    public void testSetAndGetDose() throws Exception {
        double dose = (double) 734.2;
        _calculator.setDose(dose);

        assertEquals("Dose not set correctly",
                dose, _calculator.getDose());

        dose = (double) 398.44;
        _calculator.setDose(dose);

        assertEquals("Dose not set correctly",
                dose, _calculator.getDose());
    }

    public void testSetAndGetInfusionRate() throws Exception {
        double infusionRate = (double) 198.2;
        _calculator.setInfusionRate(infusionRate);

        assertEquals("Infusion rate not set correctly",
                infusionRate, _calculator.getInfusionRate());

        infusionRate = (double) 421.2;
        _calculator.setInfusionRate(infusionRate);

        assertEquals("Infusion rate not set correctly",
                infusionRate, _calculator.getInfusionRate());
    }

    public void testSetAndGetWeight() throws Exception {
        double weight = (double) 54.3;
        _calculator.setWeight(weight);

        assertEquals("Weight not set correctly",
                weight, _calculator.getWeight());

        weight = (double) 80.2;
        _calculator.setWeight(weight);

        assertEquals("Dose not set correctly",
                weight, _calculator.getWeight());
    }

    public void testSetAndGetTime() throws Exception {
        double time = (double) 60;
        _calculator.setTime(time);

        assertEquals("Time not set correctly",
                time, _calculator.getTime());

        time = (double) 503.1;
        _calculator.setTime(time);

        assertEquals("Time not set correctly",
                time, _calculator.getTime());
    }

    /* Set all values correctly */
    private void setCorrectValues() {
        _calculator.setType(Calculator.TYPE_IR_FROM_DOSE);
        _calculator.setDose((double) 120);
        _calculator.setInfusionRate((double) 240);
        _calculator.setConcentration((double) 80);
        _calculator.setTime((double) 60);
        _calculator.setWeight((double) 84.5);
    }

    /**
     * Tests that validate returns error on infusion rate.
     */
    public void testValidateErrorInfusionRate(){
        setupAdrenaline();

        /* Set all other attributes correctly */
        setCorrectValues();

        _calculator.setType(Calculator.TYPE_DOSE_FROM_IR);

        assertNotSame("Infusion rate error showing when no error",
                Calculator.ERROR_IR, _calculator.validate(false));

        _calculator.setInfusionRate((double) -23.43);
        assertEquals("Infusion rate error should be returned",
                Calculator.ERROR_IR, _calculator.validate(false));

        _calculator.setInfusionRate((double) 0);
        assertEquals("Infusion rate error should be returned",
                Calculator.ERROR_IR, _calculator.validate(false));

        /* Changing type so infusion rate is irrelevant */
        _calculator.setType(Calculator.TYPE_IR_FROM_DOSE);

        assertNotSame("Infusion rate error showing when calculation infusion rate",
                Calculator.ERROR_IR, _calculator.validate(false));


    }

    /**
     * Tests that validate returns error on infusion rate.
     */
    public void testValidateErrorDose(){
        setupAdrenaline();

        /* Set all other attributes correctly */
        setCorrectValues();

        _calculator.setType(Calculator.TYPE_IR_FROM_DOSE);

        assertNotSame("Dose error showing when no error",
                Calculator.ERROR_DOSE, _calculator.validate(false));

        _calculator.setDose((double) -60.43);
        assertEquals("Dose error should be returned",
                Calculator.ERROR_DOSE, _calculator.validate(false));

        _calculator.setDose((double) 0);
        assertEquals("Dose error should be returned",
                Calculator.ERROR_DOSE, _calculator.validate(false));

        /* Changing type so dose is irrelevant */
        _calculator.setType(Calculator.TYPE_DOSE_FROM_IR);

        assertNotSame("Dose error showing when calculation Dose",
                Calculator.ERROR_DOSE, _calculator.validate(false));


    }

    /**
     * Tests that validate returns error on weight.
     */
    public void testValidateErrorWeight(){
        setupAdrenaline();
        /* Set all other attributes correctly */
        setCorrectValues();

        assertNotSame("Weight error showing when no error",
                Calculator.ERROR_WEIGHT, _calculator.validate(false));

        _calculator.setWeight((double) -10.34);
        assertEquals("Weight error should be returned",
                Calculator.ERROR_WEIGHT, _calculator.validate(false));

        _calculator.setWeight((double) 0);
        assertEquals("Weight error should be returned",
                Calculator.ERROR_WEIGHT, _calculator.validate(false));
    }

    /**
     * Tests that validate returns error on concentration.
     */
    public void testValidateConcentration(){
        setupAdrenaline();
        /* Set all other attributes correctly */
        setCorrectValues();

        assertNotSame("Concentration error showing when no error",
                Calculator.ERROR_CONCENTRATION, _calculator.validate(false));

        _calculator.setConcentration((double) -542);
        assertEquals("Concentration error should be returned",
                Calculator.ERROR_CONCENTRATION, _calculator.validate(false));

        _calculator.setConcentration((double) 0);
        assertEquals("Concentration error should be returned",
                Calculator.ERROR_CONCENTRATION, _calculator.validate(false));
    }

    /**
     * Tests that validate returns error on time.
     */
    public void testValidateTime(){
        setupAdrenaline();
        /* Set all other attributes correctly */
        setCorrectValues();

        assertNotSame("Time error showing when no error",
                Calculator.ERROR_TIME, _calculator.validate(false));

        _calculator.setTime((double) -34);
        assertEquals("Time error should be returned",
                Calculator.ERROR_TIME, _calculator.validate(false));

        _calculator.setTime((double) 0);
        assertEquals("Time error should be returned",
                Calculator.ERROR_TIME, _calculator.validate(false));
    }

    /**
     * Tests that validate returns a warning when the weight seems odd
     */
    public void testValidateWarnWeight(){
        setupAdrenaline();
        /* Set all other attributes correctly */
        setCorrectValues();

        assertNotSame("Warning showing when nothing to warn about",
                Calculator.WARN_WEIGHT, _calculator.validate(false));

        _calculator.setWeight((double) 2000);
        assertEquals("Weight warning should be returned",
                Calculator.WARN_WEIGHT, _calculator.validate(false));

        _calculator.setWeight((double) 400);
        assertEquals("Weight warning should be returned",
                Calculator.WARN_WEIGHT, _calculator.validate(false));
    }

    /**
     * Tests that validate returns success, when all data is valid
     */
    public void testValidateSuccess(){
        setupAdrenaline();
        /* Set all other attributes correctly */
        setCorrectValues();

        assertEquals("Success should be returned",
                Calculator.SUCCESS, _calculator.validate(false));
    }

    /**
     * These calculation's were performed using the NHS website
     * and the results then copied into this test to ensure the
     * calculations are correct
     */
    public void testAdrenalineCalcualtions(){
        setupAdrenaline();

        _calculator.setType(Calculator.TYPE_IR_FROM_DOSE);
        _calculator.setDose((double) 50.89);
        _calculator.setWeight((double) 80);
        _calculator.setTime((double) 60);
        _calculator.setConcentration((double) 123);

        assertEquals("Success should be returned",
                Calculator.SUCCESS, _calculator.validate(false));

        assertEquals("Adrenaline calculation 1 invalid",
                "1985.95", _twoDp.format(_calculator.calculate()));

        /* Setup calculation 2 */
        _calculator.setDose((double) 12);
        _calculator.setWeight((double) 60.8);
        _calculator.setConcentration((double) 654);

        assertEquals("Adrenaline calculation 2 invalid",
                "66.94", _twoDp.format(_calculator.calculate()));


        /* Setup calculation 3 */
        _calculator.setDose((double) 60.60);
        _calculator.setWeight((double) 56.43);
        _calculator.setConcentration((double) 12);

        assertEquals("Adrenaline calculation 3 invalid",
                "17098.29", _twoDp.format(_calculator.calculate()));

        /* change to infusion rate calculations */
        _calculator.setType(Calculator.TYPE_DOSE_FROM_IR);

        /* Setup calculation 4 */
        _calculator.setInfusionRate((double) 70.60);
        _calculator.setWeight((double) 49);
        _calculator.setConcentration((double) 75.43);

        assertEquals("Adrenaline calculation 4 invalid",
                "1.81", _twoDp.format(_calculator.calculate()));

        /* Setup calculation 5 */
        _calculator.setInfusionRate((double) 98.89);
        _calculator.setConcentration((double) 45.4);
        _calculator.setWeight((double) 80);

        assertEquals("Adrenaline calculation 5 invalid",
                "0.94", _twoDp.format(_calculator.calculate()));


        /* Setup calculation 6 */
        _calculator.setInfusionRate((double) 14.43);
        _calculator.setConcentration((double) 56.4);
        _calculator.setWeight((double) 90.8);

        assertEquals("Adrenaline calculation 6 invalid",
                "0.15", _twoDp.format(_calculator.calculate()));

    }

    /**
     * These calculation's were performed using the NHS website
     * and the results then copied into this test to ensure the
     * calculations are correct
     */
    public void testMidazolamCalcualtions(){
        setupMidazolam();

        _calculator.setType(Calculator.TYPE_IR_FROM_DOSE);
        _calculator.setDose((double) 50.89);
        _calculator.setWeight((double) 80);
        _calculator.setTime((double) 60);
        _calculator.setConcentration((double) 123);

        assertEquals("Success should be returned",
                Calculator.SUCCESS, _calculator.validate(false));

        assertEquals("Midazolam calculation 1 invalid",
                "0.03", _twoDp.format(_calculator.calculate()));

        /* Setup calculation 2 */
        _calculator.setDose((double) 12);
        _calculator.setWeight((double) 60.8);
        _calculator.setConcentration((double) 6);

        assertEquals("Midazolam calculation 2 invalid",
                "0.12", _twoDp.format(_calculator.calculate()));


        /* Setup calculation 3 */
        _calculator.setDose((double) 60.60);
        _calculator.setWeight((double) 56.43);
        _calculator.setConcentration((double) 12);

        assertEquals("Midazolam calculation 3 invalid",
                "0.28", _twoDp.format(_calculator.calculate()));

        /* change to infusion rate calculations */
        _calculator.setType(Calculator.TYPE_DOSE_FROM_IR);

        /* Setup calculation 4 */
        _calculator.setInfusionRate((double) 70.60);
        _calculator.setConcentration((double) 75.43);
        _calculator.setWeight((double) 49);

        assertEquals("Midazolam calculation 4 invalid",
                "108680.78", _twoDp.format(_calculator.calculate()));

        /* Setup calculation 5 */
        _calculator.setInfusionRate((double) 98.89);
        _calculator.setConcentration((double) 45.4);
        _calculator.setWeight((double) 80);

        assertEquals("Midazolam calculation 5 invalid",
                "56120.08", _twoDp.format(_calculator.calculate()));


        /* Setup calculation 6 */
        _calculator.setInfusionRate((double) 14.43);
        _calculator.setConcentration((double) 16.4);
        _calculator.setWeight((double) 90.8);

        assertEquals("Midazolam calculation 6 invalid",
                "2606.3", _twoDp.format(_calculator.calculate()));

    }

    /**
     * These calculation's were performed using the NHS website
     * and the results then copied into this test to ensure the
     * calculations are correct
     */
    public void testGlycerylTrinitrate(){
        setupGlycerylTrinitrate();

        _calculator.setType(Calculator.TYPE_IR_FROM_DOSE);
        _calculator.setDose((double) 65.89);
        _calculator.setTime((double) 60);
        _calculator.setConcentration((double) 43.33);

        assertEquals("Success should be returned",
                Calculator.SUCCESS, _calculator.validate(false));

        assertEquals("Glyceryl trinitrate calculation 1 invalid",
                "91.24", _twoDp.format(_calculator.calculate()));

        /* Setup calculation 2 */
        _calculator.setDose((double) 43);
        _calculator.setConcentration((double) 62);

        assertEquals("Glyceryl trinitrate calculation 2 invalid",
                "41.61", _twoDp.format(_calculator.calculate()));


        /* Setup calculation 3 */
        _calculator.setDose((double) 32.23);
        _calculator.setConcentration((double) 43);

        assertEquals("Glyceryl trinitrate calculation 3 invalid",
                "44.97", _twoDp.format(_calculator.calculate()));

        /* change to infusion rate calculations */
        _calculator.setType(Calculator.TYPE_DOSE_FROM_IR);

        /* Setup calculation 4 */
        _calculator.setInfusionRate((double) 43.23);
        _calculator.setConcentration((double) 76.54);

        assertEquals("Glyceryl trinitrate calculation 4 invalid",
                "55.15", _twoDp.format(_calculator.calculate()));

        /* Setup calculation 5 */
        _calculator.setInfusionRate((double) 67.5);
        _calculator.setConcentration((double) 44.4);

        assertEquals("Glyceryl trinitrate calculation 5 invalid",
                "49.95", _twoDp.format(_calculator.calculate()));


        /* Setup calculation 6 */
        _calculator.setInfusionRate((double) 34.44);
        _calculator.setConcentration((double) 54.32);

        assertEquals("Glyceryl trinitrate calculation 6 invalid",
                "31.18", _twoDp.format(_calculator.calculate()));

    }

}
