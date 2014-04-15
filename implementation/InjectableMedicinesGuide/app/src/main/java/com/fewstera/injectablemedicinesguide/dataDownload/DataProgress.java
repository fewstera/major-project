package com.fewstera.injectablemedicinesguide.dataDownload;

import com.fewstera.injectablemedicinesguide.models.Drug;

import java.util.ArrayList;

/**
 * Singleton class for the data download progress
 *
 * This class is used for checking the progress of requests and for displaying errors within the
 * data download activity. This is a singleton class, so only one object of this class can be
 * instantiated.
 *
 * @author Aidan Wynne Fewster
 * @version 1.0
 * @since 1.0
 */
public class DataProgress {
    private static DataProgress instance = null;

    private ArrayList<Character> _successLetters;
    private ArrayList<Drug> _drugList;
    private int _finishedCount, _indexSize;
    private boolean _calcsDownloaded, _calcsStarted, _lettersStarted, _loginError;

    /**
     *  Prevents object being instantiated
     */
    protected DataProgress() {
        reset();
    }

    /**
     * Retrieves the current instance of the class, as this is a singleton
     */
    public static DataProgress getInstance() {
        if(instance == null) {
            instance = new DataProgress();
        }
        return instance;
    }

    /**
     *  Reset all statistics about download
     */
    public void reset(){
        _successLetters = new ArrayList<Character>();
        _drugList = new ArrayList<Drug>();
        _finishedCount = _indexSize = 0;
        _calcsStarted = _calcsDownloaded = _lettersStarted = _loginError = false;
    }

    /**
     * Increase the amount of finished requests
     */
    public void increaseFinishedCount(){
        _finishedCount++;
    }

    /**
     * Decreases the amount of finished requests
     * @param x the amount to decrease by
     */
    public void decreaseFinishedCountBy(int x){
        _finishedCount = _finishedCount - x;
    }

    /**
     * Gets the amount of finished requests
     *
     * @return the amount of requests
     */
    public int getFinishedCount(){
        return _finishedCount;
    }

    /**
     * Adds a letter that has succeeded to the list
     * @param letter the letter that succeeded to be downloaded
     */
    public void addSucceededLetter(char letter){
        _successLetters.add(new Character(letter));
    }

    /**
     * Gets a list of all letters that have successfully been downloaded
     * @return the list of succeeded letters
     */
    public ArrayList<Character> getSucceededLetters(){
        return _successLetters;
    }

    /**
     * Adds a drug to drug list
     *
     * @param drug to add
     */
    public void addDrug(Drug drug){
        _drugList.add(drug);
    }

    /**
     * Get list of all drugs that have been downloaded
     * @return the list of rugs
     */
    public ArrayList<Drug> getDrugList(){
        return _drugList;
    }

    /**
     * Called when the letters download had began
     */
    public void lettersHasStarted(){
        _lettersStarted = true;
    }

    /**
     * Whether the calculator information should begin downloading
     *
     * @return true if the calculator download should begin, false otherwise
     */
    public boolean shouldStartCalcsDownload(){
        return (_indexSize>0&&!_calcsStarted);
    }

    /**
     * Whether the letters download should begin downloading
     *
     * @return true if the letters download should begin, false otherwise
     */
    public boolean shouldStartLetters(){
        return (_calcsDownloaded&&!_lettersStarted);
    }

    /**
     * Sets the indexSize (The amount of drugs within the index)
     * @param indexSize the size of the index
     */
    public void setIndexSize(int indexSize) {
        _indexSize = indexSize;
    }

    /**
     * Returns the size of the index
     *
     * @return the size of the index
     */
    public int getIndexSize() {
        return _indexSize;
    }

    /**
     * Called when the calculator information has finished downloading
     */
    public void calcsHaveDownloaded() {
        _calcsDownloaded = true;
    }

    /**
     * Called when a login error has occurred
     */
    public void loginErrorOccurred() {
        _loginError = true;
    }

    /**
     * Check if a login error has occurred
     *
     * @return true if login error has occured, false otherwise
     */
    public boolean isLoginError(){
        return _loginError;
    }
}