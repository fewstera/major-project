package com.fewstera.injectablemedicinesguide.dataDownload;

import android.content.Context;

import com.fewstera.injectablemedicinesguide.R;
import com.fewstera.injectablemedicinesguide.models.DrugIndex;
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
 * Class responsible for downloading the drugs and all drug information's.
 *
 * @author Aidan Wynne Fewster
 * @version 1.0
 * @since 1.0
 */
public class DownloadIndexRequest extends SpiceRequest<Integer> {

    private DatabaseHelper _db;
    private DataProgress _dataProgress;
    private String _username, _password;
    private String _url, _loginErrorTag, _indexRepeatTag, _drugIdTag, _indexNameTag;
    private ArrayList<Integer> _uniqueIds;

    public DownloadIndexRequest(Context context, String username, String password) {
        super(int.class);

        _username = username;
        _password = password;

        _db = new DatabaseHelper(context);
        _dataProgress = DataProgress.getInstance();

        loadXMLValues(context);
    }

    /**
     * This method loads the XML tag names and values from the dataDownload.xml resource file.
     * The values are stored in private attributes, so they can be used throughout the object.
     *
     * @param context the application context
     */
    private void loadXMLValues(Context context){
        int urlValue = R.string.index_url;
        _url = context.getResources().getString(urlValue);

        int indexRepeatTagRes = R.string.index_data_repeat_tag;
        _indexRepeatTag = context.getResources().getString(indexRepeatTagRes);

        int drugIdTagRes = R.string.index_data_drug_id_tag;
        _drugIdTag = context.getResources().getString(drugIdTagRes);

        int indexNameTagRes = R.string.index_data_index_name_tag;
        _indexNameTag = context.getResources().getString(indexNameTagRes);

        int loginErrorTagRes = R.string.data_login_error_tag;
        _loginErrorTag = context.getResources().getString(loginErrorTagRes);
    }

    /**
     * Adds the encoded username and password to the provided URL
     *
     * @param url the url to format
     * @return the formatted url
     */
    String formatApiUrl(String url){

        return url;
    }

    /**
     * Begins the request, this is called by the robospice service, when it's ready.
     *
     * @return the number of unique indexes within the database
     * @throws Exception
     */    @Override
    public Integer loadDataFromNetwork() throws Exception {
        _uniqueIds = new ArrayList<Integer>();
        return new Integer(downloadAndSaveIndex());
    }

    /**
     * Get URL for the drug indexes XML API
     * @return the url
     */
    private final String getUrl() {
        String url = _url.replace("%USERNAME%", _username);
        url = url.replace("%PASSWORD%", _password);
        return url;
    }

    /**
     * Downloads the drug index XML and populates the database with the information
     *
     * @return the number of unique indexes within the database
     * @throws Exception
     */
    private int downloadAndSaveIndex() throws Exception{
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputStream stream = new URL(getUrl()).openStream();

            Document doc = dBuilder.parse(stream, CharEncoding.UTF_8);
            doc.getDocumentElement().normalize();

            if(doc.getElementsByTagName(_loginErrorTag).getLength()>0){
                _dataProgress.loginErrorOccurred();
                return -1;
            }

            /* Find all drug index nodes and loop over them */
            NodeList drugIndexList = doc.getElementsByTagName(_indexRepeatTag);
            for (int indexCount = 0; indexCount < drugIndexList.getLength(); indexCount++) {
                Element indexElement = (Element) drugIndexList.item(indexCount);
                DrugIndex newIndex = parseIndexFromElement(indexElement);
                /* Save new index to local database */
                _db.createDrugIndex(newIndex);

                /* If the drugno is unique add it to the list of unqiue id's */
                Integer testUniqueId = new Integer(newIndex.getDrugId());
                if(!_uniqueIds.contains(testUniqueId)){
                    _uniqueIds.add(testUniqueId);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw(e);
        }
        _dataProgress.setIndexSize(_uniqueIds.size());
        /* return the amount of unique id's */
        return _uniqueIds.size();
    }

    /**
     * Retrieves drug index from the XML element
     *
     * @param drugElement
     * @return the DrugIndex built from the XML elements.
     * @throws Exception
     */
    private DrugIndex parseIndexFromElement(Element drugElement) throws Exception{
        int id = Integer.parseInt(drugElement.getElementsByTagName(_drugIdTag).item(0).getTextContent());
        String name = drugElement.getElementsByTagName(_indexNameTag).item(0).getTextContent();

        return new DrugIndex(id, name);
    }
}
