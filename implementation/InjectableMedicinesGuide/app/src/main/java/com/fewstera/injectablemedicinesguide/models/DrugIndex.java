package com.fewstera.injectablemedicinesguide.models;

/**
 * Created by fewstera on 01/04/2014.
 */
public class DrugIndex {

    private long _dbId;
    private int _drugId;
    private String _name;

    public DrugIndex(long dbId, int drugId, String name){
        _dbId = dbId;
        _drugId = drugId;
        _name = name;
    }
    public DrugIndex(int drugId, String name){
        _drugId = drugId;
        _name = name;
    }

    public long getDbId() {
        return _dbId;
    }

    public int getDrugId() {
        return _drugId;
    }

    public String getName() {
        return _name;
    }

    @Override
    public String toString(){
        return getName();
    }
}
