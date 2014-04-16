package com.fewstera.injectablemedicinesguide.tests;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.test.InstrumentationTestCase;

import com.fewstera.injectablemedicinesguide.Auth;


/**
 * Created by fewstera on 16/04/2014.
 */
public class AuthTest extends InstrumentationTestCase {
    private Auth _auth;
    private Context _context;

    @Override
    public void setUp() throws Exception {
        _auth = new Auth();
        _context = getInstrumentation().getContext();
        super.setUp();
    }

    public void testSetCredentials(){
        try{
            _auth.setCredentials("test", "test");
        }catch(Exception e){
            fail("Setting user credentials threw an error");
        }

    }

    public void testIsValid() throws Exception {
        ConnectivityManager cm =
                (ConnectivityManager)_context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        _auth.setCredentials("ivgdemo", "bolus7");
        if(isConnected){
            assertTrue("Correct username and password not working", _auth.isValid(_context));

            _auth.setCredentials("ivgdemo", "BADPASSWORD");
            assertFalse("Invalid password returns true", _auth.isValid(_context));

            _auth.setCredentials("BADUSERNAME", "bolus7");
            assertFalse("Invalid username returns true", _auth.isValid(_context));
        }else{
            assertNull("Connection error should return null", _auth.isValid(_context));
        }
    }

    public void testSaveCredentials() throws Exception {

    }

    public void testIsLogged() throws Exception {

    }

    public void testGetSavedUsername() throws Exception {

    }

    public void testGetSavedPassword() throws Exception {

    }

    public void testLogout() throws Exception {

    }

    public void testPrepareUrl() throws Exception {

    }
}
