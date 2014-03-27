package com.fewstera.injectablemedicinesguide.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.fewstera.injectablemedicinesguide.*;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fewstera on 24/03/2014.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    //Database Version and Name
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "drugDatabase";

    //Table names
    private static final String TABLE_DRUGS = "drugs";
    private static final String TABLE_DRUG_INFOS = "drugInformations";

    //Common column names
    private static final String KEY_ID = "id";

    //Drugs table columns
    private static final String KEY_NAME = "name";
    private static final String KEY_ROUTE = "route";
    private static final String KEY_TRADE_NAME = "trade_name";
    private static final String KEY_MEDICINE_NAME = "medicine_name";
    private static final String KEY_VERSION = "version";
    private static final String KEY_DATE_PUBLISHED = "date_published";

    //DrugInformations table columns
    private static final String F_KEY_DRUG_ID = "drug_id";
    private static final String KEY_HEADER_TEXT = "header_text";
    private static final String KEY_HEADER_HELPER = "header_helper";
    private static final String KEY_SECTION_TEXT = "key_section_text";


    //Drug table creates
    private static final String CREATE_TABLE_DRUGS = "CREATE TABLE "
            + TABLE_DRUGS + "(" + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_NAME
            + " TEXT NOT NULL, " + KEY_ROUTE + " TEXT NOT NULL, "
            + KEY_TRADE_NAME + " TEXT NOT NULL, " + KEY_MEDICINE_NAME + " TEXT NOT NULL, "
            + KEY_VERSION + " TEXT NOT NULL, " + KEY_DATE_PUBLISHED + " DATETIME)";

    //DrugInformations table creates
    private static final String CREATE_TABLE_DRUG_INFORMATIONS = "CREATE TABLE "
            + TABLE_DRUG_INFOS + "(" + KEY_ID + " INTEGER PRIMARY KEY, "
            + F_KEY_DRUG_ID + " INTEGER NOT NULL, " + KEY_HEADER_TEXT
            + " TEXT NOT NULL, " + KEY_HEADER_HELPER + " TEXT, "
            + KEY_SECTION_TEXT + " TEXT NOT NULL)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_DRUGS);
        db.execSQL(CREATE_TABLE_DRUG_INFORMATIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Delete old tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRUGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRUG_INFOS);

        //Create new ones
        onCreate(db);
    }

    /*
        *   Saves the given drug to the database, including its drugInformations
     */
    public long createDrug(Drug drug) {
        Log.d("MyApplication", "Saving drug: " + drug.getName() + "(" + drug.getId() + ")");

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

    public void truncateAll(){
        //Drop tables
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRUGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRUG_INFOS);

        //Create new ones
        onCreate(db);
    }

    public long getDrugsCount(){
        SQLiteDatabase db = this.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, TABLE_DRUGS);
    }


}
