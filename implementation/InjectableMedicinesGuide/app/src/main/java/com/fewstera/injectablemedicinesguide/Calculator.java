package com.fewstera.injectablemedicinesguide;

import com.fewstera.injectablemedicinesguide.models.DrugCalculatorInfo;

/**
 * Class for validating and performing dose and infusion rate calculations
 *
 * @author Aidan Wynne Fewster
 * @version 1.0
 * @since 1.0
 */
public class Calculator {
    private DrugCalculatorInfo _calculatorInfo;
    private float _concentration, _dose, _infusionRate, _weight, _time = -1;
    private int _calculationType;

    /* Validation output values */
    public static final int SUCCESS = 0;
    public static final int ERROR_WEIGHT = 1;
    public static final int WARN_WEIGHT = 2;
    public static final int ERROR_TIME = 3;
    public static final int ERROR_DOSE = 4;
    public static final int ERROR_IR = 5;
    public static final int ERROR_CONCENTRATION = 6;

    /* Dosage type values */
    public final static int TYPE_DOSE_FROM_IR = 0;
    public final static int TYPE_IR_FROM_DOSE = 1;
    public final static int TYPE_NOTHING_SELECTED = -1;

    /**
     * Calculator constructor
     *
     * @param calculatorInfo the DrugCalculatorInfo of the drug this calulator if for.
     */
    public Calculator(DrugCalculatorInfo calculatorInfo){
        _calculatorInfo = calculatorInfo;
    }

    /**
     * Set the type of the calculation
     *
     * @param calculationType the type
     */
    public void setType(int calculationType) {
        _calculationType = calculationType;
    }

    /**
     * Sets the user entered concentration value of the drug
     *
     * @param concentration the concentration value
     */
    public void setConcentration(float concentration) {
        _concentration = concentration;
    }

    /**
     * Sets the user entered dose value
     *
     * @param dose the dose value
     */
    public void setDose(float dose) {
        _dose = dose;
    }

    /**
     * Sets the user entered infusion rate value
     *
     * @param infusionRate the dose value
     */
    public void setInfusionRate(float infusionRate) {
        _infusionRate = infusionRate;
    }

    /**
     * Sets the user entered patient weight in kg
     *
     * @param weight the weight of the patient in kg
     */
    public void setWeight(float weight) {
        _weight = weight;
    }

    /**
     * Sets the time
     *
     * @param time the time value in minutes
     */
    public void setTime(float time) {
        _time = time;
    }

    /**
     * Gets the concentration value
     *
     * @return the concentration value
     */
    public float getConcentration() {
        return _concentration;
    }

    /**
     * Get the dose
     * @return the dose
     */
    public float getDose() {
        return _dose;
    }

    /**
     * Get the infusion rate
     * @return the infusion rate
     */
    public float getInfusionRate() {
        return _infusionRate;
    }

    /**
     * Get the weight of the patient in kg
     * @return the weight in kg
     */
    public float getWeight() {
        return _weight;
    }

    /**
     * Get the time
     * @return the time in minutes
     */
    public float getTime() {
        return _time;
    }

    /**
     * Validates all the information required to perform the calculation.
     *
     * @param skipWarnings wether to skip any warnings about the data entered
     *
     * @return SUCCESS if validation is successful and the ERROR/WARN code otherwise.
     */
    public int validate(boolean skipWarnings){
        /* Validate dose, if needed */
        if((_calculationType==Calculator.TYPE_IR_FROM_DOSE)&&!isDoseValid()){ return Calculator.ERROR_DOSE; }

       /* Validate infusion rate, if needed */
        if((_calculationType==Calculator.TYPE_DOSE_FROM_IR)&&!isIRValid()){ return Calculator.ERROR_IR; }

        /* Validate concentration */
        if(!isConcentrationValid()){ return Calculator.ERROR_CONCENTRATION; }

        /* Validate the patient weight value, if needed */
        if(!isWeightValid()){ return Calculator.ERROR_WEIGHT; }

        /* Validate time if needed */
        if(!isTimeValid()){ return Calculator.ERROR_TIME; }

        /* Check if the validation should warn user about the weight value entered. */
        if(warnWeight()&&(!skipWarnings)) { return Calculator.WARN_WEIGHT; }

        return Calculator.SUCCESS;
    }

    /**
     * Perform the calculations for calculation type selected
     *
     * @return the value to the calculation.
     */
    public float calculate(){
        /* Check for any errors */
        if(validate(true)!=Calculator.SUCCESS){
            /* This should never be called, as the the developer should run validator before
             * sending to the calculate method. */
            throw new Error("Calculations should be validated before being sent to calculator");
        }else{
            float factor = (_calculatorInfo.getFactor()==null) ? 1 : _calculatorInfo.getFactor().floatValue();
            if(_calculationType == Calculator.TYPE_DOSE_FROM_IR){
                return calculateDose(factor);
            }else{
                return calculateInfusionRate(factor);
            }
        }
    }

    /**
     * Calculates the infusion rate from dosage
     *
     * @param factor the factor of the calculation
     * @return the value of the calculation
     */
    private float calculateInfusionRate(float factor){
        return (_dose*_weight*_time)/(_concentration*factor);
    }

    /**
     * Calculates the dose from infusion rate
     *
     * @param factor the factor of the calculation
     * @return the value of the calculation
     */
    private float calculateDose(float factor){
        return (_infusionRate*_concentration*factor)/(_weight*_time);
    }

    /**
     * Checks if the weight entered if valid (greater than 0)
     *
     * @return if weight is valid
     */
    private boolean isWeightValid(){
        if(_calculatorInfo.isPatientWeightRequired()){
            if(_weight<=0){
                return false;
            }
        }else{
            _weight = 1;
        }
        return true;
    }

    /**
     * Checks if the weight seems incorrect (if we should warn user)
     *
     * @return if weight seems invalid
     */
    private boolean warnWeight(){
        if(_calculatorInfo.isPatientWeightRequired()){
            if((_weight<10)||(_weight>300)){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the time entered is greater than 0
     *
     * @return if time is valid
     */
    private boolean isTimeValid(){
        if(_calculatorInfo.isTimeRequired()){
            if(_time<=0){
                return false;
            }
        }else{
            _time = 1;
        }
        return true;
    }

    /**
     * Checks if the dose entered is valid (greater than 0)
     *
     * @return if dose is valid
     */
    private boolean isDoseValid(){
        if(_dose<=0){
            return false;
        }
        return true;
    }

    /**
     * Checks if the infusion rate entered is valid (greater than 0)
     *
     * @return if infusion rate is valid
     */
    private boolean isIRValid(){
        if(_infusionRate<=0){
            return false;
        }
        return true;
    }


    /**
     * Checks if the concentration entered is valid (greater than 0)
     *
     * @return if concentration is valid
     */
    private boolean isConcentrationValid(){
        if(_concentration<=0){
            return false;
        }
        return true;
    }
}
