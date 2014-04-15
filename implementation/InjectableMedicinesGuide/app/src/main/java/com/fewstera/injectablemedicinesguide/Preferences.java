package com.fewstera.injectablemedicinesguide;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
/**
 * Preferences class for the application
 *
 * This is class is responsible for interacting with the phones PreferenceManager. This is used
 * to store data locally for the user.
 *
 * @author Aidan Wynne Fewster
 * @version 1.0
 * @since 1.0
 */
public class Preferences {

    /* Preference keys */
    public static final String USERNAME_KEY = "_CRED_USERNAME_" ;
    public static final String PASSWORD_KEY = "_CRED_PASSWORD_" ;
    public static final String UPDATE_DATE_KEY = "_UPDATE_DATE_" ;
    public static final String DOWNLOAD_COMPLETE = "_DOWNLOAD_COMPLETE_" ;

    /**
     * Sets a string value in preferences for a given key
     *
     * @param context the application context
     * @param key the key
     * @param value the value
     */
    public static void setString(Context context, String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.commit();
    }

    /**
     * Retrieves the string for a given key
     *
     * @param context the application context
     * @param key the key
     * @param fallback if no value exists for that key, what to return
     * @return the value of the key, or the fallback
     */
    public static String getString(Context context, String key, String fallback) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            return sharedPreferences.getString(key, fallback);
        } catch (Exception e) {
            e.printStackTrace();
            return fallback;
        }
    }

    /**
     * Retrieves the string for a given key
     *
     * @param context the application context
     * @param key the key
     * @return the value of the key, or the fallback
     */
    public static String getString(Context context, String key) {
        return Preferences.getString(context, key, null);
    }

    /**
     * Deletes a given key from the prefences
     *
     * @param context the application context
     * @param key the key
     */
    public static void delete(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }

    /**
     * Sets the download complete preference
     *
     * @param context the application context
     * @param value if the download is complete (boolean)
     */
    public static void setDownloadComplete(Context context, boolean value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(DOWNLOAD_COMPLETE, value);
        editor.commit();
    }

    /**
     * Checks if the all data has been downloaded successfully
     *
     * @param context the application context
     * @return the result of the check
     */
    public static boolean getDownloadComplete(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            return sharedPreferences.getBoolean(DOWNLOAD_COMPLETE, false);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    } 
    
}
