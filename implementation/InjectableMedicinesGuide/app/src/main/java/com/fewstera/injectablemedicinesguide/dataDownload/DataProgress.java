package com.fewstera.injectablemedicinesguide.dataDownload;

import com.fewstera.injectablemedicinesguide.Drug;

import java.util.ArrayList;

/**
 * Created by fewstera on 24/03/2014.
 */
public class DataProgress {
    private static DataProgress instance = null;

    private ArrayList<Character> _successLetters;
    private ArrayList<Drug> _drugList;
    private int _finishedCount;

    protected DataProgress() {
        // Prevents object being instantiated
        reset();
    }
    public static DataProgress getInstance() {
        if(instance == null) {
            instance = new DataProgress();
        }
        return instance;
    }

    public void reset(){
        _successLetters = new ArrayList<Character>();
        _drugList = new ArrayList<Drug>();
        _finishedCount = 0;
    }

    public void increaseFinishedCount(){
        _finishedCount++;
    }

    public void decreaseFinishedCountBy(int x){
        _finishedCount = _finishedCount - x;
    }

    public int getFinishedCount(){
        return _finishedCount;
    }

    public void addSucceededLetter(char letter){
        _successLetters.add(new Character(letter));
    }

    public ArrayList<Character> getSucceededLetters(){
        return _successLetters;
    }

    public void addDrug(Drug drug){
        _drugList.add(drug);
    }

    public ArrayList<Drug> getDrugList(){
        return _drugList;
    }
}