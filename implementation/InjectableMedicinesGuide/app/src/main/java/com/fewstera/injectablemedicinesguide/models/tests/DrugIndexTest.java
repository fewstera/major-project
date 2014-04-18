package com.fewstera.injectablemedicinesguide.models.tests;

import com.fewstera.injectablemedicinesguide.models.Drug;
import com.fewstera.injectablemedicinesguide.models.DrugIndex;

import junit.framework.TestCase;

/**
 * Created by fewstera on 17/04/2014.
 */
public class DrugIndexTest extends TestCase {
    private DrugIndex _drugIndex;
    private int _id;
    private String _name;

    public void setUp() throws Exception {
        super.setUp();
        _id = 123;
        _name = "Index name";

        _drugIndex = new DrugIndex(_id, _name);
    }

    public void testConstuct() throws Exception {
        assertNotNull("Drug index is null", _drugIndex);
    }

    public void testGetId() throws Exception {
        assertEquals("Getting id failed, incorrect result",
                _id, _drugIndex.getDrugId());
    }

    public void testGetName() throws Exception {
        assertEquals("Getting name failed, incorrect result",
                _name, _drugIndex.getName());
    }

}
