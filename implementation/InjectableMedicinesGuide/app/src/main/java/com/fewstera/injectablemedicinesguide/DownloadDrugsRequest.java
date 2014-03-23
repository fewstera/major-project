package com.fewstera.injectablemedicinesguide;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharEncoding;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import roboguice.util.temp.Ln;

import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.simple.SimpleTextRequest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by fewstera on 23/03/2014.
 */
public class DownloadDrugsRequest extends SpiceRequest<Drug[]> {

    private final String url;

    public DownloadDrugsRequest(final String url) {
        super(Drug[].class);
        this.url = url;
    }

    // can't use activity here or any non serializable field
    // will be invoked in remote service
    @Override
    public Drug[] loadDataFromNetwork() throws Exception {
        Log.d("MyApplication", "Loaded");


        //Download the XML from the API
        //SimpleTextRequest xmlTextRequest = new SimpleTextRequest(getUrl());
        //String xmlText = xmlTextRequest.loadDataFromNetwork();

        Log.d("MyApplication", "Fetched XML");

        //Parse the XML into ArrayList of Drugs
        ArrayList<Drug> newDrugs = parseXML();
        Log.d("MyApplication", "Got all drugs");

        Drug[] newDrugsArr = new Drug[newDrugs.size()];
        return newDrugs.toArray(newDrugsArr);
    }

    private ArrayList<Drug> parseXML() throws Exception{
        ArrayList<Drug> drugs = new ArrayList<Drug>();

        //Remove null characters before XML
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Log.d("MyApplication", "Starting to parse XML");
            InputStream stream = new URL(getUrl()).openStream();
            stream.skip(2);
            Document doc = dBuilder.parse(stream, CharEncoding.UTF_8);
            Log.d("MyApplication", "NOrmalise");
            doc.getDocumentElement().normalize();

            Log.d("MyApplication", "PARSED");

            NodeList drugList = doc.getElementsByTagName("drug");
            for (int drugCount = 0; drugCount < drugList.getLength(); drugCount++) {
                Log.d("MyApplication", "GOT TO LOOP");
                Drug newDrug = new Drug();
                Element drugElement = (Element) drugList.item(drugCount);

                int id = Integer.parseInt(drugElement.getElementsByTagName("drugno").item(0).getTextContent());
                newDrug.setId(id);

                Log.d("MyApplication", "GOT ID:" + id);

                NodeList sectionList = drugElement.getElementsByTagName("section");
                for (int sectionCount = 0; sectionCount < sectionList.getLength(); sectionCount++) {

                    Log.d("MyApplication", "GOT TO LOOP 2");

                    Element sectionElement = (Element) sectionList.item(sectionCount);
                    Log.d("MyApplication", "1");
                    //To fix inconsistencies in XML cases from the API
                    NodeList headerTextNode = sectionElement.getElementsByTagName("header_text");
                    if(headerTextNode.getLength()==0){
                        headerTextNode = sectionElement.getElementsByTagName("Header_text");
                    }
                    Log.d("MyApplication", "2");

                    String headerText = headerTextNode.item(0).getTextContent();
                    Log.d("MyApplication", "Header: " + headerText);
                    Log.d("MyApplication", "3");
                    String headerHelper = null;
                    Log.d("MyApplication", "4");
                    NodeList headingHelpNode = sectionElement.getElementsByTagName("heading_help");
                    Log.d("MyApplication", "5");
                    if(headingHelpNode.getLength()>0){
                        headerHelper = headingHelpNode.item(0).getTextContent();
                        Log.d("MyApplication", "Header_helper: " + headerHelper);
                    }



                    Log.d("MyApplication", "6");

                    String sectionText = null;
                    Log.d("MyApplication", "4");
                    NodeList sectionTextNode = sectionElement.getElementsByTagName("Section_text");
                    Log.d("MyApplication", "5");
                    if(sectionTextNode.getLength()>0){
                        sectionText = sectionTextNode.item(0).getTextContent();
                        Log.d("MyApplication", "Header_helper: " + headerHelper);
                    }

                    Log.d("MyApplication", "sectionText: " + sectionText);
                    if(headerText.equals("DRUG:")){
                        newDrug.setName(sectionText);
                        Log.d("MyApplication", "New drug: " + sectionText);
                    }else{
                        Log.d("MyApplication", "Added drug info: " + headerText);
                        if(sectionText!=null){
                            newDrug.addDrugInformation(new DrugInformation(headerText, headerHelper, sectionText));
                        }
                    }
                }
                Log.d("MyApplication", "Adding new drug");
                drugs.add(newDrug);
            }
            Log.d("MyApplication", "Finished for loop");
        } catch (SAXException e) {
            e.printStackTrace();
            throw(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw(e);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            throw(e);
        }

        return drugs;
    }

    // can't use activity here or any non serializable field
    // will be invoked in remote service
    protected final String getUrl() {
        return this.url;
    }

}