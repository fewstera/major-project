package com.fewstera.injectablemedicinesguide.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fewstera.injectablemedicinesguide.models.Drug;
import com.fewstera.injectablemedicinesguide.models.DrugCalculatorInfo;
import com.fewstera.injectablemedicinesguide.models.DrugIndex;
import com.fewstera.injectablemedicinesguide.models.DrugInformation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fewstera on 24/03/2014.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    //Database Version and Name
    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "drugDatabase";

    //Table names
    private static final String TABLE_DRUGS = "drugs";
    private static final String TABLE_DRUG_INFOS = "drugInformations";
    private static final String TABLE_DRUG_INDEXS = "drugIndexs";
    private static final String TABLE_DRUG_CALCS = "drugCalcs";

    //Common column names
    private static final String KEY_ID = "id";
    private static final String F_KEY_DRUG_ID = "drug_id";

    //Drugs table columns
    private static final String KEY_NAME = "name";
    private static final String KEY_ROUTE = "route";
    private static final String KEY_TRADE_NAME = "trade_name";
    private static final String KEY_MEDICINE_NAME = "medicine_name";
    private static final String KEY_VERSION = "version";
    private static final String KEY_DATE_PUBLISHED = "date_published";

    //DrugInformations table columns
    private static final String KEY_HEADER_TEXT = "header_text";
    private static final String KEY_HEADER_HELPER = "header_helper";
    private static final String KEY_SECTION_TEXT = "key_section_text";

    //DrugCalcs table columns
    private static final String KEY_INFUSION_LABEL = "infusion_rate_label";
    private static final String KEY_INFUSION_UNITS = "infusion_rate_units";
    private static final String KEY_DOSE_UNITS = "dose_units";
    private static final String KEY_WEIGHT_REQ = "patient_weight_req";
    private static final String KEY_TIME_REQ = "time_req";
    private static final String KEY_FACTOR = "factor";
    private static final String KEY_CONCENTRATION_UNITS = "concentration_units";

    //DrugIndex table columns
    private static final String KEY_INDEX_NAME = "name";

    //Drug table creates
    private static final String CREATE_TABLE_DRUGS = "CREATE TABLE "
            + TABLE_DRUGS + "(" + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_NAME
            + " TEXT NOT NULL, " + KEY_ROUTE + " TEXT NOT NULL, "
            + KEY_TRADE_NAME + " TEXT NOT NULL, " + KEY_MEDICINE_NAME + " TEXT NOT NULL, "
            + KEY_VERSION + " TEXT NOT NULL, " + KEY_DATE_PUBLISHED + " DATE)";

    //DrugInformations table creates
    private static final String CREATE_TABLE_DRUG_INFORMATIONS = "CREATE TABLE "
            + TABLE_DRUG_INFOS + "(" + KEY_ID + " INTEGER PRIMARY KEY, "
            + F_KEY_DRUG_ID + " INTEGER NOT NULL, " + KEY_HEADER_TEXT
            + " TEXT NOT NULL, " + KEY_HEADER_HELPER + " TEXT, "
            + KEY_SECTION_TEXT + " TEXT NOT NULL)";

    //DrugIndex table creates
    private static final String CREATE_TABLE_DRUG_INDEX = "CREATE TABLE "
            + TABLE_DRUG_INDEXS + "(" + KEY_ID + " INTEGER PRIMARY KEY, "
            + F_KEY_DRUG_ID + " INTEGER NOT NULL, " + KEY_INDEX_NAME + " TEXT NOT NULL)";

    //DrugIndex table creates
    private static final String CREATE_TABLE_DRUG_CALCS = "CREATE TABLE "
            + TABLE_DRUG_CALCS + "(" + KEY_ID + " INTEGER PRIMARY KEY, "
            + F_KEY_DRUG_ID + " INTEGER NOT NULL, " + KEY_INFUSION_LABEL + " TEXT NOT NULL,"
            + KEY_INFUSION_UNITS + " TEXT NOT NULL, " + KEY_DOSE_UNITS + " TEXT NOT NULL,"
            + KEY_WEIGHT_REQ + " INTEGER NOT NULL, " + KEY_TIME_REQ + " INTEGER NOT NULL, "
            + KEY_FACTOR + " INTEGER, " + KEY_CONCENTRATION_UNITS + " TEXT NOT NULL)";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_DRUGS);
        db.execSQL(CREATE_TABLE_DRUG_INFORMATIONS);
        db.execSQL(CREATE_TABLE_DRUG_INDEX);
        db.execSQL(CREATE_TABLE_DRUG_CALCS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Delete old tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRUGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRUG_INFOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRUG_INDEXS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRUG_CALCS);

        //Create new ones
        onCreate(db);
    }

    public void truncateAll(){
        //Drop tables
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRUGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRUG_INFOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRUG_INDEXS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRUG_CALCS);

        //Create new ones
        onCreate(db);
    }

    /*
        *   Saves the given drug to the database, including its drugInformations
     */
    public long createDrug(Drug drug) {
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");

        ContentValues values = new ContentValues();
        values.put(KEY_ID, drug.getId());
        values.put(KEY_NAME, drug.getName());
        values.put(KEY_ROUTE, drug.getRoute());
        values.put(KEY_TRADE_NAME, drug.getTradeName());
        values.put(KEY_MEDICINE_NAME, drug.getMedicineName());
        values.put(KEY_VERSION, drug.getVersion());
        values.put(KEY_DATE_PUBLISHED, parser.format(drug.getDatePublished()));
        // insert row
        long drugId = db.insert(TABLE_DRUGS, null, values);

        // assigning drug informations to drug
        for (DrugInformation info : drug.getDrugInformations()) {
            createDrugInfo(drugId, info);
        }

        return drugId;
    }

    /*
        *   Saves the given drug information to the database
    */
    private long createDrugInfo(long drugId, DrugInformation info) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(F_KEY_DRUG_ID, drugId);
        values.put(KEY_HEADER_TEXT, info.getHeaderText());
        values.put(KEY_HEADER_HELPER, info.getHeaderHelper());
        values.put(KEY_SECTION_TEXT, info.getSectionText());

        return db.insert(TABLE_DRUG_INFOS, null, values);

    }
    /*
        *   Saves the given drug index to the database
    */
    public long createDrugIndex(DrugIndex index) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(F_KEY_DRUG_ID, index.getDrugId());
        values.put(KEY_INDEX_NAME, index.getName());

        return db.insert(TABLE_DRUG_INDEXS, null, values);

    }

    /*
        *   Saves the given drug index to the database
    */
    public long createDrugCalcInfo(DrugCalculatorInfo drugCalculatorInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(F_KEY_DRUG_ID, drugCalculatorInfo.getDrugId());
        values.put(KEY_INFUSION_LABEL, drugCalculatorInfo.getInfusionRateLabel());
        values.put(KEY_INFUSION_UNITS, drugCalculatorInfo.getInfusionRateUnits());
        values.put(KEY_DOSE_UNITS, drugCalculatorInfo.getDoseUnits());

        int weightReq = (drugCalculatorInfo.isPatientWeightRequired()) ? 1 : 0;
        values.put(KEY_WEIGHT_REQ, weightReq);

        int timeReq = (drugCalculatorInfo.isTimeRequired()) ? 1 : 0;
        values.put(KEY_TIME_REQ, timeReq);

        values.put(KEY_FACTOR, drugCalculatorInfo.getFactor());
        values.put(KEY_CONCENTRATION_UNITS, drugCalculatorInfo.getConcentrationUnits());

        return db.insert(TABLE_DRUG_CALCS, null, values);

    }

    public long getDrugsCount(){
        SQLiteDatabase db = this.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, TABLE_DRUGS);
    }

    public long getDrugIndexes(){
        SQLiteDatabase db = this.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, TABLE_DRUGS);
    }

    public List<Drug> getAllDrugsWithCalcs() {
        SQLiteDatabase db = this.getReadableDatabase();

        List<Drug> drugs = new ArrayList<Drug>();
        String[] drugCalcColumns = {DatabaseHelper.F_KEY_DRUG_ID};

        Cursor cursor = db.query(DatabaseHelper.TABLE_DRUG_CALCS,
                drugCalcColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Drug drug = getDrugFromId(cursor.getInt(0));
            if(drug!=null){ drugs.add(drug); }
            cursor.moveToNext();
        }
        cursor.close();
        return drugs;
    }


    public List<DrugIndex> getAllDrugIndexes() {
        SQLiteDatabase db = this.getReadableDatabase();

        List<DrugIndex> drugIndexes = new ArrayList<DrugIndex>();

        String[] drugIndexColumns = {DatabaseHelper.KEY_ID, DatabaseHelper.F_KEY_DRUG_ID, DatabaseHelper.KEY_INDEX_NAME};

        Cursor cursor = db.query(DatabaseHelper.TABLE_DRUG_INDEXS,
                drugIndexColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            drugIndexes.add(cursorToDrugIndex(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return drugIndexes;
    }

    private DrugIndex cursorToDrugIndex(Cursor cursor) {
        long dbId = cursor.getLong(0);
        int drugId = cursor.getInt(1);
        String drugName = cursor.getString(2);
        return new DrugIndex(dbId, drugId, drugName);
    }

    public Drug getDrugFromId(int id){
        Drug returnDrug;
        String[] drugColumns = {DatabaseHelper.KEY_ID, DatabaseHelper.KEY_NAME,
                DatabaseHelper.KEY_ROUTE, DatabaseHelper.KEY_TRADE_NAME, DatabaseHelper.KEY_MEDICINE_NAME,
                DatabaseHelper.KEY_VERSION, DatabaseHelper.KEY_DATE_PUBLISHED};

        SQLiteDatabase db = this.getReadableDatabase();
        String idString = String.valueOf(id);
        Cursor drugCursor = db.query(DatabaseHelper.TABLE_DRUGS,
                drugColumns, DatabaseHelper.KEY_ID + "=?", new String[] {idString}, null, null, null);
        drugCursor.moveToFirst();
        if(drugCursor.isAfterLast()){
            returnDrug = null;
        }else{
            returnDrug = cursorToDrug(drugCursor);
        }
        drugCursor.close();
        return returnDrug;
    }

    private Drug cursorToDrug(Cursor cursor) {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");

        Drug drug = new Drug();
        drug.setId(cursor.getInt(0));
        drug.setName(cursor.getString(1));
        drug.setRoute(cursor.getString(2));
        drug.setTradeName(cursor.getString(3));
        drug.setMedicineName(cursor.getString(4));
        drug.setVersion(cursor.getString(5));
        try {
            drug.setDatePublished(parser.parse(cursor.getString(6)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return drug;
    }

    public ArrayList<DrugInformation> getDrugInformationsFromDrugId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<DrugInformation> drugInfos = new ArrayList<DrugInformation>();

        String[] drugInfoColumns = {DatabaseHelper.KEY_ID, DatabaseHelper.KEY_HEADER_TEXT,
                DatabaseHelper.KEY_HEADER_HELPER, DatabaseHelper.KEY_SECTION_TEXT};

        String idString = String.valueOf(id);
        Cursor cursor = db.query(DatabaseHelper.TABLE_DRUG_INFOS,
                drugInfoColumns, DatabaseHelper.F_KEY_DRUG_ID + "=?", new String[] {idString}, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            drugInfos.add(cursorToDrugInfo(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return drugInfos;
    }

    private DrugInformation cursorToDrugInfo(Cursor cursor) {
        int id = cursor.getInt(0);
        String headerText = cursor.getString(1);
        String headerHelper = cursor.getString(2);
        String sectionText = cursor.getString(3);
        headerHelper = (headerHelper=="") ? null : headerHelper;

        return new DrugInformation(id, headerText, headerHelper, sectionText);
    }


    public DrugCalculatorInfo getDrugCalcInfoFromDrugId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        DrugCalculatorInfo calcInfo;

        String[] calcInfoColumns = {DatabaseHelper.F_KEY_DRUG_ID, DatabaseHelper.KEY_INFUSION_LABEL,
                DatabaseHelper.KEY_INFUSION_UNITS, DatabaseHelper.KEY_DOSE_UNITS,
                DatabaseHelper.KEY_WEIGHT_REQ, DatabaseHelper.KEY_TIME_REQ, DatabaseHelper.KEY_FACTOR,
                DatabaseHelper.KEY_CONCENTRATION_UNITS};

        String idString = String.valueOf(id);
        Cursor cursor = db.query(DatabaseHelper.TABLE_DRUG_CALCS,
                calcInfoColumns, DatabaseHelper.F_KEY_DRUG_ID + "=?", new String[] {idString}, null, null, null);

        cursor.moveToFirst();
        if(cursor.isAfterLast()) {
            calcInfo = null;
        }else{
            calcInfo = cursorToDrugCalculatorInfo(cursor);
        }
        cursor.close();
        return calcInfo;

    }

    private DrugCalculatorInfo cursorToDrugCalculatorInfo(Cursor cursor) {
        DrugCalculatorInfo calculatorInfo = new DrugCalculatorInfo();
        calculatorInfo.setDrugId(cursor.getInt(0));
        calculatorInfo.setInfusionRateLabel(cursor.getString(1));
        calculatorInfo.setInfusionRateUnits(cursor.getString(2));
        calculatorInfo.setDoseUnits(cursor.getString(3));
        boolean weightRequired = (cursor.getInt(4)==1);
        calculatorInfo.setPatientWeightRequired(weightRequired);
        boolean timeRequired = (cursor.getInt(5)==1);
        calculatorInfo.setTimeRequired(timeRequired);
        Integer factor = (cursor.isNull(6)) ? null : new Integer(cursor.getInt(6));
        calculatorInfo.setFactor(factor);
        calculatorInfo.setConcentrationUnits(cursor.getString(7));

        return calculatorInfo;
    }
}
