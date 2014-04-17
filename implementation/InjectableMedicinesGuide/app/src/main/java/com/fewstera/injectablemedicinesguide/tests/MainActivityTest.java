package com.fewstera.injectablemedicinesguide.tests;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.SmallTest;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.fewstera.injectablemedicinesguide.MainActivity;
import com.fewstera.injectablemedicinesguide.Preferences;
import com.fewstera.injectablemedicinesguide.R;
import com.fewstera.injectablemedicinesguide.database.DatabaseHelper;
import com.fewstera.injectablemedicinesguide.models.Drug;

/**
 * Created by fewstera on 17/04/2014.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private MainActivity _activity;
    private Context _context;
    private DatabaseHelper _db;
    private Drug _drug;
    private TextView _welcomeTextView, _lastUpdateText;
    private Button _browseButton, _calculatorButton, _updateButton;


    public MainActivityTest() {
        super(MainActivity.class);
    }

    public void setUp(){
        _context = getInstrumentation().getTargetContext().getApplicationContext();
        _activity = getActivity();

        /* Fetch the view elements */
        _welcomeTextView = (TextView) _activity.findViewById(R.id.welcome_text_view);
        _lastUpdateText = (TextView) _activity.findViewById(R.id.last_update_textview);

        _browseButton = (Button) _activity.findViewById(R.id.view_drugs_button);
        _calculatorButton = (Button) _activity.findViewById(R.id.calculator_button);
        _updateButton = (Button) _activity.findViewById(R.id.update_drugs_button);

    }

    @SmallTest
    public void testPreconditions() {
        /* Check that nothing needed is null */
        assertNotNull("_welcomeTextView is null", _welcomeTextView);
        assertNotNull("_lastUpdateText is null", _lastUpdateText);
        assertNotNull("_browseButton is null", _browseButton);
        assertNotNull("_calculatorButton is null", _calculatorButton);
        assertNotNull("_updateButton is null", _updateButton);
    }

    @SmallTest
    public void testWelcomeText() throws Exception{
        /* Check the welcome message is set correctly */
        String welcomeText = _activity.getResources().getString(R.string.welcome_text);
        assertEquals("Welcome text set incorrectly",
                Html.fromHtml(welcomeText).toString(), _welcomeTextView.getText().toString());

        /* Check its being displayed properly */
        final View decorView = _activity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(decorView, _welcomeTextView);

        final ViewGroup.LayoutParams layoutParams =
                _welcomeTextView.getLayoutParams();

        /* Check layout */
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.MATCH_PARENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);


    }

    @SmallTest
    public void testUpdateText() throws Exception{
        String updateDate = Preferences.getString(_context, Preferences.UPDATE_DATE_KEY, "01/01/1970");
        /* Check the title on the activity is set correctly */
        String updateText = String.format(_activity.getResources().getString(R.string.last_updated_text), updateDate);
        assertEquals("Update text set incorrectly",
                Html.fromHtml(updateText).toString(), _lastUpdateText.getText().toString());

        /* Check its being displayed properly */
        final View decorView = _activity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(decorView, _lastUpdateText);

        final ViewGroup.LayoutParams layoutParams =
                _lastUpdateText.getLayoutParams();

        /* Check layout */
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.MATCH_PARENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @SmallTest
    public void testBrowseButton(){
        /* Check its being displayed properly */
        final ViewGroup.LayoutParams layoutParams =
                _browseButton.getLayoutParams();

        /* Check layout */
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.MATCH_PARENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);
    }


    @SmallTest
    public void testUpdateButton(){
        /* Check its being displayed properly */
        final ViewGroup.LayoutParams layoutParams =
                _updateButton.getLayoutParams();

        /* Check layout */
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.MATCH_PARENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);
    }


    @SmallTest
    public void testCalculatorButton(){
        /* Check its being displayed properly */
        final ViewGroup.LayoutParams layoutParams =
                _calculatorButton.getLayoutParams();

        /* Check layout */
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.MATCH_PARENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);
    }

}