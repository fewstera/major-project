package com.fewstera.injectablemedicinesguide;

/**
 * Created by fewstera on 08/04/2014.
 */
public class Calculator {
    private DrugCalculatorInfo _calculatorInfo;
    private float _concentration, _dose, _infusionRate, _weight, _time = -1;
    private int _calculationType;

    public static final int SUCCESS = 0;
    public static final int ERROR_WEIGHT = 1;
    public static final int WARN_WEIGHT = 2;
    public static final int ERROR_TIME = 3;
    public static final int ERROR_DOSE = 4;
    public static final int ERROR_IR = 5;
    public static final int ERROR_CONCENTRATION = 6;

    public final static int TYPE_DOSE_FROM_IR = 0;
    public final static int TYPE_IR_FROM_DOSE = 1;
    public final static int TYPE_NOTHING_SELECTED = -1;

    public Calculator(DrugCalculatorInfo calculatorInfo){
        _calculatorInfo = calculatorInfo;
    }

    public void setType(int calculationType) {
        _calculationType = calculationType;
    }

    public void setConcentration(float concentration) {
        _concentration = concentration;
    }

    public void setDose(float dose) {
        _dose = dose;
    }

    public void setInfusionRate(float infusionRate) {
        _infusionRate = infusionRate;
    }

    public void setWeight(float weight) {
        _weight = weight;
    }


    public void setTime(float time) {
        _time = time;
    }

    public float getConcentration() {
        return _concentration;
    }

    public float getDose() {
        return _dose;
    }

    public float getInfusionRate() {
        return _infusionRate;
    }

    public float getWeight() {
        return _weight;
    }

    public float getTime() {
        return _time;
    }

    public int getCalculationType() {
        return _calculationType;
    }


    public int validate(boolean skipWarnings){
        if((_calculationType==Calculator.TYPE_IR_FROM_DOSE)&&!isDoseValid()){ return Calculator.ERROR_DOSE; }
        if((_calculationType==Calculator.TYPE_DOSE_FROM_IR)&&!isIRValid()){ return Calculator.ERROR_IR; }
        if(!isConcentrationValid()){ return Calculator.ERROR_CONCENTRATION; }
        if(!isWeightValid()){ return Calculator.ERROR_WEIGHT; }
        if(!isTimeValid()){ return Calculator.ERROR_TIME; }
        if(warnWeight()&&(!skipWarnings)) { return Calculator.WARN_WEIGHT; }

        return Calculator.SUCCESS;
    }

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

    private float calculateInfusionRate(float factor){
        return (_dose*_weight*_time)/(_concentration*factor);
    }

    private float calculateDose(float factor){
        return (_infusionRate*_concentration*factor)/(_weight*_time);
    }

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

    private boolean warnWeight(){
        if(_calculatorInfo.isPatientWeightRequired()){
            if((_weight<10)||(_weight>300)){
                return true;
            }
        }
        return false;
    }


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

    private boolean isDoseValid(){
        if(_dose<=0){
            return false;
        }
        return true;
    }

    private boolean isIRValid(){
        if(_infusionRate<=0){
            return false;
        }
        return true;
    }

    private boolean isConcentrationValid(){
        if(_concentration<=0){
            return false;
        }
        return true;
    }
}
