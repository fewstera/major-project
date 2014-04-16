package com.fewstera.injectablemedicinesguide.models;
/**
 * Class that represents the model of a piece of information a drug contains
 *
 * @author Aidan Wynne Fewster
 * @version 1.0
 * @since 1.0
 */
public class DrugInformation {

    private int _id;
    private String _headerText, _headerHelper, _sectionText;

    /**
     * Constructor used when first creating a drug information (When not stored within DB)
     *
     * @param headerText the header text
     * @param headerHelper the heading help
     * @param sectionText the context of the information
     */
	public DrugInformation(String headerText, String headerHelper, String sectionText){
        _headerText = headerText;
        _headerHelper = headerHelper;
        _sectionText = sectionText;
	}

    /**
     * Constructor used when creating from the database
     *
     * @param id the id in the database
     * @param headerText the header text
     * @param headerHelper the heading help
     * @param sectionText the context of the information
     */
    public DrugInformation(int id, String headerText, String headerHelper, String sectionText){
        _id = id;
        _headerText = headerText;
        _headerHelper = headerHelper;
        _sectionText = sectionText;
	}

    /**
     * Gets the DB id
     *
     * @return the id
     */
    public int getId(){
        return _id;
    }

    /**
     * Gets the header text
     *
     * @return the header text
     */
    public String getHeaderText(){
		return _headerText;
	}

    /**
     * Gets the header help
     *
     * @return the heading help
     */
	public String getHeaderHelper(){
		return _headerHelper;
	}

    /**
     * Gets the context of the information
     *
     * @return the information
     */
	public String getSectionText(){
		return _sectionText;
	}
}
