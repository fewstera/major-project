package com.fewstera.injectablemedicinesguide.models;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;

import com.fewstera.injectablemedicinesguide.database.DatabaseHelper;
/**
 * Class that represents the model of a Drug
 *
 * @author Aidan Wynne Fewster
 * @version 1.0
 * @since 1.0
 */
public class Drug implements Comparable<Drug>{
	
	private int _id;
	private String _name;
    private DrugCalculatorInfo _calculatorInfo;

    private ArrayList<DrugInformation> _drugInformations;

    /**
     * Get id of the drug (drug-no)
     * @return the id
     */
    public int getId(){
        return _id;
    }

    /**
     * Sets the drug's id
     * @param id the id
     */
    public void setId(int id){
        _id = id;
    }

    /**
     * Get drugs name
     * @return the name
     */
    public String getName(){
        return _name;
    }

    /**
     * Set drug name
     * @param name the name
     */
    public void setName(String name){
        _name = name;
    }

    /**
     * Add new information to the drug
     *
     * @param drugInfo DrugInformation to add
     */

    public void addDrugInformation(DrugInformation drugInfo){
        if(_drugInformations==null){ _drugInformations = new ArrayList<DrugInformation>(); }

        _drugInformations.add(drugInfo);
    }

    /**
     * Get an ArrayList of all the drug information's
     *
     * @return the list of DrugInformation's
     */
    public ArrayList<DrugInformation> getDrugInformations(){
        if(_drugInformations==null){ _drugInformations = new ArrayList<DrugInformation>(); }
        return _drugInformations;
    }

    /**
     *  Get an ArrayList of all the drug information's from the database
     *
     * @param context the application context
     * @return
     */
    public ArrayList<DrugInformation> getDrugInformations(Context context){
        if(_drugInformations==null){
            DatabaseHelper db = new DatabaseHelper(context);
            _drugInformations = db.getDrugInformationsFromDrugId(this.getId());
        }
        return _drugInformations;
    }

    /**
     * Get the DrugCalculatorInfo for the drug
     * @param context the application context
     * @return the DrugCalculatorInfo or null if there isn't any
     */
    public DrugCalculatorInfo getCalculatorInfo(Context context){
        if(_calculatorInfo==null){
            DatabaseHelper db = new DatabaseHelper(context);
            _calculatorInfo = db.getDrugCalcInfoFromDrugId(this.getId());
        }
        return _calculatorInfo;
    }

    /**
     * Compares a drug to another drug
     * @param test the drug to test against
     * @return the result of the comparison
     */
    public int compareTo(Drug test) {
        return getName().compareTo(test.getName());
    }

    /**
     * Outputs a Drug as a string (Used by the ListAdapaters)
     * @return the String for outputing the Drug as a string
     */
    @Override
    public String toString(){
        return getName();
    }
	
}
