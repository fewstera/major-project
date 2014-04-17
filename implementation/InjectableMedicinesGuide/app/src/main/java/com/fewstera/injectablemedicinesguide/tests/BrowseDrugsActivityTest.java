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
import android.widget.EditText;
import android.widget.ListView;


import com.fewstera.injectablemedicinesguide.BrowseDrugsActivity;
import com.fewstera.injectablemedicinesguide.MainActivity;
import com.fewstera.injectablemedicinesguide.R;
import com.fewstera.injectablemedicinesguide.ViewDrugActivity;
import com.fewstera.injectablemedicinesguide.database.DatabaseHelper;
import com.fewstera.injectablemedicinesguide.models.DrugIndex;

/**
 * Tests for the browse drugs activity
 *
 * @author Aidan Wynne Fewster
 * @version 1.0
 * @since 1.0
 */
public class BrowseDrugsActivityTest  extends ActivityInstrumentationTestCase2<BrowseDrugsActivity> {
    private BrowseDrugsActivity _activity;
    private Context _context;
    private DatabaseHelper _db;
    private ListView _list;
    private EditText _searchBox;
    private int _listSize;


    public BrowseDrugsActivityTest() {
        super(BrowseDrugsActivity.class);
    }

    public void populateDatabase(){
        _db.createDrugIndex(new DrugIndex(1, "Drug one"));
        _db.createDrugIndex(new DrugIndex(1, "Drug one extra"));
        _db.createDrugIndex(new DrugIndex(2, "Paracetamol"));
        _db.createDrugIndex(new DrugIndex(3, "Test drug"));
        _listSize = 4;
    }

    public void setUp(){
        _context = getInstrumentation().getTargetContext().getApplicationContext();
        _db = new DatabaseHelper(_context, "test");
        populateDatabase();

        Intent i = new Intent(_context, BrowseDrugsActivity.class);
        i.putExtra(MainActivity.EXTRA_TEST, true);
        setActivityIntent(i);
        _activity = getActivity();

        /* Fetch the third drug information */
        _list = (ListView) _activity.findViewById(R.id.list);
        _searchBox = (EditText) _activity.findViewById(R.id.search_text);

    }

    @SmallTest
    public void testPreconditions() {
        /* Check that nothing needed is null */
        assertNotNull("_list is null", _list);
        assertNotNull("_searchBox is null", _searchBox);
    }

    @MediumTest
    public void testSearchOnScreen() throws Exception{
        /* Check the search is on the screen */
        final View decorView = _activity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(decorView, _searchBox);

        final ViewGroup.LayoutParams layoutParams =
                _searchBox.getLayoutParams();

        /* Check layout */
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.MATCH_PARENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);

        /* Check the header contains the correct text */
        String searchHint = _activity.getResources().getString(R.string.search_hint);
        assertEquals("Search hint set incorrectly",
                searchHint, _searchBox.getHint().toString());
    }

    @MediumTest
    public void testListOnScreen() throws Exception{
        /* Check the download message is on the screen */
        final View decorView = _activity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(decorView, _list);

        final ViewGroup.LayoutParams layoutParams =
                _list.getLayoutParams();

        /* Check layout */
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.MATCH_PARENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.MATCH_PARENT);
    }

    @MediumTest
    public void testListSize() throws Exception{
        assertEquals("List not displaying all drug indexes",
                _listSize, _list.getCount());
    }

    public void tearDown() throws Exception{
        _db.truncateAll();
        super.tearDown();
    }
}