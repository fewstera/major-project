package com.fewstera.injectablemedicinesguide.tests;

import android.content.Context;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fewstera.injectablemedicinesguide.Auth;
import com.fewstera.injectablemedicinesguide.DownloadDataActivity;
import com.fewstera.injectablemedicinesguide.MainActivity;
import com.fewstera.injectablemedicinesguide.R;
import com.fewstera.injectablemedicinesguide.ViewDrugActivity;


/**
 * Created by fewstera on 17/04/2014.
 */
public class DownloadDataActivityTest extends ActivityInstrumentationTestCase2<DownloadDataActivity> {
    private DownloadDataActivity _activity;
    private Context _context;
    private TextView _header, _downloadMessage, _progressMessage;
    private ProgressBar _progressBar;


    public DownloadDataActivityTest() {
        super(DownloadDataActivity.class);
    }

    public void setUp(){
        _context = getInstrumentation().getTargetContext().getApplicationContext();
        String username = _context.getResources().getString(R.string.test_username);
        String password = _context.getResources().getString(R.string.test_password);

        Auth auth = new Auth();
        auth.setCredentials(username, password);
        auth.saveCredentials(_context);

        Intent i = new Intent(_context, ViewDrugActivity.class);
        i.putExtra(MainActivity.EXTRA_TEST, true);
        setActivityIntent(i);
        _activity = getActivity();

        /* Fetch the third drug information */
        _header = (TextView) _activity.findViewById(R.id.downloading_header);
        _downloadMessage = (TextView) _activity.findViewById(R.id.downloading_message);
        _progressMessage = (TextView) _activity.findViewById(R.id.progressText);
        _progressBar = (ProgressBar) _activity.findViewById(R.id.downloadProgress);

    }

    @SmallTest
    public void testPreconditions() {
        /* Check that nothing needed is null */
        assertNotNull("_header is null", _header);
        assertNotNull("_downloadMessage is null", _downloadMessage);
        assertNotNull("_progressMessage is null", _progressMessage);
        assertNotNull("_progressBar is null", _progressBar);
    }

    @MediumTest
    public void testHeader() throws Exception{
        /* Check the header is on the screen */
        final View decorView = _activity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(decorView, _header);

        final ViewGroup.LayoutParams layoutParams =
                _header.getLayoutParams();

        /* Check layout */
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.WRAP_CONTENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);

        /* Check the header contains the correct text */
        String header = _activity.getResources().getString(R.string.downloading_header);
        assertEquals("Header set incorrectly",
                header, _header.getText().toString());
    }

    @MediumTest
    public void testMessage() throws Exception{
        /* Check the download message is on the screen */
        final View decorView = _activity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(decorView, _downloadMessage);

        final ViewGroup.LayoutParams layoutParams =
                _downloadMessage.getLayoutParams();

        /* Check layout */
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.WRAP_CONTENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);

        /* Check the message contains the correct text */
        String message = _activity.getResources().getString(R.string.downloading_message);
        assertEquals("Download message set incorrectly",
                message, _downloadMessage.getText().toString());
    }

    @MediumTest
    public void testProgressBar() throws Exception{
        /* Check the progress bar is on the screen */
        final View decorView = _activity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(decorView, _progressBar);

        final ViewGroup.LayoutParams layoutParams =
                _progressBar.getLayoutParams();

        /* Check layout */
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.MATCH_PARENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @MediumTest
    public void testProgressMessage() throws Exception{
        /* Check the download message is on the screen */
        final View decorView = _activity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(decorView, _progressMessage);

        final ViewGroup.LayoutParams layoutParams =
                _progressMessage.getLayoutParams();

        /* Check layout */
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.MATCH_PARENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public void tearDown() throws Exception{
        _activity.killServices();
        super.tearDown();
    }
}
