package com.fewstera.injectablemedicinesguide.dataDownload;

import android.content.Context;

import com.fewstera.injectablemedicinesguide.models.Drug;
import com.fewstera.injectablemedicinesguide.models.DrugCalculatorInfo;
import com.fewstera.injectablemedicinesguide.database.DatabaseHelper;
import com.octo.android.robospice.request.SpiceRequest;

import org.apache.commons.lang3.CharEncoding;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Class responsible for downloading the drug calculator information.
 *
 * @author Aidan Wynne Fewster
 * @version 1.0
 * @since 1.0
 */
public class DownloadCalculationsRequest extends SpiceRequest<Void> {

    private DatabaseHelper _db;
    private DataProgress _dataProgress;
    private Context _context;
    private String _accountUsername;
    private String _accountPassword;

    private ArrayList<Integer> _uniqueIds;

    public DownloadCalculationsRequest(Context context, String username, String password) {
        super(Void.class);
        _accountUsername = username;
        _accountPassword = password;
        _context = context;

        _db = new DatabaseHelper(context);
        _dataProgress = DataProgress.getInstance();
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        downloadAndSaveCalcs();
        return null;
    }

    /**
     *  Get the URL of the calculations API
     */
    private final String getUrl() {
        return "http://www.injguide.nhs.uk/IMGPopupdata.asp?username="
                + _accountUsername + "&password=" +
                _accountPassword;
    }

    /**
     * Downloads and saves all calculator informations.
     *
     * @throws Exception
     */
    private void downloadAndSaveCalcs() throws Exception{
        ArrayList<Drug> drugs = new ArrayList<Drug>();

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputStream stream = new URL(getUrl()).openStream();

            Document doc = dBuilder.parse(stream, CharEncoding.UTF_8);
            doc.getDocumentElement().normalize();

            /* Find all drug calculator nodes and loop over them */
            NodeList drugCalcList = doc.getElementsByTagName("DrugCalculation");
            for (int indexCount = 0; indexCount < drugCalcList.getLength(); indexCount++) {
                Element calcInfoElement = (Element) drugCalcList.item(indexCount);
                DrugCalculatorInfo newDrugCalculatorInfo = parseCalcInfoFromElement(calcInfoElement);
                /* Save to the database */
                _db.createDrugCalcInfo(newDrugCalculatorInfo);
            }
            _dataProgress.calcsHaveDownloaded();
        } catch (Exception e) {
            e.printStackTrace();
            throw(e);
        }
    }


    /**
     * Builds drug calculator information from the element
     *
     * @param calcElem the element of the DrugCalculation
     * @return DrugCalculatorInfo containing the information
     * @throws Exception
     */
    private DrugCalculatorInfo parseCalcInfoFromElement(Element calcElem) throws Exception{
        /* Init, and set values */
        DrugCalculatorInfo drugCalculatorInfo = new DrugCalculatorInfo();
        drugCalculatorInfo.setDrugId(Integer.parseInt(calcElem.getElementsByTagName("DrugNo").item(0).getTextContent()));
        drugCalculatorInfo.setInfusionRateLabel(calcElem.getElementsByTagName("InfusionRateLabel").item(0).getTextContent());
        drugCalculatorInfo.setInfusionRateUnits(calcElem.getElementsByTagName("InfusionRateUnits").item(0).getTextContent());
        drugCalculatorInfo.setDoseUnits(calcElem.getElementsByTagName("DoseUnits").item(0).getTextContent());
        /* Set the patient weight required value */
        boolean weightReq = Boolean.valueOf(calcElem.getElementsByTagName("PatientWeightRequired").item(0).getTextContent());
        drugCalculatorInfo.setPatientWeightRequired(weightReq);
        /* Set the time required value */
        boolean timeReq = Boolean.valueOf(calcElem.getElementsByTagName("TimeRequired").item(0).getTextContent());
        drugCalculatorInfo.setTimeRequired(timeReq);
        /* Set the factor for the calculation */
        String factorStr = calcElem.getElementsByTagName("Factor").item(0).getTextContent();
        Integer factor = (factorStr.equals("")) ? null : Integer.parseInt(factorStr);
        drugCalculatorInfo.setFactor(factor);
        /* Set the concentration units */
        String concentrationUnits = calcElem.getElementsByTagName("ConcentrationUnits").item(0).getTextContent();
        drugCalculatorInfo.setConcentrationUnits(concentrationUnits);

        return drugCalculatorInfo;
    }
}
