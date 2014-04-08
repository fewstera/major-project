package com.fewstera.injectablemedicinesguide;

/**
 * Created by fewstera on 08/04/2014.
 */
public class Calculator {
    private DrugCalculatorInfo _calculatorInfo;
    private float _concentration, _dose, _infusionRate, _weight, _time = -1;

    public static final int SUCCESS = 0;
    public static final int ERROR_WEIGHT = 1;
    public static final int WARN_WEIGHT = 2;
    public static final int ERROR_TIME = 3;
    public static final int ERROR_DOSE = 4;

    public Calculator(DrugCalculatorInfo calculatorInfo){
        _calculatorInfo = calculatorInfo;
    }

    public int validateInfusionRateCalculator(boolean skipWarnings){
        if(!isWeightValid()){ return Calculator.ERROR_WEIGHT; }
        if(warnWeight()&&(!skipWarnings)) { return Calculator.WARN_WEIGHT; }
        if(!isTimeValid()){ return Calculator.ERROR_TIME; }
        if(!isDoseValid()){ return Calculator.ERROR_DOSE; }

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
            if((_weight<10)&&(_weight>300)){
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

}
