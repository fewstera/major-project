package com.fewstera.NHSPackage;

import java.io.IOException;

import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DataIndexParser {
	
	public ArrayList<Drug> parse(String inputXML) {
		try {
			ArrayList<Drug> drugIndex = new ArrayList<Drug>();
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new InputSource(new StringReader (inputXML)));
			doc.getDocumentElement().normalize();
			NodeList drugList = doc.getElementsByTagName("drug_index_line");
			for (int temp = 0; temp < drugList.getLength(); temp++) {
				Drug newDrug = new Drug();
				Element drugElement = (Element) drugList.item(temp);
				
				int id = Integer.parseInt(drugElement.getElementsByTagName("drugno").item(0).getTextContent());
				String name = drugElement.getElementsByTagName("drugname").item(0).getTextContent();
				
				newDrug.setId(id);
				newDrug.setName(name);
				
				drugIndex.add(newDrug);
			}
			
			return drugIndex;
			
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		return null;
	}
	
}
