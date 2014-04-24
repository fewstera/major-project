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

    private ArrayList<String> _succeededTags;
    private ArrayList<Drug> _drugList;
    private int _finishedCount, _indexSize;
    private boolean _calcsDownloaded, _calcsStarted, _drugsDownload, _loginError;

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
        _succeededTags = new ArrayList<String>();
        _drugList = new ArrayList<Drug>();
        _finishedCount = _indexSize = 0;
        _calcsStarted = _calcsDownloaded = _drugsDownload = _loginError = false;
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
     * Adds a tag that has succeeded to the list of complete tags. A tag is a reference to the
     * an API url to download part of the drug data.
     *
     * @param tag the tag that succeeded to be downloaded
     */
    public void addSucceededTag(String tag){
        _succeededTags.add(tag);
    }

    /**
     * Gets a list of all tags that have successfully been downloaded
     * @return the list of succeeded tags
     */
    public ArrayList<String> getSucceededTags(){
        return _succeededTags;
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
     * Called when the drugs download had began
     */
    public void drugsDownloadHasStarted(){
        _drugsDownload = true;
        _finishedCount = 0;
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
     * Whether the drugs list download should begin downloading
     *
     * @return true if the drugs list download should begin, false otherwise
     */
    public boolean shouldDrugsListStart(){
        return (_calcsDownloaded&&!_drugsDownload);
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