package com.fewstera.injectablemedicinesguide.models.tests;

import com.fewstera.injectablemedicinesguide.models.Drug;
import com.fewstera.injectablemedicinesguide.models.DrugInformation;

import junit.framework.TestCase;

/**
 * Created by fewstera on 17/04/2014.
 */
public class DrugTest extends TestCase {
    private Drug _drug;

    public void setUp() throws Exception {
        super.setUp();
        _drug = new Drug();
    }

    public void testConstuct() throws Exception {
        assertNotNull("Drug is null", _drug);
    }

    public void testGetAndSetId() throws Exception {
        int testId = 123;
        _drug.setId(testId);

        assertEquals("Setting and getting id failed",
                testId, _drug.getId());
    }

    public void testGetAndSetName() throws Exception {
        String testName = "Drug name";
        _drug.setName(testName);

        assertEquals("Setting and getting name failed",
                testName, _drug.getName());
    }

    public void testEmptyDrugInfos() throws Exception {
        assertEquals("Drug informations is not empty",
                0, _drug.getDrugInformations().size());
    }

    public void testAddAndGetDrugInformation() throws Exception {
        _drug.addDrugInformation(new DrugInformation("Test", null, "Text"));
        assertEquals("Drug informations does not contain the correct amount of infos",
                1, _drug.getDrugInformations().size());
        _drug.addDrugInformation(new DrugInformation("New", "Hello", "Extra"));

        assertEquals("Drug informations does not contain the correct amount of infos",
                2, _drug.getDrugInformations().size());

        assertEquals("Drug information does not contain the correct text",
                "Text", _drug.getDrugInformations().get(0).getSectionText());
    }

    public void testToString() throws Exception {
        _drug.setName("Test");
        assertEquals("Drug to string should reutnr drug name",
                _drug.getName(), _drug.toString());
    }
}
