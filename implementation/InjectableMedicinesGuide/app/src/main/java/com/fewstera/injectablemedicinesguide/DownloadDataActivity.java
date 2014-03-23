package com.fewstera.injectablemedicinesguide;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.SpiceService;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.simple.SimpleTextRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import roboguice.util.temp.Ln;

public class DownloadDataActivity extends Activity {

    private SpiceManager spiceManager = new SpiceManager(DownloadService.class);
    ArrayList<Drug> drugList = new ArrayList<Drug>();
    LinkedList<Character> lettersRemaining;
    TextView progressText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download_data);

        progressText = (TextView) findViewById(R.id.progressText);

        Log.d("MyApplication", "Starting");

        startDownload();

	}

    private void startDownload(){
        char[] letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        lettersRemaining = new LinkedList<Character>();
        for(char letter : letters) {
            lettersRemaining.add(letter);
        }
        downloadNext();
    }

    private void downloadNext(){
        if(lettersRemaining.size()>0){
            Character nextLetter = lettersRemaining.removeFirst();
            downloadLetter(nextLetter);
        }
    }

    private void downloadLetter(Character letter){
        Log.d("MyApplication", "Downlaoding next");
        progressText.setText("Downloading monographs starting with " + letter + "...");
        DownloadDrugsRequest downloadRequest = new DownloadDrugsRequest("http://www.injguide.nhs.uk/IMGDrugData.asp?username=ivgdemo&password=bolus7&Part=" + letter);
        spiceManager.execute(downloadRequest, "NO_CACHE", -1, new drugsDownloadRequestListener(letter));
    }

    public final class drugsDownloadRequestListener implements RequestListener<Drug[]> {
        private Character _letter;

        public drugsDownloadRequestListener(Character letter){
            super();
            _letter = letter;
        }
        public void onRequestFailure(SpiceException spiceException) {
            System.out.println("Failed ");

            Toast.makeText(DownloadDataActivity.this, "Failed downloading " + _letter + "... drugs", Toast.LENGTH_SHORT).show();
        }

        public void onRequestSuccess(final Drug[] drugs) {
            Toast.makeText(DownloadDataActivity.this, "Downloaded " + _letter + "... drugs", Toast.LENGTH_SHORT).show();
            for(Drug d: drugs){
                Log.d("MyApplication", d.getName() + " (" + d.getId() + ")");
                Log.d("MyApplication", "=====================================");
                DrugInformation info = d.getDrugInformations().get(2);
                Log.d("MyApplication", "===" + info.getHeaderText() + "===\n");
                Log.d("MyApplication", info.getSectionText() + "");
            }
            downloadNext();
        }
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.download_data, menu);
		return true;
	}

    @Override
    protected void onStart() {
        spiceManager.start(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

}
