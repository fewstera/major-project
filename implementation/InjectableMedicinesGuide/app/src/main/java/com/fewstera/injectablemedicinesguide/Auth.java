package com.fewstera.injectablemedicinesguide;

import android.content.Context;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.lang3.CharEncoding;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Auth {

	private String _accountUsername;
	private String _accountPassword;

	public void setCredentials(String username, String password) {
		_accountUsername = username;
		_accountPassword = password;
	}

    public boolean isValid() {
        try {
            String encodedUsername = URLEncoder.encode(_accountUsername, "UTF-8");
            String encodedPassword = URLEncoder.encode(_accountPassword, "UTF-8");

            String indexURL = "http://www.injguide.nhs.uk/IMGLogin.asp"
                    + "?username=" + encodedUsername + "&password="
                    + encodedPassword;

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputStream stream = new URL(indexURL).openStream();

            Document doc = dBuilder.parse(stream, CharEncoding.UTF_8);

            //Grabs the login result
            String loginResult = doc.getElementsByTagName("LoginResult").item(0).getTextContent();

            return (loginResult.equals("true"));

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void saveCredentials(Context context) {
        Preferences.setString(context, Preferences.USERNAME_KEY, _accountUsername);
        Preferences.setString(context, Preferences.PASSWORD_KEY, _accountPassword);
    }

    public static boolean isLogged(Context context){
        //Check if the username and password are not NULL.
        return (getSavedUsername(context)!=null)&&(getSavedPassword(context)!=null);
    }

    public static String getSavedUsername(Context context){
        return Preferences.getString(context, Preferences.USERNAME_KEY);
    }

    public static String getSavedPassword(Context context){
        return Preferences.getString(context, Preferences.PASSWORD_KEY);
    }

    public static void logout(Context context) {
        Preferences.delete(context, Preferences.USERNAME_KEY);
        Preferences.delete(context, Preferences.PASSWORD_KEY);
    }
}
