package com.fewstera.injectablemedicinesguide.models;

/**
 * Class that represents the model of a DrugIndex
 *
 * @author Aidan Wynne Fewster
 * @version 1.0
 * @since 1.0
 */
public class DrugIndex {

    private int _drugId;
    private String _name;

    public DrugIndex(int drugId, String name){
        _drugId = drugId;
        _name = name;
    }

    /**
     * Get the id of the drug that this Index belongs to
     * @return the id
     */
    public int getDrugId() {
        return _drugId;
    }

    /**
     * Get the name of this index
     * @return the name
     */
    public String getName() {
        return _name;
    }

    /**
     * Converts the DrugIndex to string (used by ListAdapter)
     * @return the drugs name
     */
    @Override
    public String toString(){
        return getName();
    }
}
