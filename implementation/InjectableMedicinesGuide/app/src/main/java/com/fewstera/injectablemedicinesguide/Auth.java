package com.fewstera.injectablemedicinesguide;

import android.content.Context;

import org.apache.commons.lang3.CharEncoding;
import org.w3c.dom.Document;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Class for handling authentication
 *
 * This class is used for validating user credentials with the NHS server. This class
 * is also used for saving, retrieving and deleting users credentials.
 *
 * @author Aidan Wynne Fewster
 * @version 1.0
 * @since 1.0
 */
public class Auth {

	private String _accountUsername;
	private String _accountPassword;

    /**
     * Sets the credentials to be used for authenticating
     *
     * @param username of the user.
     * @param password of the user.
     */
	public void setCredentials(String username, String password) {
		_accountUsername = username;
		_accountPassword = password;
	}

    /**
     * Checks if the credentials set are correct.
     *
     * Returns true if correct, false if incorrect and null if there was a connection error
     *
     * @return      the result of the validation check
     */
    public Boolean isValid(Context context) {
        try {

            String indexURL = context.getResources().getString(R.string.login_url);

            /* Substitute the username and password values into the url  */
            indexURL = indexURL.replace("%USERNAME%", _accountUsername).replace("%PASSWORD%", _accountPassword);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputStream stream = new URL(indexURL).openStream();

            Document doc = dBuilder.parse(stream, CharEncoding.UTF_8);

            String loginResultTag = context.getResources().getString(R.string.login_data_result_tag);

            //Grabs the login result
            String loginResult = doc.getElementsByTagName(loginResultTag).item(0).getTextContent();

            return new Boolean(Boolean.valueOf(loginResult));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Saves the credentials of the user into Preferences.
     *
     * @param context the context of the activity
     */
    public void saveCredentials(Context context) {
        Preferences.setString(context, Preferences.USERNAME_KEY, _accountUsername);
        Preferences.setString(context, Preferences.PASSWORD_KEY, _accountPassword);
    }

    /**
     * Checks if the user is currently logged in
     *
     * @param context the context of the activity
     * @return true if the user is logged in, false otherwise
     */
    public static boolean isLogged(Context context){
        //Check if the username and password are not NULL.
        return (getSavedUsername(context)!=null)&&(getSavedPassword(context)!=null);
    }

    /**
     * Gets the username saved within preferences
     *
     * @param context the context of the activity
     * @return the saved username
     */
    public static String getSavedUsername(Context context){
        return Preferences.getString(context, Preferences.USERNAME_KEY);
    }

    /**
     * Gets the password saved within preferences
     *
     * @param context the context of the activity
     * @return the saved password
     */
    public static String getSavedPassword(Context context){
        return Preferences.getString(context, Preferences.PASSWORD_KEY);
    }

    /**
     * Logs the user out by deleting there saved username and password
     *
     * @param context the context of the activity
     */
    public static void logout(Context context) {
        Preferences.delete(context, Preferences.USERNAME_KEY);
        Preferences.delete(context, Preferences.PASSWORD_KEY);
    }

    /**
     * Prepares a URL from the XML customisation file to contain the user username and password
     *
     * @param url the url to prepare
     * @param username the user username
     * @param password the user password
     * @return the prepared url
     */
    public static String prepareUrl(String url, String username, String password){
        /* Encode the users username and password ready be used for a web request.  */

        String encodedUsername, encodedPassword;
        try {
            encodedUsername = URLEncoder.encode(username, "UTF-8");
            encodedPassword = URLEncoder.encode(password, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            encodedUsername = username;
            encodedPassword = password;
            e.printStackTrace();
        }
        return url.replace("%USERNAME%", encodedUsername).replace("%PASSWORD%", encodedPassword);
    }
}
