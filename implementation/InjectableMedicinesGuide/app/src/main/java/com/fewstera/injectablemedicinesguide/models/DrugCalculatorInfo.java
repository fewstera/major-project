package com.fewstera.injectablemedicinesguide.models;

/**
 * Class that represents the model of a drugs calculator information
 *
 * @author Aidan Wynne Fewster
 * @version 1.0
 * @since 1.0
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

    /**
     * Gets the concentration units
     *
     * @return the concentration units
     */
    public String getConcentrationUnits() {
        return _concentrationUnits;
    }

    /**
     * Sets the concentration units
     *
     * @param concentrationUnits the concentration units
     */
    public void setConcentrationUnits(String concentrationUnits) {
        _concentrationUnits = concentrationUnits;
    }

    /**
     * Gets the ID of the drug this calculator belongs to
     *
     * @return the id
     */
    public int getDrugId() {
        return _drugId;
    }

    /**
     * Sets the ID of the drug this calculator belongs to
     *
     * @param drugId the id
     */
    public void setDrugId(int drugId) {
        _drugId = drugId;
    }

    /**
     * Gets the infusion rate label
     *
     * @return the infusion rate label
     */
    public String getInfusionRateLabel() {
        return _infusionRateLabel;
    }

    /**
     * Sets the infusion rate label
     *
     * @param infusionRateLabel the infusion rate label
     */
    public void setInfusionRateLabel(String infusionRateLabel) {
        _infusionRateLabel = infusionRateLabel;
    }

    /**
     * Gets the infusion rate units
     *
     * @return the infusion rate units
     */
    public String getInfusionRateUnits() {
        return _infusionRateUnits;
    }

    /**
     * Sets the infusion rate units
     *
     * @param infusionRateUnits the infusion rate units
     */
    public void setInfusionRateUnits(String infusionRateUnits) {
        _infusionRateUnits = infusionRateUnits;
    }

    /**
     * Gets the dose units
     *
     * @return the dose units
     */
    public String getDoseUnits() {
        return _doseUnits;
    }

    /**
     * Sets the dose units
     *
     * @param doseUnits the dose units
     */
    public void setDoseUnits(String doseUnits) {
        _doseUnits = doseUnits;
    }

    /**
     * Checks if the patient weight is required for the calculation
     *
     * @return true if its needed, false otherwise
     */
    public boolean isPatientWeightRequired() {
        return _patientWeightRequired;
    }

    /**
     * Sets if the patient weight is required for the calculation
     *
     * @param patientWeightRequired true if its needed, false otherwise
     */
    public void setPatientWeightRequired(boolean patientWeightRequired) {
        _patientWeightRequired = patientWeightRequired;
    }

    /**
     * Checks if the time is required for the calculation
     *
     * @return true if its needed, false otherwise
     */
    public boolean isTimeRequired() {
        return _timeRequired;
    }

    /**
     * Sets if the time is required for the calculation
     *
     * @param timeRequired true if its needed, false otherwise
     */
    public void setTimeRequired(boolean timeRequired) {
        _timeRequired = timeRequired;
    }

    /**
     * Gets the factor of the calculation
     *
     * @return the factor
     */
    public Integer getFactor() {
        return _factor;
    }

    /**
     * Sets the factor of the calculation
     *
     * @param factor the factor
     */
    public void setFactor(Integer factor) {
        _factor = factor;
    }
}
