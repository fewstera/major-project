package com.fewstera.injectablemedicinesguide.tests;

import android.content.Context;
import android.test.ActivityTestCase;
import android.test.RenamingDelegatingContext;
import android.test.suitebuilder.annotation.SmallTest;

import com.fewstera.injectablemedicinesguide.Preferences;


/**
 * Created by fewstera on 16/04/2014.
 */
public class PreferencesTest  extends ActivityTestCase {
    private Context _context;
    private String _correctUsername = "ivgdemo";
    private String _correctPassword = "bolus7";

    @Override
    public void setUp() throws Exception {
        _context = new RenamingDelegatingContext(getInstrumentation().getTargetContext(), "_test");

        super.setUp();
    }

    @SmallTest
    public void testSetAndGetString(){
        String testKey = "TEST_KEY";
        String stringToTest = "SaveThisString";
        Preferences.setString(_context, testKey, stringToTest);

        String prefString = Preferences.getString(_context, testKey);

        assertEquals("Set string is not the same as the one retrieved",
                stringToTest, prefString);

        /* Testing fallbacks */

        assertNull("Invalid key should return null",
                Preferences.getString(_context, "NON_KEY"));

        assertEquals("Invalid key should return fallback",
                Preferences.getString(_context, "NON_KEY", "Failed"), "Failed");

    }

    @SmallTest
    public void testDelete(){
        String testKey = "TEST_KEY";
        String stringToTest = "SaveThisString";
        Preferences.setString(_context, testKey, stringToTest);
        Preferences.delete(_context, testKey);

        assertNull("Should return null after deleted key",
                Preferences.getString(_context, testKey));
    }

    @SmallTest
    public void testDownloadCompleteBool(){
        Preferences.setDownloadComplete(_context, false);

        assertFalse("Download Complete should be false",
                Preferences.getDownloadComplete(_context));

        Preferences.setDownloadComplete(_context, true);

        assertTrue("Download Complete should be true",
                Preferences.getDownloadComplete(_context));

    }

}
