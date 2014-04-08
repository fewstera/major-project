package com.fewstera.injectablemedicinesguide;

/**
 * Created by fewstera on 08/04/2014.
 */
public class DrugCalculatorInfo {
    private int _drugId;
    private String _infusionRateLabel;
    private String _infusionRateUnits;
    private String _doseUnits;
    private boolean _patientWeightRequired;
    private boolean _timeRequired;
    private Integer _factor;
    private String _concentrationUnits;

    public String getConcentrationUnits() {
        return _concentrationUnits;
    }

    public void setConcentrationUnits(String concentrationUnits) {
        _concentrationUnits = concentrationUnits;
    }

    public int getDrugId() {
        return _drugId;
    }

    public void setDrugId(int drugId) {
        _drugId = drugId;
    }

    public String getInfusionRateLabel() {
        return _infusionRateLabel;
    }

    public void setInfusionRateLabel(String infusionRateLabel) {
        _infusionRateLabel = infusionRateLabel;
    }

    public String getInfusionRateUnits() {
        return _infusionRateUnits;
    }

    public void setInfusionRateUnits(String infusionRateUnits) {
        _infusionRateUnits = infusionRateUnits;
    }

    public String getDoseUnits() {
        return _doseUnits;
    }

    public void setDoseUnits(String doseUnits) {
        _doseUnits = doseUnits;
    }

    public boolean isPatientWeightRequired() {
        return _patientWeightRequired;
    }

    public void setPatientWeightRequired(boolean patientWeightRequired) {
        _patientWeightRequired = patientWeightRequired;
    }

    public boolean isTimeRequired() {
        return _timeRequired;
    }

    public void setTimeRequired(boolean timeRequired) {
        _timeRequired = timeRequired;
    }

    public Integer getFactor() {
        return _factor;
    }

    public void setFactor(Integer factor) {
        _factor = factor;
    }
}
