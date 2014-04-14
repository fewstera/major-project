package com.fewstera.injectablemedicinesguide.dataDownload;

import com.fewstera.injectablemedicinesguide.models.Drug;

import java.util.ArrayList;

/**
 * Created by fewstera on 24/03/2014.
 */
public class DataProgress {
    private static DataProgress instance = null;

    private ArrayList<Character> _successLetters;
    private ArrayList<Drug> _drugList;
    private int _finishedCount, _indexSize;
    private boolean _calcsDownloaded, _calcsStarted, _lettersStarted, _loginError;

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
        _finishedCount = _indexSize = 0;
        _calcsStarted = _calcsDownloaded = _lettersStarted = _loginError = false;
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

    public void lettersHasStarted(){
        _lettersStarted = true;
    }

    public boolean shouldStartCalcsDownload(){
        return (_indexSize>0&&!_calcsStarted);
    }

    public boolean shouldStartLetters(){
        return (_calcsDownloaded&&!_lettersStarted);
    }

    public void setIndexSize(int indexSize) {
        _indexSize = indexSize;
    }

    public int getIndexSize() {
        return _indexSize;
    }

    public void calcsHaveDownloaded() {
        _calcsDownloaded = true;
    }

    public void loginErrorOccured() {
        _loginError = true;
    }

    public boolean isLoginError(){
        return _loginError;
    }
}