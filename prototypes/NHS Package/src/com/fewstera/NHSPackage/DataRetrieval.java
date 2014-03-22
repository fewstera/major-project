package com.fewstera.NHSPackage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class DataRetrieval {

	private String _accountUsername;
	private String _accountPassword;

	private static DataRetrieval instance = null;

	protected DataRetrieval() {
	}

	public static DataRetrieval getInstance() {
		if (instance == null) {
			instance = new DataRetrieval();
		}
		return instance;
	}

	public void setCredentials(String username, String password) {
		_accountUsername = username;
		_accountPassword = password;
	}

	public ArrayList<Drug> fetchIndex() throws AuthException {
		ArrayList<Drug> drugsIndex = null;

		try {
			String enocdedUsername = URLEncoder.encode(_accountUsername, "UTF-8");
			String enocdedPassword = URLEncoder.encode(_accountPassword, "UTF-8");

			String indexURL = "http://www.injguide.nhs.uk/IMGDrugIndex.asp"
					+ "?username=" + enocdedUsername + "&password="
					+ enocdedPassword;

			String indexXML = _getDataFromURL(indexURL);
			indexXML = indexXML.substring(indexXML.indexOf("<"));

			if (indexXML.contains("User not found")) {
				throw new AuthException("Invalid credentials");
			} else {
				DataIndexParser indexParser = new DataIndexParser();
				return indexParser.parse(indexXML);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return drugsIndex;
	}
	
	public ArrayList<Drug> populateLetter(char letter, ArrayList<Drug> drugList) throws AuthException {

		try {
			String enocdedUsername = URLEncoder.encode(_accountUsername, "UTF-8");
			String enocdedPassword = URLEncoder.encode(_accountPassword, "UTF-8");

			String indexURL = "http://www.injguide.nhs.uk/IMGDrugData.asp"
					+ "?username=" + enocdedUsername
					+ "&password=" + enocdedPassword
					+ "&Part=" + letter;

			String informationXML = _getDataFromURL(indexURL);
			
			//Remove characters before <?xml... due to encoding.
			informationXML = informationXML.substring(informationXML.indexOf("<"));

			if (informationXML.contains("User not found")) {
				throw new AuthException("Invalid credentials");
			} else {
				System.out.println(informationXML);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}

	private String _getDataFromURL(String url) throws IOException {
		String data = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("accept", "application/xml");
		HttpResponse response = httpClient.execute(httpGet);
		HttpEntity entity = response.getEntity();
		data = EntityUtils.toString(entity);
		return data;
	}
}
