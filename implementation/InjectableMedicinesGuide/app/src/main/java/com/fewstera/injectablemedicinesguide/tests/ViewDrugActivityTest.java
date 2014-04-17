package com.fewstera.injectablemedicinesguide.tests;

import android.content.Context;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fewstera.injectablemedicinesguide.BrowseDrugsActivity;
import com.fewstera.injectablemedicinesguide.MainActivity;
import com.fewstera.injectablemedicinesguide.R;
import com.fewstera.injectablemedicinesguide.ViewDrugActivity;
import com.fewstera.injectablemedicinesguide.database.DatabaseHelper;
import com.fewstera.injectablemedicinesguide.models.Drug;
import com.fewstera.injectablemedicinesguide.models.DrugInformation;

/**
 * Created by fewstera on 16/04/2014.
 */
public class ViewDrugActivityTest  extends ActivityInstrumentationTestCase2<ViewDrugActivity> {
    private ViewDrugActivity _viewDrugActivity;
    private Context _context;
    private DatabaseHelper _db;
    private Drug _drug;
    private TextView _header, _drugInfoHeader1, _drugInfoText1, _drugInfoHeader3, _drugInfoText3;
    private ImageButton _drugInfoHelpButton1, _drugInfoHelpButton3;


    public ViewDrugActivityTest() {
        super(ViewDrugActivity.class);
    }

    public void setUp(){
        _context = getInstrumentation().getTargetContext().getApplicationContext();
        _db = new DatabaseHelper(_context, "test");
        _db.truncateAll();

        /* Create a new drug for the database and add it  */
        _drug = new Drug();
        _drug.setId(1);
        _drug.setName("Test Drug 1");
        _drug.addDrugInformation(new DrugInformation("Test Header", "Some help here", "Content"));
        _drug.addDrugInformation(new DrugInformation("TTEr", "Some sds here", "asd"));
        _drug.addDrugInformation(new DrugInformation("No3", null, "This is text to display"));
        _drug.addDrugInformation(new DrugInformation("TTEr", "Some sds here", "asd"));
        _db.createDrug(_drug);

        /* Setup the intent for the ViewDrug activity */
        Intent i = new Intent(_context, ViewDrugActivity.class);
        i.putExtra(MainActivity.EXTRA_TEST, true);
        i.putExtra(BrowseDrugsActivity.EXTRA_INDEX_NAME, "Test drug");
        i.putExtra(BrowseDrugsActivity.EXTRA_DRUG_ID, _drug.getId());
        setActivityIntent(i);
        _viewDrugActivity = getActivity();

        _header = (TextView) _viewDrugActivity.findViewById(R.id.drug_name_header);

        /* Fetch the ViewGroup containing all drug infos */
        ViewGroup infosContainer = (ViewGroup) (_viewDrugActivity.findViewById(R.id.drug_informations));

        /* Fetch the first drug information */
        _drugInfoHeader1 = (TextView) (infosContainer).getChildAt(1).findViewById(R.id.information_name);
        _drugInfoText1 = (TextView) (infosContainer).getChildAt(1).findViewById(R.id.information_content);
        _drugInfoHelpButton1 = (ImageButton) (infosContainer).getChildAt(1).findViewById(R.id.info_button);

        /* Fetch the third drug information */
        _drugInfoHeader3 = (TextView) (infosContainer).getChildAt(3).findViewById(R.id.information_name);
        _drugInfoText3 = (TextView) (infosContainer).getChildAt(3).findViewById(R.id.information_content);
        _drugInfoHelpButton3 = (ImageButton) (infosContainer).getChildAt(3).findViewById(R.id.info_button);

    }

    @SmallTest
    public void testPreconditions() {
        /* Check that nothing needed is null */
        assertNotNull("_viewDrugActivity is null", _viewDrugActivity);
        assertNotNull("_header is null", _header);
        assertNotNull("_drugInfoHeader1 is null", _drugInfoHeader1);
        assertNotNull("_drugInfoText1 is null", _drugInfoText1);
        assertNotNull("_drugInfoHelpButton1 is null", _drugInfoHelpButton1);
        assertNotNull("_drugInfoHeader3 is null", _drugInfoHeader3);
        assertNotNull("_drugInfoText3 is null", _drugInfoText3);
        assertNotNull("_drugInfoHelpButton3 is null", _drugInfoHelpButton3);
    }

    @SmallTest
    public void testTitle() throws Exception{
        /* Check the title on the activity is set correctly */
        String title = String.format(_viewDrugActivity.getResources().getString(R.string.title_activity_view_drug), "Test drug");
        assertEquals("Title set incorrectly",
                title, _viewDrugActivity.getTitle().toString());
    }

    @MediumTest
    public void testHeaderTextView() throws Exception{
        final View decorView = _viewDrugActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(decorView, _header);

        Log.d("Md", _db.getDrugFromId(1).getName());

        final ViewGroup.LayoutParams layoutParams =
                _header.getLayoutParams();

        /* Check layout */
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.WRAP_CONTENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);

        /* Check header value */
        assertEquals(_drug.getName(), _header.getText());
    }

    @MediumTest
    public void testDrugInfoHeaders() throws Exception{
        assertEquals("Drug information 1 header text has not been set correctly",
                "Test Header", _drugInfoHeader1.getText().toString());
        assertEquals("Drug information 3 header text has not been set correctly",
                "No3", _drugInfoHeader3.getText().toString());
    }

    @MediumTest
    public void testDrugInfoHelper() throws Exception{
        assertEquals("Drug information 1 header help button has not been shown",
                View.VISIBLE, _drugInfoHelpButton1.getVisibility());
        assertEquals("Drug information 3 header has not been set correctly",
                View.GONE, _drugInfoHelpButton3.getVisibility());
    }

    @MediumTest
    public void testDrugInfoContent() throws Exception{
        assertEquals("Drug information 1 content text is not set correctly",
                "Content", _drugInfoText1.getText().toString());
        assertEquals("Drug information 3 content text has not been set correctly",
                "This is text to display", _drugInfoText3.getText().toString());
    }

    public void tearDown() throws Exception{
        _db.close();
        _db.truncateAll();
        super.tearDown();
    }


}
