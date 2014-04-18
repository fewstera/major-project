package com.fewstera.injectablemedicinesguide.models.tests;

import com.fewstera.injectablemedicinesguide.models.DrugIndex;
import com.fewstera.injectablemedicinesguide.models.DrugInformation;

import junit.framework.TestCase;

/**
 * Created by fewstera on 17/04/2014.
 */
public class DrugInformationTest  extends TestCase {
    private DrugInformation _drugInfo;
    private int _drugId;
    private String _headerText, _headingHelp, _sectionText;

    public void setUp() throws Exception {
        super.setUp();
        _drugId = 123;
        _headerText = "Header text";
        _headingHelp = "Helping";
        _sectionText = "Section text";


        _drugInfo = new DrugInformation(_drugId, _headerText, _headingHelp, _sectionText);
    }

    public void testConstuct() throws Exception {
        assertNotNull("Drug info is null", _drugInfo);
    }

    public void testGetHeaderText() throws Exception {
        assertEquals("Getting header text failed, incorrect result",
                _headerText, _drugInfo.getHeaderText());
    }

    public void testGetHeaderHelp() throws Exception {
        assertEquals("Getting heading help failed, incorrect result",
                _headingHelp, _drugInfo.getHeaderHelper());
    }

    public void testGetSectionText() throws Exception {
        assertEquals("Getting section text failed, incorrect result",
                _sectionText, _drugInfo.getSectionText());
    }

}
