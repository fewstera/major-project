package com.fewstera.injectablemedicinesguide;

public class DrugInformation {
	
	private String name, information;
	
	public DrugInformation(String name, String information){
		this.name = name;
		this.information = information;
	}
	
	public String getName(){
		return this.name;
	}
	
	
	public String getInformation(){
		return this.information;
	}
}
