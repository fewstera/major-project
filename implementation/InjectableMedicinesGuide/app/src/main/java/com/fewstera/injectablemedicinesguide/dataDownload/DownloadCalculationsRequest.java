package com.fewstera.injectablemedicinesguide.dataDownload;

import android.content.Context;

import com.fewstera.injectablemedicinesguide.R;
import com.fewstera.injectablemedicinesguide.models.Drug;
import com.fewstera.injectablemedicinesguide.models.DrugCalculatorInfo;
import com.fewstera.injectablemedicinesguide.database.DatabaseHelper;
import com.octo.android.robospice.request.SpiceRequest;
import org.apache.commons.lang3.CharEncoding;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
    private String _username, _password;
    private String _url, _calculatorRepeatTag, _drugIdTag, _infusionRatelabelTag, _infusionRateUnitsTag,
            _doseUnitsTag, _patientWeightReqTag, _timeReqTag, _factorTag, _concentrationUnitsTag;

    public DownloadCalculationsRequest(Context context, String username, String password) {
        super(Void.class);

        _db = new DatabaseHelper(context);
        _dataProgress = DataProgress.getInstance();

        _username = username;
        _password = password;

        loadXMLValues(context);
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        downloadAndSaveCalcs();
        return null;
    }


    /**
     * This method loads the XML tag names and values from the dataDownload.xml resource file.
     * The values are stored in private attributes, so they can be used throughout the object.
     *
     * @param context the application context
     */
    private void loadXMLValues(Context context){
        int urlValue = R.string.calculator_url;
        _url = context.getResources().getString(urlValue);

        int calculatorRepeatTagRes = R.string.calculator_data_repeat_tag;
        _calculatorRepeatTag = context.getResources().getString(calculatorRepeatTagRes);

        int drugIdTagRes = R.string.calculator_data_drug_id_tag;
        _drugIdTag = context.getResources().getString(drugIdTagRes);

        int infusionRatelabelTagRes = R.string.calculator_infusion_rate_label_tag;
        _infusionRatelabelTag = context.getResources().getString(infusionRatelabelTagRes);

        int infusionRateUnitsTagRes = R.string.calculator_infusion_rate_units_tag;
        _infusionRateUnitsTag = context.getResources().getString(infusionRateUnitsTagRes);

        int doseUnitsTagRes = R.string.calculator_dose_units_tag;
        _doseUnitsTag = context.getResources().getString(doseUnitsTagRes);

        int patientWeightReqTagRes = R.string.calculator_weight_required_tag;
        _patientWeightReqTag = context.getResources().getString(patientWeightReqTagRes);

        int timeReqTagRes = R.string.calculator_time_required_tag;
        _timeReqTag = context.getResources().getString(timeReqTagRes);

        int factorTagRes = R.string.calculator_factor_tag;
        _factorTag = context.getResources().getString(factorTagRes);

        int concentrationUnitsTagRes = R.string.calculator_concentration_units_tag;
        _concentrationUnitsTag = context.getResources().getString(concentrationUnitsTagRes);

    }

    /**
     *  Get the URL of the calculations API
     */
    private final String getUrl() {
        String url = _url.replace("%USERNAME%", _username);
        url = url.replace("%PASSWORD%", _password);
        return url;
    }

    /**
     * Downloads and saves all calculator information's.
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
            NodeList drugCalcList = doc.getElementsByTagName(_calculatorRepeatTag);
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
        drugCalculatorInfo.setDrugId(Integer.parseInt(calcElem.getElementsByTagName(_drugIdTag).item(0).getTextContent()));
        drugCalculatorInfo.setInfusionRateLabel(calcElem.getElementsByTagName(_infusionRatelabelTag).item(0).getTextContent());
        drugCalculatorInfo.setInfusionRateUnits(calcElem.getElementsByTagName(_infusionRateUnitsTag).item(0).getTextContent());
        drugCalculatorInfo.setDoseUnits(calcElem.getElementsByTagName(_doseUnitsTag).item(0).getTextContent());
        /* Set the patient weight required value */
        boolean weightReq = Boolean.valueOf(calcElem.getElementsByTagName(_patientWeightReqTag).item(0).getTextContent());
        drugCalculatorInfo.setPatientWeightRequired(weightReq);
        /* Set the time required value */
        boolean timeReq = Boolean.valueOf(calcElem.getElementsByTagName(_timeReqTag).item(0).getTextContent());
        drugCalculatorInfo.setTimeRequired(timeReq);
        /* Set the factor for the calculation */
        String factorStr = calcElem.getElementsByTagName(_factorTag).item(0).getTextContent();
        Integer factor = (factorStr.equals("")) ? null : Integer.parseInt(factorStr);
        drugCalculatorInfo.setFactor(factor);
        /* Set the concentration units */
        String concentrationUnits = calcElem.getElementsByTagName(_concentrationUnitsTag).item(0).getTextContent();
        drugCalculatorInfo.setConcentrationUnits(concentrationUnits);

        return drugCalculatorInfo;
    }
}
