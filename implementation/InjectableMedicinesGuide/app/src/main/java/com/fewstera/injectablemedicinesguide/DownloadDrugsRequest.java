package com.fewstera.injectablemedicinesguide;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

    private char _letter;

    public DownloadDrugsRequest(char letter) {
        super(Drug[].class);
        _letter = letter;
    }

    // can't use activity here or any non serializable field
    // will be invoked in remote service
    @Override
    public Drug[] loadDataFromNetwork() throws Exception {
        Log.d("MyApplication", "Loaded " + _letter);
        ArrayList<Drug> newDrugs = fetchDrugsWithLetter(_letter);
        Drug[] newDrugsArr = new Drug[newDrugs.size()];
        return newDrugs.toArray(newDrugsArr);
    }

    // Returns URL given Letter
    protected final String getUrl(char letter) {
        return "http://www.injguide.nhs.uk/IMGDrugData.asp?username=ivgdemo&password=bolus7&Part=" + letter;
    }

    private ArrayList<Drug> fetchDrugsWithLetter(char letter) throws Exception{
        ArrayList<Drug> drugs = new ArrayList<Drug>();

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputStream stream = new URL(getUrl(letter)).openStream();

            //Skips the first two bytes of the input stream as the XML contains whitespace.
            stream.skip(2);

            Document doc = dBuilder.parse(stream, CharEncoding.UTF_8);
            if(doc.getDocumentElement()!=null){
                doc.getDocumentElement().normalize();

                //Find all drug nodes and loop over them
                NodeList drugList = doc.getElementsByTagName("drug");
                for (int drugCount = 0; drugCount < drugList.getLength(); drugCount++) {
                    Element drugElement = (Element) drugList.item(drugCount);
                    drugs.add(parseDrugFromElement(drugElement));
                }
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

    /*
        *  Retrieves complete drug from the given drug element
        *  Returns: drug containing all information
    */
    private Drug parseDrugFromElement(Element drugElement) throws Exception{
        Drug newDrug = new Drug();

        int id = Integer.parseInt(drugElement.getElementsByTagName("drugno").item(0).getTextContent());
        newDrug.setId(id);

        //Fetch all sections of a drug and loop over them.
        NodeList sectionList = drugElement.getElementsByTagName("section");
        for (int sectionCount = 0; sectionCount < sectionList.getLength(); sectionCount++) {
            //Get section element
            Element sectionElement = (Element) sectionList.item(sectionCount);
            newDrug = addSectionsDrugInfo(newDrug, sectionElement);
        }
        return newDrug;
    }

    /*
        *  Retrieves drug information from the drugs section element and applies information to the drug/
        *  Returns: the drug passed in, with the information applied
    */
    private Drug addSectionsDrugInfo(Drug drug, Element sectionElement) throws Exception{
        //To fix inconsistencies in XML cases from the API
        NodeList headerTextNode = sectionElement.getElementsByTagName("header_text");
        if(headerTextNode.getLength()==0){
            headerTextNode = sectionElement.getElementsByTagName("Header_text");
        }
        String headerText = headerTextNode.item(0).getTextContent();

        String headerHelper = null;
        NodeList headingHelpNode = sectionElement.getElementsByTagName("heading_help");
        if(headingHelpNode.getLength()>0){
            headerHelper = headingHelpNode.item(0).getTextContent();
        }


        String sectionText = null;
        NodeList sectionTextNode = sectionElement.getElementsByTagName("Section_text");
        if(sectionTextNode.getLength()>0){
            sectionText = sectionTextNode.item(0).getTextContent();
        }

        return addDrugInformationToDrug(drug, headerText, headerHelper, sectionText);
    }

    /*
        *  Adds the information about the drug to the Drug passed in.
        *  Returns: the drug passed in, with the information applied
     */
    private Drug addDrugInformationToDrug(Drug drug, String headerText, String headerHelper, String sectionText) {
        if(headerText.equals("DRUG:")){
            drug.setName(sectionText);

        }else if(headerText.equals("ROUTE:")){
            drug.setRoute(sectionText);

        }else if(headerText.equals("MEDICINE NAME:")){
            drug.setMedicineName(sectionText);

        }else if(headerText.equals("TRADE NAME(S):")){
            drug.setTradeName(sectionText);

        }else if(headerText.equals("VERSION:")){
            drug.setVersion(sectionText);

        }else if(headerText.equals("DATE PUBLISHED:")){
            SimpleDateFormat  format = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date date = format.parse(sectionText);
                drug.setDatePublished(date);
            } catch (ParseException e) {
                //If date is invalid, date will be set to null and not displayed.
            }

        }else if(sectionText!=null){
            drug.addDrugInformation(new DrugInformation(headerText, headerHelper, sectionText));
        }
        return drug;
    }

}