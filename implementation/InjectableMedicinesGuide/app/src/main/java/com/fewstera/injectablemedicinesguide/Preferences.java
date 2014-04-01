package com.fewstera.injectablemedicinesguide;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
    public static final String USERNAME_KEY = "_CRED_USERNAME_" ;
    public static final String PASSWORD_KEY = "_CRED_PASSWORD_" ;
    public static final String DOWNLOAD_COMPLETE = "_DOWNLOAD_COMPLETE_" ;

    public static void setString(Context context, String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.commit();
    }

    public static String getString(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            return sharedPreferences.getString(key, null);
        } catch (Exception e) {
             e.printStackTrace();
             return null;
        }
    }
    
    public static void delete(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }

    public static void setDownloadComplete(Context context, boolean value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(DOWNLOAD_COMPLETE, value);
        editor.commit();
    }

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
