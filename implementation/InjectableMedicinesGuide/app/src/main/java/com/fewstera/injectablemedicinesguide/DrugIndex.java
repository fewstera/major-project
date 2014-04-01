package com.fewstera.injectablemedicinesguide;

/**
 * Created by fewstera on 01/04/2014.
 */
public class DrugIndex {

    private int _drugId;
    private String _name;

    public DrugIndex(int drugId, String name){
        _drugId = drugId;
        _name = name;
    }

    public int getDrugId() {
        return _drugId;
    }

    public String getName() {
        return _name;
    }
}
