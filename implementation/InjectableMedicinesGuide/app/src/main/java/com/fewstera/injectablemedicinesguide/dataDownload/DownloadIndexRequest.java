package com.fewstera.injectablemedicinesguide.dataDownload;

import android.content.Context;

import com.fewstera.injectablemedicinesguide.models.Drug;
import com.fewstera.injectablemedicinesguide.models.DrugIndex;
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
 * Class responsible for downloading the drugs and all drug information's.
 *
 * @author Aidan Wynne Fewster
 * @version 1.0
 * @since 1.0
 */
public class DownloadIndexRequest extends SpiceRequest<Integer> {

    private DatabaseHelper _db;
    private DataProgress _dataProgress;
    private String _accountUsername;
    private String _accountPassword;

    private ArrayList<Integer> _uniqueIds;

    public DownloadIndexRequest(Context context, String username, String password) {
        super(int.class);
        _accountUsername = username;
        _accountPassword = password;

        _db = new DatabaseHelper(context);
        _dataProgress = DataProgress.getInstance();
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
        return "http://www.injguide.nhs.uk/IMGDrugIndex.asp?username="
                + _accountUsername + "&password=" +
                _accountPassword;
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

            if(doc.getElementsByTagName("LoginError").getLength()>0){
                _dataProgress.loginErrorOccurred();
                return -1;
            }

            /* Find all drug index nodes and loop over them */
            NodeList drugIndexList = doc.getElementsByTagName("drug_index_line");
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
     * @param drugElement
     * @return the DrugIndex built from the XML elements.
     * @throws Exception
     */
    private DrugIndex parseIndexFromElement(Element drugElement) throws Exception{
        int id = Integer.parseInt(drugElement.getElementsByTagName("drugno").item(0).getTextContent());
        String name = drugElement.getElementsByTagName("drugname").item(0).getTextContent();

        return new DrugIndex(id, name);
    }
}
