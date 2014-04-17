package com.fewstera.injectablemedicinesguide.tests;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.test.ActivityTestCase;
import android.test.RenamingDelegatingContext;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.fewstera.injectablemedicinesguide.Auth;
import com.fewstera.injectablemedicinesguide.R;


/**
 * Tests for the Auth class
 *
 * @author Aidan Wynne Fewster
 * @version 1.0
 * @since 1.0
 */
public class AuthTest extends ActivityTestCase {
    private Auth _auth;
    private Context _context;
    private String _correctUsername;
    private String _correctPassword;

    @Override
    public void setUp() throws Exception {
        _auth = new Auth();
        _context = new RenamingDelegatingContext(getInstrumentation().getTargetContext(), "_test");
        _correctUsername = _context.getResources().getString(R.string.test_username);
        _correctPassword = _context.getResources().getString(R.string.test_password);
        super.setUp();
    }

    @SmallTest
    public void testConstuct(){
        assertNotNull("Auth is null", _auth);
    }

    @SmallTest
    public void testSetCredentials(){
        try{
            _auth.setCredentials("test", "test");
        }catch(Exception e){
            fail("Setting user credentials threw an error");
        }

    }

    @MediumTest
    public void testIsValid() throws Exception {
        ConnectivityManager cm =
                (ConnectivityManager)_context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        _auth.setCredentials(_correctUsername, _correctPassword);
        if(isConnected){
            assertTrue("Correct username and password not working", _auth.isValid(_context));
            _auth.setCredentials(_correctUsername, "BADPASSWORD");


            assertFalse("Invalid password returns true", _auth.isValid(_context));

            _auth.setCredentials("BADUSERNAME", _correctPassword);
            assertFalse("Invalid username returns true", _auth.isValid(_context));

        }else{
            assertNull("Connection error should return null", _auth.isValid(_context));
        }
    }

    @SmallTest
    public void testSaveCredentialsAndGetters() {
        String username = "TEST_USERNAME";
        String password = "TEST_PASSWORD";

        _auth.setCredentials(username, password);
        _auth.saveCredentials(_context);

        assertEquals("Saved username does not match", Auth.getSavedUsername(_context), username);
        assertEquals("Saved password does not match", Auth.getSavedPassword(_context), password);
    }

    @SmallTest
    public void testIsLogged() throws Exception {
        _auth.logout(_context);
        assertFalse("User should be logged out", Auth.isLogged(_context));

        _auth.setCredentials(_correctUsername, _correctPassword);
        _auth.saveCredentials(_context);

        assertTrue("User should be logged in", Auth.isLogged(_context));


    }

    public void testLogout() throws Exception {
        _auth.logout(_context);
        assertFalse(Auth.isLogged(_context));
        assertNull(Auth.getSavedPassword(_context));
        assertNull(Auth.getSavedUsername(_context));
    }

    public void testPrepareUrl() throws Exception {
        String testUrl = "http://test.com?u=%USERNAME%&p=%PASSWORD%";

        /* test normal url */
        String encodedUrl = Auth.prepareUrl(testUrl, "Username", "Password");
        assertEquals(encodedUrl, "http://test.com?u=Username&p=Password");

        /* test url with encoded username */
        encodedUrl = Auth.prepareUrl(testUrl, "<>/@£$%^&", "Password");
        assertEquals(encodedUrl, "http://test.com?u=%3C%3E%2F%40%C2%A3%24%25%5E%26&p=Password");

        /* test url with encoded password */
        encodedUrl = Auth.prepareUrl(testUrl, "Username", "<>/@£$%^&");
        assertEquals(encodedUrl, "http://test.com?u=Username&p=%3C%3E%2F%40%C2%A3%24%25%5E%26");
    }
}
