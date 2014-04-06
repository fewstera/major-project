package com.fewstera.injectablemedicinesguide;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fewstera.injectablemedicinesguide.dataDownload.DataProgress;
import com.fewstera.injectablemedicinesguide.dataDownload.DownloadDrugsRequest;
import com.fewstera.injectablemedicinesguide.dataDownload.DownloadIndexRequest;
import com.fewstera.injectablemedicinesguide.dataDownload.DownloadService;
import com.fewstera.injectablemedicinesguide.database.DatabaseHelper;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;
import com.octo.android.robospice.request.listener.RequestListener;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DownloadDataActivity extends Activity {

    private SpiceManager _spiceManager = new SpiceManager(DownloadService.class);
    private final char[] _letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    int _drugCount = 0;
    DataProgress _dataProgress;
    ProgressBar _progressBar;
    TextView _progressText;

    String _encodedUsername;
    String _encodedPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_download_data);

        _progressText = (TextView) findViewById(R.id.progressText);
        _progressBar = (ProgressBar) findViewById(R.id.downloadProgress);
        _progressBar.setMax(100);

        _dataProgress = DataProgress.getInstance();
        _dataProgress.reset();

        // Reset the download complete boolean so that if a data download has failed
        // the user isn't displayed with a partial database.
        Preferences.setDownloadComplete(this, false);
        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        db.truncateAll();

        try {
            _encodedUsername = URLEncoder.encode(Auth.getSavedUsername(this), "UTF-8");
            _encodedPassword = URLEncoder.encode(Auth.getSavedPassword(this), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            _encodedUsername = Auth.getSavedUsername(this);
            _encodedPassword = Auth.getSavedPassword(this);
            e.printStackTrace();
        }
        startDownload();

	}

    private void startDownload(){
        this.setProgressBarIndeterminateVisibility(true);
        _progressText.setText("Downloading drug index information");
        DownloadIndexRequest downloadRequest = new DownloadIndexRequest(getApplicationContext(), _encodedUsername, _encodedPassword);
        _spiceManager.execute(downloadRequest, "index_download", -1, new indexDownloadRequestListener());
    }

    private void startLetterDownloads(){
        updateDownloadLetterProgress();
        for (char letter : _letters) {
            startLetterDownload(letter);
        }
    }

    private void startLetterDownload(char letter) {
        DownloadDrugsRequest downloadRequest = new DownloadDrugsRequest(getApplicationContext(), _encodedUsername, _encodedPassword, letter);
        _spiceManager.execute(downloadRequest, "drug_download_" + letter, -1, new drugsDownloadRequestListener(letter));
    }


    private void updateDownloadLetterProgress(){
        _progressText.setText("Downloading (" + _dataProgress.getDrugList().size() + " of " + _drugCount + ")");
        _progressBar.setProgress(10 + Math.round((float)_dataProgress.getDrugList().size()/(float)_drugCount*90));

    }

    private void checkIfFinished() {
        Log.d("MyApplication", "CheckIfFinished: FinishedCount: " + _dataProgress.getFinishedCount()
                + ", ListSize: " + _dataProgress.getDrugList().size());
        if((_dataProgress.getFinishedCount()>=_letters.length) || (_dataProgress.getDrugList().size()>=_drugCount)){
            this.setProgressBarIndeterminateVisibility(false);
            if(_dataProgress.getSucceededLetters().size()<_letters.length){
                //Decrease the amount of finished services to allow the user to try again.
                _dataProgress.decreaseFinishedCountBy(_letters.length-_dataProgress.getSucceededLetters().size());
                onDownloadFail(false);
            }else{
                updateCompletePrefs();
                Log.d("MyApplication", "Finished");
                Intent intent = new Intent();
                intent.setClass(DownloadDataActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

        }
    }

    private void updateCompletePrefs() {
        //Set the download complete to complete.
        Preferences.setDownloadComplete(this, true);
        SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date dateNow = new Date();
        String dateString = dataFormat.format(dateNow);
        Preferences.setString(this, Preferences.UPDATE_DATE_KEY, dateString);
    }

    private void onDownloadFail(boolean index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Download failed, try again?");
        String message;
        if(index){
            message = "Failed to download drug index.\n\nWould you like to try again?";
            builder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    startDownload();
                }
            });
        }else{
            String failedLetterString = "";
            final ArrayList<Character> failedLetters = new ArrayList<Character>();
            for(char c : _letters){
                if(!_dataProgress.getSucceededLetters().contains(new Character(c))){
                    failedLetterString = failedLetterString + c + ", ";
                    failedLetters.add(new Character(c));
                }
            }
            failedLetterString = failedLetterString.substring(0 , failedLetterString.length() - 1);
            message = "Failed to download drugs starting with the letters " +
                    failedLetterString + "\n\nWould you like to try again?";

            builder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    for(Character failedLetter : failedLetters){
                        startLetterDownload(failedLetter.charValue());
                    }
                }
            });
        }
        builder.setMessage(message);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent();
                intent.setClass(DownloadDataActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private final class drugsDownloadRequestListener implements RequestListener<Drug[]>, PendingRequestListener<Drug[]> {

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
            updateDownloadLetterProgress();
            checkIfFinished();
        }

        public void onRequestNotFound() {}
    }
    private final class indexDownloadRequestListener implements RequestListener<Integer>, PendingRequestListener<Integer> {

        public indexDownloadRequestListener(){
            super();
        }

        public void onRequestFailure(SpiceException spiceException) {
            Log.d("MyApplication", "Failed downloading index");
            Toast.makeText(DownloadDataActivity.this, "Failed downloading index", Toast.LENGTH_SHORT).show();
            onDownloadFail(true);
        }

        public void onRequestSuccess(final Integer indexNo) {
            Log.d("MyApplication", "Downloaded index");
            Toast.makeText(DownloadDataActivity.this, "Downloaded drug index", Toast.LENGTH_SHORT).show();
            _drugCount = indexNo.intValue();
            startLetterDownloads();
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
        _spiceManager.start(this);
        if(_drugCount!=0){
            checkIfFinished();
            updateDownloadLetterProgress();
            for(char letter : _letters) {
                _spiceManager.addListenerIfPending(Drug[].class, "drug_download_" + letter, new drugsDownloadRequestListener(letter));

            }
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        _spiceManager.shouldStop();
        super.onStop();
    }

}
