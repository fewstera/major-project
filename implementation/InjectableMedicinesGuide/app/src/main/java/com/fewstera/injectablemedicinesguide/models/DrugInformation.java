package com.fewstera.injectablemedicinesguide.models;

public class DrugInformation {

    private int _id;
    private String _headerText, _headerHelper, _sectionText;

	public DrugInformation(String headerText, String headerHelper, String sectionText){
        _headerText = headerText;
        _headerHelper = headerHelper;
        _sectionText = sectionText;
	}

    public DrugInformation(int id, String headerText, String headerHelper, String sectionText){
        _id = id;
        _headerText = headerText;
        _headerHelper = headerHelper;
        _sectionText = sectionText;
	}

    public int getId(){
        return _id;
    }

    public String getHeaderText(){
		return _headerText;
	}
	
	public String getHeaderHelper(){
		return _headerHelper;
	}

	public String getSectionText(){
		return _sectionText;
	}
}
