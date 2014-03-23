package com.fewstera.injectablemedicinesguide;

public class DrugInformation {
	
	private String _headerText, _headerHelper, _sectionText;
	
	public DrugInformation(String headerText, String headerHelper, String sectionText){
        _headerText = headerText;
        _headerHelper = headerHelper;
        _sectionText = sectionText;
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
