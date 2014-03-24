package com.fewstera.injectablemedicinesguide;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.Collections;

public class DownloadDataActivity extends Activity {

    private SpiceManager _spiceManager = new SpiceManager(DownloadService.class);
    int _drugCount;
    ProgressBar _progressBar;
    TextView _progressText;
    ArrayList<Character> _failedLetters = new ArrayList<Character>();
    ArrayList<Drug> _drugList = new ArrayList<Drug>();
    int _finishedCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download_data);

        _progressText = (TextView) findViewById(R.id.progressText);
        _progressBar = (ProgressBar) findViewById(R.id.downloadProgress);
        _progressBar.setMax(100);
        _drugCount = getIntent().getIntExtra("num_of_drugs", 100);

        startDownloads();

	}

    private void startDownloads(){
        updateDownloadProgress();
        char[] letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        for(char letter : letters) {
            Log.d("MyApplication", "Downloading letter: " + letter);
            DownloadDrugsRequest downloadRequest = new DownloadDrugsRequest(letter);
            _spiceManager.execute(downloadRequest, "drug_download_" + letter, -1, new drugsDownloadRequestListener(letter));
        }

    }

    private void updateDownloadProgress(){
        _progressText.setText("Downloading (" + _drugList.size() + " of " + _drugCount + ")");
        _progressBar.setProgress((int) Math.round((float)_drugList.size()/(float)_drugCount*90));

    }

    private void updateFinished(){
        _finishedCount++;
        if((_finishedCount>=26) || (_drugList.size()>=_drugCount)){
            Log.d("MyApplication", "=====Finished, got drugs:===");
            Collections.sort(_drugList);
            for(Drug d : _drugList){
                Log.d("MyApplication", d.getName() + " (" + d.getId() + ")");
                Log.d("MyApplication", "SECTION HEADER: " + d.getDrugInformations().get(4));
            }
            Log.d("MyApplication", "=====Failed letters:===");
            for(char c : _failedLetters){
                Log.d("MyApplication", "FAILED:" + c);
            }
        }

    }

    public final class drugsDownloadRequestListener implements RequestListener<Drug[]> {

        private char _letter;

        public drugsDownloadRequestListener(char letter){
            super();
            _letter = letter;
        }

        public void onRequestFailure(SpiceException spiceException) {
            Log.d("MyApplication", "Failed: " + _letter);
            Toast.makeText(DownloadDataActivity.this, "Failed downloading " + _letter + "... drugs", Toast.LENGTH_SHORT).show();
            _failedLetters.add(new Character(_letter));
            updateFinished();
        }

        public void onRequestSuccess(final Drug[] drugs) {
            Log.d("MyApplication", "Downloaded: " + _letter);
            Toast.makeText(DownloadDataActivity.this, "Downloaded " + _letter + "... drugs", Toast.LENGTH_SHORT).show();
            for(Drug d: drugs){
                _drugList.add(d);
            }
            updateDownloadProgress();
            updateFinished();
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
        _spiceManager.start(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        _spiceManager.shouldStop();
        super.onStop();
    }

}
