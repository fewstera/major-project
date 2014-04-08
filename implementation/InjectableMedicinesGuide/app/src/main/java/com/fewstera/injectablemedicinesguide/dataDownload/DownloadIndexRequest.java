package com.fewstera.injectablemedicinesguide.dataDownload;

import android.content.Context;

import com.fewstera.injectablemedicinesguide.Drug;
import com.fewstera.injectablemedicinesguide.DrugIndex;
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
 * Created by fewstera on 01/04/2014.
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

    // Returns the number of unique drug id's from the table
    @Override
    public Integer loadDataFromNetwork() throws Exception {
        _uniqueIds = new ArrayList<Integer>();
        return new Integer(downloadAndSaveIndex());
    }

    // Returns URL given Letter
    protected final String getUrl() {
        return "http://www.injguide.nhs.uk/IMGDrugIndex.asp?username="
                + _accountUsername + "&password=" +
                _accountPassword;
    }

    private int downloadAndSaveIndex() throws Exception{
        ArrayList<Drug> drugs = new ArrayList<Drug>();

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputStream stream = new URL(getUrl()).openStream();

            Document doc = dBuilder.parse(stream, CharEncoding.UTF_8);
            doc.getDocumentElement().normalize();

            //Find all drug nodes and loop over them
            NodeList drugIndexList = doc.getElementsByTagName("drug_index_line");
            for (int indexCount = 0; indexCount < drugIndexList.getLength(); indexCount++) {
                Element indexElement = (Element) drugIndexList.item(indexCount);
                DrugIndex newIndex = parseIndexFromElement(indexElement);
                _db.createDrugIndex(newIndex);
                Integer testUniqueId = new Integer(newIndex.getDrugId());
                if(!_uniqueIds.contains(testUniqueId)){
                    _uniqueIds.add(testUniqueId);
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
        _dataProgress.setIndexSize(_uniqueIds.size());
        return _uniqueIds.size();
    }

    /*
        *  Retrieves drug index from the element
        *  Returns: DrugIndex containing the info
    */
    private DrugIndex parseIndexFromElement(Element drugElement) throws Exception{
        int id = Integer.parseInt(drugElement.getElementsByTagName("drugno").item(0).getTextContent());
        String name = drugElement.getElementsByTagName("drugname").item(0).getTextContent();

        return new DrugIndex(id, name);
    }
}
