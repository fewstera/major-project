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

    public DownloadDrugsRequest() {
        super(Drug[].class);
    }

    // can't use activity here or any non serializable field
    // will be invoked in remote service
    @Override
    public Drug[] loadDataFromNetwork() throws Exception {
        Log.d("MyApplication", "Loaded");

        ArrayList<Drug> newDrugs = new ArrayList<Drug>();

        char[] letters = "JKLMNOPQRSTUVWXYZ".toCharArray();
        for(char letter : letters) {
            Log.d("MyApplication", "Downloading letter: " + letter);
            newDrugs.addAll(fetchDrugsWithLetter(letter));
        }
        //Parse the XML into ArrayList of Drugs

        Log.d("MyApplication", "Got all drugs");

        Drug[] newDrugsArr = new Drug[newDrugs.size()];
        return newDrugs.toArray(newDrugsArr);
    }

    // Returns URL given Letter
    protected final String getUrl(char letter) {
        return "http://www.injguide.nhs.uk/IMGDrugData.asp?username=ivgdemo&password=bolus7&Part=" + letter;
    }

    private ArrayList<Drug> fetchDrugsWithLetter(char letter) throws Exception{
        ArrayList<Drug> drugs = new ArrayList<Drug>();

        //Remove null characters before XML
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputStream stream = new URL(getUrl(letter)).openStream();
            stream.skip(2);
            Document doc = dBuilder.parse(stream, CharEncoding.UTF_8);
            if(doc.getDocumentElement()!=null){
                doc.getDocumentElement().normalize();

                //Find all drug nodes and loop over them
                NodeList drugList = doc.getElementsByTagName("drug");
                for (int drugCount = 0; drugCount < drugList.getLength(); drugCount++) {
                    Drug newDrug = new Drug();

                    Element drugElement = (Element) drugList.item(drugCount);

                    int id = Integer.parseInt(drugElement.getElementsByTagName("drugno").item(0).getTextContent());
                    newDrug.setId(id);

                    //Fetch all sections of a drug and loop over them.
                    NodeList sectionList = drugElement.getElementsByTagName("section");
                    for (int sectionCount = 0; sectionCount < sectionList.getLength(); sectionCount++) {

                        //Get section element
                        Element sectionElement = (Element) sectionList.item(sectionCount);

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

                        if(headerText.equals("DRUG:")){
                            newDrug.setName(sectionText);
                        }else{
                            if(sectionText!=null){
                                newDrug.addDrugInformation(new DrugInformation(headerText, headerHelper, sectionText));
                            }
                        }
                    }
                    drugs.add(newDrug);
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

}