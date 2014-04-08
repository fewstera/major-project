package com.fewstera.injectablemedicinesguide.dataDownload;

import android.content.Context;

import com.fewstera.injectablemedicinesguide.Drug;
import com.fewstera.injectablemedicinesguide.DrugCalculatorInfo;
import com.fewstera.injectablemedicinesguide.database.DatabaseHelper;
import com.octo.android.robospice.request.SpiceRequest;

import org.apache.commons.lang3.CharEncoding;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by fewstera on 01/04/2014.
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

    // Returns the number of unique drug id's from the table
    @Override
    public Void loadDataFromNetwork() throws Exception {
        downloadAndSaveCalcs();
        return null;
    }

    // Returns URL given Letter
    protected final String getUrl() {
        return "http://www.injguide.nhs.uk/IMGDrugIndex.asp?username="
                + _accountUsername + "&password=" +
                _accountPassword;
    }

    private void downloadAndSaveCalcs() throws Exception{
        ArrayList<Drug> drugs = new ArrayList<Drug>();

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputStream stream = _context.getAssets().open("calcs.xml");
            //InputStream stream = new URL(getUrl()).openStream();

            Document doc = dBuilder.parse(stream, CharEncoding.UTF_8);
            doc.getDocumentElement().normalize();

            //Find all drug nodes and loop over them
            NodeList drugCalcList = doc.getElementsByTagName("DrugCalculation");
            for (int indexCount = 0; indexCount < drugCalcList.getLength(); indexCount++) {
                Element calcInfoElement = (Element) drugCalcList.item(indexCount);
                DrugCalculatorInfo newDrugCalculatorInfo = parseCalcInfoFromElement(calcInfoElement);
                _db.createDrugCalcInfo(newDrugCalculatorInfo);
            }
            _dataProgress.calcsHaveDownloaded();

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
    }

    /*
        *  Retrieves drug index from the element
        *  Returns: DrugIndex containing the info
    */
    private DrugCalculatorInfo parseCalcInfoFromElement(Element calcElem) throws Exception{
        DrugCalculatorInfo drugCalculatorInfo = new DrugCalculatorInfo();
        drugCalculatorInfo.setDrugId(Integer.parseInt(calcElem.getElementsByTagName("DrugNo").item(0).getTextContent()));
        drugCalculatorInfo.setInfusionRateLabel(calcElem.getElementsByTagName("InfusionRateLabel").item(0).getTextContent());
        drugCalculatorInfo.setInfusionRateUnits(calcElem.getElementsByTagName("InfusionRateUnits").item(0).getTextContent());
        drugCalculatorInfo.setDoseUnits(calcElem.getElementsByTagName("DoseUnits").item(0).getTextContent());

        boolean weightReq = Boolean.valueOf(calcElem.getElementsByTagName("PatientWeightRequired").item(0).getTextContent());
        drugCalculatorInfo.setPatientWeightRequired(weightReq);

        boolean timeReq = Boolean.valueOf(calcElem.getElementsByTagName("TimeRequired").item(0).getTextContent());
        drugCalculatorInfo.setTimeRequired(timeReq);

        String factorStr = calcElem.getElementsByTagName("Factor").item(0).getTextContent();
        Integer factor = (factorStr.equals("")) ? null : Integer.parseInt(factorStr);
        drugCalculatorInfo.setFactor(factor);

        String concentrationUnits = calcElem.getElementsByTagName("ConcentrationUnits").item(0).getTextContent();
        drugCalculatorInfo.setConcentrationUnits(concentrationUnits);


        return drugCalculatorInfo;
    }
}
