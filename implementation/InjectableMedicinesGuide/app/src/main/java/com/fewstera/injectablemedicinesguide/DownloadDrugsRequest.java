package com.fewstera.injectablemedicinesguide;

import java.io.IOException;
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

        //Download the XML from the API
        SimpleTextRequest xmlTextRequest = new SimpleTextRequest(getUrl());
        String xmlText = xmlTextRequest.loadDataFromNetwork();

        //Parse the XML into ArrayList of Drugs
        ArrayList<Drug> newDrugs = parseXML(xmlText);

        Drug[] newDrugsArr = new Drug[newDrugs.size()];
        return newDrugs.toArray(newDrugsArr);
    }

    private ArrayList<Drug> parseXML(String xmlString) throws Exception{
        ArrayList<Drug> drugs = new ArrayList<Drug>();

        //Remove null characters before XML
        xmlString = xmlString.substring(xmlString.indexOf("<"));

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(new StringReader (xmlString)));
            doc.getDocumentElement().normalize();

            NodeList drugList = doc.getElementsByTagName("drug");
            for (int drugCount = 0; drugCount < drugList.getLength(); drugCount++) {
                Drug newDrug = new Drug();
                Element drugElement = (Element) drugList.item(drugCount);

                int id = Integer.parseInt(drugElement.getElementsByTagName("drugno").item(0).getTextContent());
                newDrug.setId(id);

                NodeList sectionList = drugElement.getElementsByTagName("section");
                for (int sectionCount = 0; sectionCount < sectionList.getLength(); sectionCount++) {

                    Element sectionElement = (Element) sectionList.item(sectionCount);

                    //To fix inconsistencies in XML cases from the API
                    NodeList headerTextNode = sectionElement.getElementsByTagName("header_text");
                    if(headerTextNode.getLength()==0){
                        headerTextNode = sectionElement.getElementsByTagName("Header_text");
                    }

                    String headerText = headerTextNode.item(0).getTextContent();
                    String headerHelper = null;

                    NodeList headingHelpNode = sectionElement.getElementsByTagName("heading_help");
                    if(headerTextNode.getLength()>0){
                        headerHelper = headingHelpNode.item(0).getTextContent();
                    }

                    String sectionText = sectionElement.getElementsByTagName("Section_text").item(0).getTextContent();

                    if(headerText.equals("DRUG:")){
                        newDrug.setName(sectionText);
                    }else{
                        newDrug.addDrugInformation(new DrugInformation(headerText, headerHelper, sectionText));
                    }
                }

                drugs.add(newDrug);
            }
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