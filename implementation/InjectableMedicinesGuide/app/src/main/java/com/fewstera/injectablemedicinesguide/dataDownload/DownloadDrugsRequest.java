package com.fewstera.injectablemedicinesguide.dataDownload;

import android.content.Context;
import android.support.v7.appcompat.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang3.CharEncoding;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fewstera.injectablemedicinesguide.models.Drug;
import com.fewstera.injectablemedicinesguide.models.DrugInformation;
import com.fewstera.injectablemedicinesguide.database.DatabaseHelper;
import com.octo.android.robospice.request.SpiceRequest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Class responsible for downloading the drugs and all drug information's.
 *
 * @author Aidan Wynne Fewster
 * @version 1.0
 * @since 1.0
 */
public class DownloadDrugsRequest extends SpiceRequest<Drug[]> {

    private DatabaseHelper _db;
    private DataProgress _dataProgress;
    private String _url, _tag;

    private String _drugRepeatTag, _drugIdTag, _drugInfoRepeatTag, _drugInfoHeaderTag,
            _drugInfoHeaderHelpTag, _drugInfoTextTag, _drugNameValue;

    public DownloadDrugsRequest(Context context, String url, String tag) {
        super(Drug[].class);
        loadXMLValues(context);
        _url = url;
        _db = new DatabaseHelper(context);
        _dataProgress = DataProgress.getInstance();

    }

    /**
     * This method loads the XML tag names and values from the dataDownload.xml resource file.
     * The values are stored in private attributes, so they can be used throughout the object.
     *
     * @param context the application context
     */
    private void loadXMLValues(Context context){
        int drugRepeatTagRes = com.fewstera.injectablemedicinesguide.R.string.drug_data_drug_repeat_tag;
        _drugRepeatTag = context.getResources().getString(drugRepeatTagRes);

        int drugIdTagRes = com.fewstera.injectablemedicinesguide.R.string.drug_data_drug_id_tag;
        _drugIdTag = context.getResources().getString(drugIdTagRes);

        int drugInfoRepeatTagRes = com.fewstera.injectablemedicinesguide.R.string.drug_data_drug_info_repeat_tag;
        _drugInfoRepeatTag = context.getResources().getString(drugInfoRepeatTagRes);

        int drugInfoHeaderTagRes = com.fewstera.injectablemedicinesguide.R.string.drug_data_drug_info_header_tag;
        _drugInfoHeaderTag = context.getResources().getString(drugInfoHeaderTagRes);

        int drugInfoHeaderHelpTagRes = com.fewstera.injectablemedicinesguide.R.string.drug_data_drug_info_header_help_tag;
        _drugInfoHeaderHelpTag = context.getResources().getString(drugInfoHeaderHelpTagRes);

        int drugInfoTextTagRes = com.fewstera.injectablemedicinesguide.R.string.drug_data_drug_info_text_tag;
        _drugInfoTextTag = context.getResources().getString(drugInfoTextTagRes);

        int drugNameValueRes = com.fewstera.injectablemedicinesguide.R.string.drug_data_drug_name;
        _drugNameValue = context.getResources().getString(drugNameValueRes);
    }

    /*.
     * Begins the request, this is called by the robospice service, when it's ready.
     *
     * @return the list of Drug's that have been downloaded
     * @throws Exception
     */
    @Override
    public Drug[] loadDataFromNetwork() throws Exception {
        ArrayList<Drug> newDrugs = fetchDrugs();
        _dataProgress.addSucceededTag(_tag);
        Drug[] newDrugsArr = new Drug[newDrugs.size()];
        return newDrugs.toArray(newDrugsArr);
    }

    /**
     * Get URL for the given letter download
     * @param letter to download
     * @return the url
     */
    private final String getUrl(char letter) {
        return _url;
    }

    /**
     * Downloads the XML from the API from the provided URL.
     *
     * @return the list of Drugs that have been downloaded
     * @throws Exception
     */
    private ArrayList<Drug> fetchDrugs() throws Exception{
        ArrayList<Drug> drugs = new ArrayList<Drug>();

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputStream stream = new URL(getUrl()).openStream();

            Document doc = dBuilder.parse(stream, CharEncoding.UTF_8);
            if(doc.getDocumentElement()!=null){
                doc.getDocumentElement().normalize();

                /* Find all drug nodes and loop over them */
                NodeList drugList = doc.getElementsByTagName("drug");
                for (int drugCount = 0; drugCount < drugList.getLength(); drugCount++) {
                    Element drugElement = (Element) drugList.item(drugCount);
                    Drug newDrug = parseDrugFromElement(drugElement);
                    /* Save drug to database */
                    _db.createDrug(newDrug);
                    /* Add to data progress singleton */
                    _dataProgress.addDrug(newDrug);
                    drugs.add(newDrug);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw(e);
        }

        return drugs;
    }

    /**
     * Retrieves complete drug from the given drug element
     *
     * @param drugElement the XML element containing the drug details
     * @return the Drug from the element
     *
     * @throws Exception
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

    /**
     * Retrieves drug information from the drugs section XML element and applies information to the drug.
     *
     * @param drug the drug to add the information to
     * @param sectionElement the XML element containing the drug information
     * @return the drug with the added drug information
     * @throws Exception
     */
    private Drug addSectionsDrugInfo(Drug drug, Element sectionElement) throws Exception{

        String headerText = null;
        NodeList headerTextNode = sectionElement.getElementsByTagName("header_text");
        if(headerTextNode.getLength()>0){
            headerText = headerTextNode.item(0).getTextContent();
        }

        String headerHelper = null;
        NodeList headingHelpNode = sectionElement.getElementsByTagName("heading_help");
        if(headingHelpNode.getLength()>0){
            headerHelper = headingHelpNode.item(0).getTextContent();
        }

        String sectionText = null;
        NodeList sectionTextNode = sectionElement.getElementsByTagName("section_text");
        if(sectionTextNode.getLength()>0){
            sectionText = sectionTextNode.item(0).getTextContent();
        }

        return addDrugInformationToDrug(drug, headerText, headerHelper, sectionText);

    }

    /**
     * Adds the information about the drug to the Drug passed in.
     *
     * @param drug the drug to add the information to
     * @param headerText the header text
     * @param headerHelper the header helper
     * @param sectionText the section text
     * @return the drug with the added drug information
     */
    private Drug addDrugInformationToDrug(Drug drug, String headerText, String headerHelper, String sectionText) {
        if(headerText.equals("DRUG:")){
            drug.setName(sectionText);
        }else if(headerText!=null&&sectionText!=null){
            drug.addDrugInformation(new DrugInformation(headerText, headerHelper, sectionText));
        }
        return drug;
    }

}