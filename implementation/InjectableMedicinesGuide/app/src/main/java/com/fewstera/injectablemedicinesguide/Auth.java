package com.fewstera.injectablemedicinesguide;

import android.content.Context;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.lang3.CharEncoding;
import org.w3c.dom.Document;
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
    public Boolean isValid() {
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

            return new Boolean(loginResult.equals("true"));

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
}
