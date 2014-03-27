package com.fewstera.injectablemedicinesguide;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fewstera.injectablemedicinesguide.database.DatabaseHelper;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;

public class DownloadDataActivity extends Activity {

    private SpiceManager _spiceManager = new SpiceManager(DownloadService.class);
    private final char[] _letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    int _drugCount;
    DataProgress _dataProgress;
    ProgressBar _progressBar;
    TextView _progressText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_download_data);

        _progressText = (TextView) findViewById(R.id.progressText);
        _progressBar = (ProgressBar) findViewById(R.id.downloadProgress);
        _progressBar.setMax(100);
        _drugCount = getIntent().getIntExtra("num_of_drugs", 100);

        _dataProgress = DataProgress.getInstance();
        _dataProgress.reset();

        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        db.truncateAll();

        startDownloads();

	}

    private void startDownloads(){
        this.setProgressBarIndeterminateVisibility(true);
        updateDownloadProgress();
        for(char letter : _letters) {
            DownloadDrugsRequest downloadRequest = new DownloadDrugsRequest(getApplicationContext(), letter);
            _spiceManager.execute(downloadRequest, "drug_download_" + letter, -1, new drugsDownloadRequestListener(letter));
        }

    }

    private void updateDownloadProgress(){
        _progressText.setText("Downloading (" + _dataProgress.getDrugList().size() + " of " + _drugCount + ")");
        _progressBar.setProgress((int) Math.round((float)_dataProgress.getDrugList().size()/(float)_drugCount*100));

    }

    private void checkIfFinished() {
        Log.d("MyApplication", "CheckIfFinished: FinishedCount: " + _dataProgress.getFinishedCount()
                + ", ListSize: " + _dataProgress.getDrugList().size());
        if((_dataProgress.getFinishedCount()>=_letters.length) || (_dataProgress.getDrugList().size()>=_drugCount)){
            this.setProgressBarIndeterminateVisibility(false);
            if(_dataProgress.getSucceededLetters().size()<_letters.length){
                Log.d("MyApplication", "=====Failed letters:===");
                for(char c : _letters){
                    if(!_dataProgress.getSucceededLetters().contains(new Character(c))){
                        Log.d("MyApplication", "FAILED:" + c);
                    }
                }
            }else{
                Log.d("MyApplication", "Finished");
                Intent intent = new Intent();
                intent.setClass(DownloadDataActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

        }
    }

    public final class drugsDownloadRequestListener implements RequestListener<Drug[]>, PendingRequestListener<Drug[]> {

        private char _letter;

        public drugsDownloadRequestListener(char letter){
            super();
            _letter = letter;
        }

        public void onRequestFailure(SpiceException spiceException) {
            Log.d("MyApplication", "Failed: " + _letter);
            Toast.makeText(DownloadDataActivity.this, "Failed downloading " + _letter + "... drugs", Toast.LENGTH_SHORT).show();
            checkIfFinished();
        }

        public void onRequestSuccess(final Drug[] drugs) {
            Log.d("MyApplication", "Downloaded: " + _letter);
            Toast.makeText(DownloadDataActivity.this, "Downloaded " + _letter + "... drugs", Toast.LENGTH_SHORT).show();
            updateDownloadProgress();
            checkIfFinished();
        }

        public void onRequestNotFound() {}


    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.download_data, menu);
		return true;
	}

    @Override
    protected void onStart() {
        checkIfFinished();
        updateDownloadProgress();
        _spiceManager.start(this);
        for(char letter : _letters) {
            _spiceManager.addListenerIfPending(Drug[].class, "drug_download_" + letter, new drugsDownloadRequestListener(letter));

        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        _spiceManager.shouldStop();
        super.onStop();
    }

}
