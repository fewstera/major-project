package com.fewstera.injectablemedicinesguide;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;

public class DownloadDataActivity extends Activity {

    private SpiceManager spiceManager = new SpiceManager(DownloadService.class);
    ArrayList<Drug> drugList = new ArrayList<Drug>();
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
        progressText.setText("Downloading monographs...");
        DownloadDrugsRequest downloadRequest = new DownloadDrugsRequest();
        spiceManager.execute(downloadRequest, "NO_CACHE", -1, new drugsDownloadRequestListener());
    }


    public final class drugsDownloadRequestListener implements RequestListener<Drug[]> {

        public void onRequestFailure(SpiceException spiceException) {
            Log.d("MyApplication", "Failed ");
            Toast.makeText(DownloadDataActivity.this, "Failed downloading drugs", Toast.LENGTH_SHORT).show();
        }

        public void onRequestSuccess(final Drug[] drugs) {
            Toast.makeText(DownloadDataActivity.this, "Downloaded  drugs", Toast.LENGTH_SHORT).show();
            for(Drug d: drugs){
                Log.d("MyApplication", d.getName() + " (" + d.getId() + ")");
                Log.d("MyApplication", "=====================================");
                DrugInformation info = d.getDrugInformations().get(2);
                Log.d("MyApplication", "===" + info.getHeaderText() + "===\n");
                Log.d("MyApplication", info.getSectionText() + "");
            }
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
